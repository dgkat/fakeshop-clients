package org.example.fakeshop_clients.core.presentation.models

data class UiBriefProduct(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)