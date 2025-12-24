package org.example.fakeshop_clients.features.search.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val products: List<ProductResponse>
)

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val category: String
)