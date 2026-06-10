package org.example.fakeshop_clients.features.bdui.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Wire model for ReplaceData — the per-product `values` that fill a ReplaceLayout.
 * `values` is opaque JSON (no BE schema), kept as a [JsonObject] and bound at render time.
 */
@Serializable
data class ReplaceDataResponse(
    val id: String,
    val layoutId: String,
    val productId: String,
    val values: JsonObject,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
