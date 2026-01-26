package org.example.fakeshop_clients.features.home.domain.models

data class BriefProduct(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)
