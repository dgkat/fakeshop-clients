package org.example.fakeshop_clients.features.productDetail.presentation

sealed class ProductDetailEvent {
    data class LoadProduct(val productId: String) : ProductDetailEvent()
    data object Retry : ProductDetailEvent()
}
