package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

/**
 * Builds the merged JsonObject the BDUI bind resolver walks.
 * Top-level keys come from the brief; `data` is the v2 detailed blob verbatim.
 *
 *   { id, name, price, imageUrl, category, fullDescription, galleryUrls, data: { ... } }
 */
fun buildPdpBindData(brief: UiBriefProduct, detailed: DetailedProduct): JsonObject = buildJsonObject {
    put("id", JsonPrimitive(brief.id))
    put("name", JsonPrimitive(brief.name))
    put("price", JsonPrimitive(brief.price))
    put("imageUrl", JsonPrimitive(brief.imageUrl))
    put("category", JsonPrimitive(brief.category))
    put("fullDescription", detailed.fullDescription?.let(::JsonPrimitive) ?: JsonNull)
    put("galleryUrls", JsonArray(detailed.galleryUrls.map(::JsonPrimitive)))
    put("data", detailed.data)
}
