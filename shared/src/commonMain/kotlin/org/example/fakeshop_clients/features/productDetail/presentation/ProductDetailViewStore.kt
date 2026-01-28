package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationDetailedProductMapper

class ProductDetailViewStore(
    private val scope: CoroutineScope,
    private val productDetailService: ProductDetailService,
    private val briefProductMapper: DomainToPresentationBriefProductMapper,
    private val detailedProductMapper: DomainToPresentationDetailedProductMapper
) {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private var currentProductId: String? = null

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(event.productId)
            ProductDetailEvent.Retry -> retry()
        }
    }

    private fun loadProduct(productId: String) {
        currentProductId = productId
        _state.update {
            it.copy(
                briefState = BriefProductState.Loading,
                detailedState = DetailedProductState.Loading
            )
        }

        scope.launch {
            loadBriefProduct(productId)
            loadDetailedProduct(productId)
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

    private fun retry() {
        currentProductId?.let { loadProduct(it) }
    }
}
