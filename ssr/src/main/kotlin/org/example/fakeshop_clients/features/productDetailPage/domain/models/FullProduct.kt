package org.example.fakeshop_clients.features.productDetailPage.domain.models

data class FullProduct(
    val id : String,
    val name : String,
    val price : Double,
    val imageUrl : String,
    val category : String,
    val description : String,
    val rating : Double,
    val reviews : Int,
    val inStock : Boolean = true,
    val isLiked : Boolean = false
)