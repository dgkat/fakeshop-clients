package org.example.fakeshop_clients.features.home.domain.models

data class CategoryRow(
    val category: String,
    val products: List<BriefProduct>
)
