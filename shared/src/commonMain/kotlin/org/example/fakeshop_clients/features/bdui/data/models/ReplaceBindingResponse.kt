package org.example.fakeshop_clients.features.bdui.data.models

import kotlinx.serialization.Serializable

/**
 * Wire model for one replace binding. Returned as a bare array by
 * `GET /products/{id}/replace-bindings`. Wires a product slot to a reusable
 * layout + per-product data.
 */
@Serializable
data class ReplaceBindingResponse(
    val targetSlotId: String,
    val layoutId: String,
    val dataId: String
)
