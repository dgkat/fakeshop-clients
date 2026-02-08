package org.example.fakeshop_clients.features.productDetail.domain.models

data class DetailedProduct(
    val description: String?,
    val specs: String?,
    val galleryUrls: List<String>?
)
