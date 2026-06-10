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
 * Top-level keys come from the brief + detailed fields; the v2 `data` blob is spread
 * at the top level so template bind paths like "title1" resolve directly.
 *
 *   { id, name, price, imageUrl, category, fullDescription, galleryUrls, bduiUrls (alias for galleryUrls), <data keys...> }
 */
fun buildPdpBindData(brief: UiBriefProduct, detailed: DetailedProduct): JsonObject = buildJsonObject {
    put("id", JsonPrimitive(brief.id))
    put("name", JsonPrimitive(brief.name))
    put("price", JsonPrimitive(brief.price))
    put("imageUrl", JsonPrimitive(brief.imageUrl))
    put("category", JsonPrimitive(brief.category))
    put("fullDescription", detailed.fullDescription?.let(::JsonPrimitive) ?: JsonNull)
    val galleryArray = JsonArray(detailed.galleryUrls.map(::JsonPrimitive))
    put("galleryUrls", galleryArray)
    put("bduiUrls", galleryArray)
    // Spread data keys at top level for direct bind paths (e.g. "bduiTitle")
    detailed.data.forEach { (key, value) -> put(key, value) }
    // Also nest under "data" so paths like "data.specs" resolve correctly
    put("data", detailed.data)
}
