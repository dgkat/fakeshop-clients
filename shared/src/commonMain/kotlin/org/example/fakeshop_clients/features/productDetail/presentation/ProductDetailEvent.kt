package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.serialization.json.JsonObject

sealed class ProductDetailEvent {
    data class LoadProduct(val productId: String) : ProductDetailEvent()
    data object Retry : ProductDetailEvent()
    data object ToggleFavorite : ProductDetailEvent()
    /**
     * Carries a raw [JsonObject] context, so it must NOT be constructed from platform UI code —
     * iOS can't build a [JsonObject] without bridging it across SKIE (see [ActionContext]). All
     * platforms dispatch BDUI actions through [ProductDetailViewStore.dispatchBduiAction] instead;
     * the `internal` constructor enforces that single entry point.
     */
    @ConsistentCopyVisibility
    data class DispatchAction internal constructor(
        val actionId: String,
        val context: JsonObject,
        val idempotencyKey: String? = null
    ) : ProductDetailEvent()
}
