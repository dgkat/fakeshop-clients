package org.example.fakeshop_clients.core.presentation.models

import org.example.fakeshop_clients.core.presentation.format.formatPrice

data class UiBriefProduct(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String
) {
    val formattedPrice: String get() = formatPrice(price)
}