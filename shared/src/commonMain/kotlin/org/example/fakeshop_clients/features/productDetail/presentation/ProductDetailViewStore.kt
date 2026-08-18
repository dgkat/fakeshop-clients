package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.core.navigation.AppRouteParser
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.BduiActionService
import org.example.fakeshop_clients.features.bdui.domain.BduiMutationApplier
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.ReplaceService
import org.example.fakeshop_clients.features.bdui.domain.buildPdpBindData
import org.example.fakeshop_clients.features.bdui.domain.models.ActionContext
import org.example.fakeshop_clients.features.bdui.domain.models.BduiMutation
import org.example.fakeshop_clients.features.bdui.domain.models.BindData
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ToastSeverity
import org.example.fakeshop_clients.features.bdui.presentation.BduiError
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsService

class ProductDetailViewStore(
    private val scope: CoroutineScope,
    private val productDetailService: ProductDetailService,
    private val bduiTemplateService: BduiTemplateService,
    private val bduiActionService: BduiActionService,
    private val replaceService: ReplaceService,
    private val favoritesService: FavoritesService,
    private val recommendationsService: RecommendationsService,
    private val briefProductMapper: DomainToPresentationBriefProductMapper,
    private val sessionObserver: SessionObserver
) {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private val _effects = Channel<ProductDetailEffect>(Channel.BUFFERED)
    val effects: Flow<ProductDetailEffect> = _effects.receiveAsFlow()

    private var currentProductId: String? = null

    /** Attribution of the navigation that opened the current product, so a retry reports the
     * surface the user actually came from rather than collapsing to PRODUCT_SCREEN. */
    private var currentSurface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN
    private var currentPosition: Int? = null

    /** In-flight product load; cancelled when a new product loads so a stale product's brief/bdui
     * responses can't overwrite the newer load (item 8). */
    private var loadJob: Job? = null

    /**
     * Latest replace wiring for the current product, cached so it can be folded into
     * `Ready` regardless of whether the (non-blocking) bindings call resolves before or
     * after the BDUI body becomes `Ready`. Reset on every product load.
     */
    private var currentReplaceBindings: List<ReplaceBinding> = emptyList()

    /**
     * The single, cross-platform entry point for dispatching a BDUI action. Takes the opaque
     * [ActionContext] and unwraps its [JsonObject] **here in Kotlin**, so the context is built and
     * read entirely Kotlin-side and never crosses the SKIE bridge as a Swift `Dictionary` (which
     * would mangle it / crash on `Map#get`). Every platform routes through here; the
     * [ProductDetailEvent.DispatchAction] constructor is `internal` to enforce it.
     */
    fun dispatchBduiAction(
        actionId: String,
        context: ActionContext,
        idempotencyKey: String? = null
    ) {
        onEvent(ProductDetailEvent.DispatchAction(actionId, context.json, idempotencyKey))
    }

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(
                event.productId, event.surface, event.position
            )

            ProductDetailEvent.Retry -> retry()
            ProductDetailEvent.ToggleFavorite -> toggleFavorite()
            is ProductDetailEvent.DispatchAction -> dispatchAction(
                event.actionId, event.context, event.idempotencyKey
            )
        }
    }

    private fun loadProduct(
        productId: String,
        surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
        position: Int? = null
    ) {
        loadJob?.cancel()
        currentProductId = productId
        currentSurface = surface
        currentPosition = position
        currentReplaceBindings = emptyList()
        _state.update {
            it.copy(
                briefState = BriefProductState.Loading,
                bduiBodyState = BduiBodyState.Loading,
                galleryUrls = emptyList(),
                isFavorited = false,
                recommendations = emptyList()
            )
        }

        loadJob = scope.launch {
            coroutineScope {
                launch { loadBriefAndBdui(productId, surface, position) }
                launch { loadReplaceBindings(productId) }
                launch { loadRecommendations(productId) }
                launch {
                    val isFav = isFavorite(productId)
                    _state.update { it.copy(isFavorited = isFav) }
                }
            }
        }
    }

    /**
     * 4th, non-blocking PDP call. Never gates the body render: on success we cache the
     * wiring and fold it into `Ready` if it already exists. Failure → leave it empty, so
     * replace simply never fires (not a screen error).
     */
    private suspend fun loadReplaceBindings(productId: String) {
        replaceService.getReplaceBindings(productId).fold(
            onSuccess = { bindings ->
                currentReplaceBindings = bindings
                _state.update { state ->
                    val ready = state.bduiBodyState as? BduiBodyState.Ready ?: return@update state
                    state.copy(bduiBodyState = ready.copy(replaceBindings = bindings))
                }
            },
            onError = { /* no-op: replace wiring stays empty */ }
        )
    }

    private suspend fun loadRecommendations(productId: String) {
        recommendationsService.getRecommendations(productId).fold(
            onSuccess = { products ->
                _state.update { state ->
                    state.copy(recommendations = products.map(briefProductMapper::map))
                }
            },
            onError = { }
        )
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
    private suspend fun loadBriefAndBdui(
        productId: String,
        surface: InteractionSurface,
        position: Int?
    ) = coroutineScope {
        val briefDef =
            async { productDetailService.getBriefProductById(productId, surface, position) }
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
                    it.copy(
                        bduiBodyState = BduiBodyState.Ready(
                            template = templateRes.data,
                            bindData = bindData,
                            replaceBindings = currentReplaceBindings
                        )
                    )
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

        _state.update { it.copy(isFavorited = !currentlyFavorited, isFavoriteLoading = true) }

        scope.launch {
            favoritesService.toggleFavorite(productId, currentlyFavorited).fold(
                onSuccess = {
                    _state.update { it.copy(isFavoriteLoading = false) }
                },
                onError = {
                    _state.update {
                        it.copy(
                            isFavorited = currentlyFavorited,
                            isFavoriteLoading = false
                        )
                    }
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

        // `replace` and `navigate` are resolved entirely client-side — they are NOT server
        // actions and must never be POSTed to /ui/action (the allowlist would 404 them).
        if (actionId == BduiConstants.REPLACE_ACTION_ID) {
            handleReplace(ready, context)
            return
        }
        if (actionId == BduiConstants.NAVIGATE_ACTION_ID) {
            handleNavigate(context)
            return
        }

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
                    _effects.send(
                        ProductDetailEffect.ShowToast(
                            message = "Action failed. Please try again.",
                            severity = ToastSeverity.error
                        )
                    )
                }
            )
        }
    }

    /**
     * Resolves a `replace` action client-side and swaps the slot via the standalone-scope
     * `replacedSlots` map. One-way: an already-replaced slot is ignored. A no-op result
     * (no binding for the slot, or layout/data drift) leaves the UI untouched.
     */
    private fun handleReplace(ready: BduiBodyState.Ready, context: JsonObject) {
        val targetSlotId = (context[BduiConstants.TARGET_SLOT_ID_KEY] as? JsonPrimitive)
            ?.contentOrNull
            ?: return
        if (ready.replacedSlots.containsKey(targetSlotId)) return

        scope.launch {
            replaceService.resolve(ready.replaceBindings, targetSlotId).fold(
                onSuccess = { resolved ->
                    if (resolved != null) {
                        _state.update { state ->
                            val current = state.bduiBodyState as? BduiBodyState.Ready
                                ?: return@update state
                            state.copy(
                                bduiBodyState = current.copy(
                                    replacedSlots = current.replacedSlots + (targetSlotId to resolved)
                                )
                            )
                        }
                    }
                },
                onError = { /* best-effort: leave the original subtree in place */ }
            )
        }
    }

    private fun handleNavigate(context: JsonObject) {
        val url = (context[BduiConstants.URL_KEY] as? JsonPrimitive)
            ?.contentOrNull
            ?: return
        AppRouteParser.parse(url) ?: return
        val replace = (context[BduiConstants.REPLACE_STACK_KEY] as? JsonPrimitive)
            ?.contentOrNull == "true"
        scope.launch {
            _effects.send(ProductDetailEffect.Navigate(url, replace))
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
                _effects.send(ProductDetailEffect.Navigate(mutation.url, mutation.replace))
            }

            is BduiMutation.ShowToast -> {
                _effects.send(ProductDetailEffect.ShowToast(mutation.message, mutation.severity))
            }
        }
    }

    private fun retry() {
        currentProductId?.let { loadProduct(it, currentSurface, currentPosition) }
    }
}
