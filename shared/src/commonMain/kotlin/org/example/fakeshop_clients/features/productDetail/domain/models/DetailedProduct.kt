package org.example.fakeshop_clients.features.productDetail.domain.models

import kotlinx.serialization.json.JsonObject

data class DetailedProduct(
    val productId: String,
    val category: String,
    val fullDescription: String?,
    val galleryUrls: List<String>,
    val data: JsonObject
)
