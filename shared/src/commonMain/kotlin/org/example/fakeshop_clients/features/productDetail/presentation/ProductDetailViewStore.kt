package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.bdui.domain.BduiActionService
import org.example.fakeshop_clients.features.bdui.domain.BduiMutationApplier
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.buildPdpBindData
import org.example.fakeshop_clients.features.bdui.domain.models.BduiMutation
import org.example.fakeshop_clients.features.bdui.domain.models.BindData
import org.example.fakeshop_clients.features.bdui.domain.models.ToastSeverity
import org.example.fakeshop_clients.features.bdui.presentation.BduiError
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper

class ProductDetailViewStore(
    private val scope: CoroutineScope,
    private val productDetailService: ProductDetailService,
    private val bduiTemplateService: BduiTemplateService,
    private val bduiActionService: BduiActionService,
    private val favoritesService: FavoritesService,
    private val briefProductMapper: DomainToPresentationBriefProductMapper,
    private val sessionObserver: SessionObserver
) {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ProductDetailEffect>(extraBufferCapacity = 8)
    val effects: SharedFlow<ProductDetailEffect> = _effects.asSharedFlow()

    private var currentProductId: String? = null

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(event.productId)
            ProductDetailEvent.Retry -> retry()
            ProductDetailEvent.ToggleFavorite -> toggleFavorite()
            is ProductDetailEvent.DispatchAction -> dispatchAction(
                event.actionId, event.context, event.idempotencyKey
            )
        }
    }

    private fun loadProduct(productId: String) {
        currentProductId = productId
        _state.update {
            it.copy(
                briefState = BriefProductState.Loading,
                bduiBodyState = BduiBodyState.Loading,
                galleryUrls = emptyList(),
                isFavorited = false
            )
        }

        scope.launch {
            coroutineScope {
                launch { loadBriefAndBdui(productId) }
                launch {
                    val isFav = isFavorite(productId)
                    _state.update { it.copy(isFavorited = isFav) }
                }
            }
        }
    }

    private suspend fun isFavorite(productId: String): Boolean {
        if (productId in favoritesService.favoritedIds.value) return true
        return favoritesService.checkFavorite(productId).fold(
            onSuccess = { it },
            onError = { false }
        )
    }

    /**
     * Drives briefState (top half), galleryUrls (top-half carousel) and bduiBodyState (bottom half).
     * brief + detailed fan out in parallel; the template fetch chains off brief.category.
     */
    private suspend fun loadBriefAndBdui(productId: String) = coroutineScope {
        val briefDef = async { productDetailService.getBriefProductById(productId) }
        val detailedDef = async { productDetailService.getDetailedProductById(productId) }

        val briefRes = briefDef.await()
        briefRes.fold(
            onSuccess = { brief ->
                _state.update {
                    it.copy(briefState = BriefProductState.Success(briefProductMapper.map(brief)))
                }
            },
            onError = { networkError ->
                _state.update {
                    it.copy(
                        briefState = BriefProductState.Error(
                            ProductDetailError.Network(
                                networkError
                            )
                        )
                    )
                }
            }
        )

        if (briefRes is Result.Error) {
            setBduiError(BduiError.Network(briefRes.error))
            val detailedRes = detailedDef.await()
            if (detailedRes is Result.Success) {
                _state.update { it.copy(galleryUrls = detailedRes.data.galleryUrls) }
            }
            return@coroutineScope
        }

        val brief = (briefRes as Result.Success).data
        val templateDef = async { bduiTemplateService.getPdpTemplate(brief.category) }

        val detailedRes = detailedDef.await()
        if (detailedRes is Result.Success) {
            _state.update { it.copy(galleryUrls = detailedRes.data.galleryUrls) }
        }

        val templateRes = templateDef.await()

        when {
            detailedRes is Result.Error -> setBduiError(BduiError.Network(detailedRes.error))
            templateRes is Result.Error -> setBduiError(BduiError.Network(templateRes.error))
            detailedRes is Result.Success && templateRes is Result.Success -> {
                val bindData = BindData(
                    buildPdpBindData(
                        briefProductMapper.map(brief),
                        detailedRes.data
                    )
                )
                _state.update {
                    it.copy(bduiBodyState = BduiBodyState.Ready(templateRes.data, bindData))
                }
            }
        }
    }

    private fun setBduiError(error: BduiError) {
        _state.update { it.copy(bduiBodyState = BduiBodyState.Error(error)) }
    }

    private fun toggleFavorite() {
        if (sessionObserver.upgradeInProgress.value) return
        val productId = currentProductId ?: return
        val currentlyFavorited = _state.value.isFavorited
        _state.update { it.copy(isFavoriteLoading = true) }

        scope.launch {
            favoritesService.toggleFavorite(productId, currentlyFavorited).fold(
                onSuccess = {
                    _state.update { it.copy(isFavoriteLoading = false) }
                },
                onError = {
                    _state.update { it.copy(isFavoriteLoading = false) }
                }
            )
        }
    }

    private fun dispatchAction(
        actionId: String,
        context: JsonObject,
        idempotencyKey: String?
    ) {
        val ready = _state.value.bduiBodyState as? BduiBodyState.Ready ?: return
        scope.launch {
            bduiActionService.dispatch(
                actionId = actionId,
                screen = ready.template.screen,
                templateId = null,
                context = context,
                idempotencyKey = idempotencyKey
            ).fold(
                onSuccess = { response ->
                    response.mutations.forEach { applyMutation(it) }
                },
                onError = {
                    _effects.emit(
                        ProductDetailEffect.ShowToast(
                            message = "Action failed. Please try again.",
                            severity = ToastSeverity.error
                        )
                    )
                }
            )
        }
    }

    private suspend fun applyMutation(mutation: BduiMutation) {
        when (mutation) {
            is BduiMutation.UpdateBindData -> {
                _state.update { state ->
                    val ready = state.bduiBodyState as? BduiBodyState.Ready ?: return@update state
                    state.copy(
                        bduiBodyState = ready.copy(
                            bindData = BindData(
                                BduiMutationApplier.applyBindPatch(
                                    ready.bindData.json,
                                    mutation.patch
                                )
                            )
                        )
                    )
                }
            }

            is BduiMutation.ReplaceSlot -> {
                _state.update { state ->
                    val ready = state.bduiBodyState as? BduiBodyState.Ready ?: return@update state
                    state.copy(
                        bduiBodyState = ready.copy(
                            template = ready.template.copy(
                                root = BduiMutationApplier.applyReplaceSlot(
                                    ready.template.root,
                                    mutation
                                )
                            )
                        )
                    )
                }
            }

            is BduiMutation.Navigate -> {
                _effects.emit(ProductDetailEffect.Navigate(mutation.url, mutation.replace))
            }

            is BduiMutation.ShowToast -> {
                _effects.emit(ProductDetailEffect.ShowToast(mutation.message, mutation.severity))
            }
        }
    }

    private fun retry() {
        currentProductId?.let { loadProduct(it) }
    }
}
