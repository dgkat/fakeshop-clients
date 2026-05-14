package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.serialization.json.JsonObject

sealed class ProductDetailEvent {
    data class LoadProduct(val productId: String) : ProductDetailEvent()
    data object Retry : ProductDetailEvent()
    data object ToggleFavorite : ProductDetailEvent()
    data class DispatchAction(
        val actionId: String,
        val context: JsonObject,
        val idempotencyKey: String? = null
    ) : ProductDetailEvent()
}
