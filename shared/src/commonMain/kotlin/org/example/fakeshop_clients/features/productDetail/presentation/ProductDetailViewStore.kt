package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.buildPdpBindData
import org.example.fakeshop_clients.features.bdui.presentation.BduiError
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper

class ProductDetailViewStore(
    private val scope: CoroutineScope,
    private val productDetailService: ProductDetailService,
    private val bduiTemplateService: BduiTemplateService,
    private val favoritesService: FavoritesService,
    private val briefProductMapper: DomainToPresentationBriefProductMapper,
    private val sessionObserver: SessionObserver
) {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private var currentProductId: String? = null

    init {
        scope.launch {
            favoritesService.favoritedIds.collect { ids ->
                val productId = currentProductId ?: return@collect
                _state.update { it.copy(isFavorited = productId in ids) }
            }
        }
    }

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(event.productId)
            ProductDetailEvent.Retry -> retry()
            ProductDetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun loadProduct(productId: String) {
        currentProductId = productId
        _state.update {
            it.copy(
                briefState = BriefProductState.Loading,
                bduiBodyState = BduiBodyState.Loading,
                galleryUrls = emptyList(),
                isFavorited = productId in favoritesService.favoritedIds.value
            )
        }

        scope.launch {
            coroutineScope {
                launch { loadBriefAndBdui(productId) }
                launch { favoritesService.checkFavorite(productId) }
            }
        }
    }

    /**
     * Drives briefState (top half), galleryUrls (top-half carousel) and bduiBodyState (bottom half).
     * brief + detailed fan out in parallel; the template fetch chains off brief.category.
     */
    private suspend fun loadBriefAndBdui(productId: String) = coroutineScope {
        val briefDef = async { productDetailService.getBriefProductById(productId) }
        val detailedDef = async { productDetailService.getDetailedProductById(productId) }
        val briefRes = briefDef.await()
        val detailedRes = detailedDef.await()

        briefRes.fold(
            onSuccess = { brief ->
                _state.update {
                    it.copy(briefState = BriefProductState.Success(briefProductMapper.map(brief)))
                }
            },
            onError = { networkError ->
                _state.update {
                    it.copy(briefState = BriefProductState.Error(ProductDetailError.Network(networkError)))
                }
            }
        )

        if (detailedRes is Result.Success) {
            _state.update { it.copy(galleryUrls = detailedRes.data.galleryUrls) }
        }

        when {
            briefRes is Result.Error -> setBduiError(BduiError.Network(briefRes.error))
            detailedRes is Result.Error -> setBduiError(BduiError.Network(detailedRes.error))
            briefRes is Result.Success && detailedRes is Result.Success -> {
                val brief = briefRes.data
                val detailed = detailedRes.data
                bduiTemplateService.getPdpTemplate(brief.category).fold(
                    onSuccess = { template ->
                        val bindData = buildPdpBindData(briefProductMapper.map(brief), detailed)
                        _state.update {
                            it.copy(bduiBodyState = BduiBodyState.Ready(template, bindData))
                        }
                    },
                    onError = { networkError ->
                        setBduiError(BduiError.Network(networkError))
                    }
                )
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

    private fun retry() {
        currentProductId?.let { loadProduct(it) }
    }
}
