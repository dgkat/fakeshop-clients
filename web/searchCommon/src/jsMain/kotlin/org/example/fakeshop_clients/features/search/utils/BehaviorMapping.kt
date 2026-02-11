package org.example.fakeshop_clients.features.search.utils

import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior

object BehaviorMapping {
    /**
     * Determines search bar behavior based on page identifier (for SSR islands).
     * Page identifier is read from document.body data-page attribute.
     */
    fun getSearchBarBehavior(page: String): SearchBarBehavior {
        return when {
            page.contains("home") -> SearchBarBehavior.SCROLL_REACTIVE
            page.contains("favorites") -> SearchBarBehavior.SCROLL_REACTIVE
            page.contains("product-detail") || page.contains("product/") -> SearchBarBehavior.SCROLL_REACTIVE
            page.contains("notifications") -> SearchBarBehavior.HIDDEN
            page.contains("profile") -> SearchBarBehavior.STATIC
            else -> SearchBarBehavior.STATIC
        }
    }

    /**
     * Determines search bar behavior based on route pathname (for SPA).
     * Pathname is obtained from React Router's useLocation().pathname.
     * Handles locale-prefixed routes like /en/favorites, /es/profile etc.
     */
    fun getSearchBarBehaviorFromRoute(pathname: String): SearchBarBehavior {
        return when {
            pathname.contains("/favorites") -> SearchBarBehavior.SCROLL_REACTIVE
            pathname.contains("/product/") -> SearchBarBehavior.SCROLL_REACTIVE
            pathname.contains("/notifications") -> SearchBarBehavior.HIDDEN
            pathname.contains("/profile") -> SearchBarBehavior.STATIC
            pathname == "/" || pathname.matches(Regex("^/[a-z]{2}/?$")) -> SearchBarBehavior.SCROLL_REACTIVE
            else -> SearchBarBehavior.STATIC
        }
    }
}
