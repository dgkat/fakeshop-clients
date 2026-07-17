package org.example.fakeshop_clients.core.navigation

sealed interface AppRoute {
    data object Home : AppRoute
    data class ProductDetail(val productId: String) : AppRoute
    data object Favorites : AppRoute
    data object Notifications : AppRoute
    data object Profile : AppRoute
}
