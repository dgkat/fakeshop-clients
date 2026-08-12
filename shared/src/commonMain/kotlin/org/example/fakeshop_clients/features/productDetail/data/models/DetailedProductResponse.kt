package org.example.fakeshop_clients.features.productDetail.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DetailedProductResponse(
    val productId: String,
    val category: String,
    val fullDescription: String? = null,
    val galleryUrls: List<String> = emptyList(),
    val data: JsonObject
)
