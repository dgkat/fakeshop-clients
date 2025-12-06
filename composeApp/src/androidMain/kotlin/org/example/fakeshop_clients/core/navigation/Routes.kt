package org.example.fakeshop_clients.core.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
    object Favorites : Route("favorites")
    object Notifications : Route("notifications")
    object Profile : Route("profile")
    object ProductDetail : Route("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
}