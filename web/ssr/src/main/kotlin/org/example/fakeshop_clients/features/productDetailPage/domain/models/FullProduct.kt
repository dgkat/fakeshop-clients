package org.example.fakeshop_clients.features.productDetailPage.domain.models

data class FullProduct(
    val id : String,
    val name : String,
    val price : Double,
    val imageUrl : String,
    val category : String,
    val description : String?,
    val specs: String?,
    val galleryUrls: List<String>?,
    val isLiked : Boolean = false
)