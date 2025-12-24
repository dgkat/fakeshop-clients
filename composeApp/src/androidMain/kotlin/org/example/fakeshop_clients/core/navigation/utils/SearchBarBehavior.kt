package org.example.fakeshop_clients.core.navigation.utils

import org.example.fakeshop_clients.core.navigation.Route
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior

fun getSearchBarBehavior(route: String?): SearchBarBehavior {
    return when (route) {
        Route.Home.route -> SearchBarBehavior.SCROLL_REACTIVE
        Route.Favorites.route -> SearchBarBehavior.SCROLL_REACTIVE
        Route.ProductDetail.route -> SearchBarBehavior.STATIC
        Route.Notifications.route -> SearchBarBehavior.HIDDEN
        Route.Profile.route -> SearchBarBehavior.STATIC
        else -> SearchBarBehavior.STATIC
    }
}