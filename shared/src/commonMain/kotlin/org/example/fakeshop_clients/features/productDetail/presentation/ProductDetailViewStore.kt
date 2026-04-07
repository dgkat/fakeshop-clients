package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationDetailedProductMapper

class ProductDetailViewStore(
    private val scope: CoroutineScope,
    private val productDetailService: ProductDetailService,
    private val favoritesService: FavoritesService,
    private val briefProductMapper: DomainToPresentationBriefProductMapper,
    private val detailedProductMapper: DomainToPresentationDetailedProductMapper
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
                detailedState = DetailedProductState.Loading,
                isFavorited = productId in favoritesService.favoritedIds.value
            )
        }

        scope.launch {
            coroutineScope {
                launch { loadBriefProduct(productId) }
                launch { loadDetailedProduct(productId) }
                launch { favoritesService.checkFavorite(productId) }
            }
        }
    }

    private suspend fun loadBriefProduct(productId: String) {
        productDetailService.getBriefProductById(productId).fold(
            onSuccess = { briefProduct ->
                _state.update {
                    it.copy(
                        briefState = BriefProductState.Success(
                            product = briefProductMapper.map(briefProduct)
                        )
                    )
                }
            },
            onError = { networkError ->
                _state.update {
                    it.copy(
                        briefState = BriefProductState.Error(
                            error = ProductDetailError.Network(networkError)
                        )
                    )
                }
            }
        )
    }

    private suspend fun loadDetailedProduct(productId: String) {
        productDetailService.getDetailedProductById(productId).fold(
            onSuccess = { detailedProduct ->
                _state.update {
                    it.copy(
                        detailedState = DetailedProductState.Success(
                            product = detailedProductMapper.map(detailedProduct)
                        )
                    )
                }
            },
            onError = { networkError ->
                _state.update {
                    it.copy(
                        detailedState = DetailedProductState.Error(
                            error = ProductDetailError.Network(networkError)
                        )
                    )
                }
            }
        )
    }

    private fun toggleFavorite() {
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
