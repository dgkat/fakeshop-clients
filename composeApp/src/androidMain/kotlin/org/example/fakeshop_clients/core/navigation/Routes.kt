package org.example.fakeshop_clients.core.navigation

import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface

sealed class Route(val route: String) {
    object Home : Route("home")
    object Favorites : Route("favorites")
    object Notifications : Route("notifications")
    object Profile : Route("profile")

    /**
     * `surface`/`position` carry the originating-list attribution across the navigation hop — the
     * list the user tapped, not the screen they land on. Both are optional, so deeplinks and
     * existing navigations keep working and correctly report [InteractionSurface.PRODUCT_SCREEN].
     */
    object ProductDetail : Route("product/{productId}?surface={surface}&position={position}") {
        const val ARG_PRODUCT_ID = "productId"
        const val ARG_SURFACE = "surface"
        const val ARG_POSITION = "position"

        fun createRoute(
            productId: String,
            surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
            position: Int? = null
        ): String = buildString {
            append("product/")
            append(productId)
            append("?surface=")
            append(surface.wireValue)
            position?.let {
                append("&position=")
                append(it)
            }
        }
    }
}
