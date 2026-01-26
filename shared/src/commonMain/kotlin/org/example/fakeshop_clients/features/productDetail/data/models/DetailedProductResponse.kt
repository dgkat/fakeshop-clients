package org.example.fakeshop_clients.features.productDetail.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DetailedProductResponse(
    val description: String?,
    val specs: String?,
    val galleryUrls: List<String>?
)
