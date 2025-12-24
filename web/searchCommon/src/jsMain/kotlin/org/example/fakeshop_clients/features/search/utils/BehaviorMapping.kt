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
     */
    fun getSearchBarBehaviorFromRoute(pathname: String): SearchBarBehavior {
        return when {
            pathname == "/" || pathname.startsWith("/home") -> SearchBarBehavior.SCROLL_REACTIVE
            pathname.startsWith("/favorites") -> SearchBarBehavior.SCROLL_REACTIVE
            pathname.startsWith("/product/") -> SearchBarBehavior.SCROLL_REACTIVE
            pathname.startsWith("/notifications") -> SearchBarBehavior.HIDDEN
            pathname.startsWith("/profile") -> SearchBarBehavior.STATIC
            else -> SearchBarBehavior.STATIC
        }
    }
}
