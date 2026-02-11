package org.example.fakeshop_clients.features.core.navigation.mobile

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.nav
import kotlinx.html.span

fun FlowContent.bottomNavigation(
    activeTab: String?,
    locale: String,
    strings: Map<String, String>
) {
    nav(classes = "bottom-nav") {
        a(href = "/$locale/", classes = if (activeTab == "home") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🏠" }
            span(classes = "nav-label") { +(strings["tab_home"] ?: "Home") }
        }

        a(href = "/$locale/favorites", classes = if (activeTab == "favorites") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"❤️" }
            span(classes = "nav-label") { +(strings["tab_favorites"] ?: "Favorites") }
        }

        a(href = "/$locale/notifications", classes = if (activeTab == "notifications") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🔔" }
            span(classes = "nav-label") { +(strings["tab_notifications"] ?: "Notifications") }
        }

        a(href = "/$locale/profile", classes = if (activeTab == "profile") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"👤" }
            span(classes = "nav-label") { +(strings["tab_profile"] ?: "Profile") }
        }
    }
}
