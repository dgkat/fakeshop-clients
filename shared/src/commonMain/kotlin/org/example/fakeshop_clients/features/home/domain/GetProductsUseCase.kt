package org.example.fakeshop_clients.features.home.domain

import kotlinx.coroutines.delay
import org.example.fakeshop_clients.features.home.presentation.models.UiBriefProduct

class GetProductsUseCase {

    suspend operator fun invoke(): List<UiBriefProduct> {
        delay(2000)

        // Return fake data
        return listOf(
            UiBriefProduct(
                id = "1",
                name = "Wireless Headphones",
                price = 99.99,
                imageUrl = "https://placehold.co/300x300?text=Headphones",
                category = "Electronics"
            ),
            UiBriefProduct(
                id = "2",
                name = "Smart Watch",
                price = 249.99,
                imageUrl = "https://placehold.co/300x300?text=Smart+Watch",
                category = "Electronics"
            ),
            UiBriefProduct(
                id = "3",
                name = "Running Shoes",
                price = 79.99,
                imageUrl = "https://placehold.co/300x300?text=Running+Shoes",
                category = "Sports"
            ),
            UiBriefProduct(
                id = "4",
                name = "Laptop Backpack",
                price = 49.99,
                imageUrl = "https://placehold.co/300x300?text=Backpack",
                category = "Accessories"
            ),
            UiBriefProduct(
                id = "5",
                name = "Bluetooth Speaker",
                price = 59.99,
                imageUrl = "https://placehold.co/300x300?text=Speaker",
                category = "Electronics"
            ),
            UiBriefProduct(
                id = "6",
                name = "Coffee Maker",
                price = 129.99,
                imageUrl = "https://placehold.co/300x300?text=Coffee+Maker",
                category = "Home"
            ),
            UiBriefProduct(
                id = "7",
                name = "Yoga Mat",
                price = 29.99,
                imageUrl = "https://placehold.co/300x300?text=Yoga+Mat",
                category = "Sports"
            ),
            UiBriefProduct(
                id = "8",
                name = "Phone Case",
                price = 19.99,
                imageUrl = "https://placehold.co/300x300?text=Phone+Case",
                category = "Accessories"
            )
        )
    }
}