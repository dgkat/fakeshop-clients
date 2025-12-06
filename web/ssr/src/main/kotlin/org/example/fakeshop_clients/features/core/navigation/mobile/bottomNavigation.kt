package org.example.fakeshop_clients.features.core.navigation.mobile

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.nav
import kotlinx.html.span

fun FlowContent.bottomNavigation(activeTab: String?) {
    nav(classes = "bottom-nav") {
        a(href = "/", classes = if (activeTab == "home") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🏠" }
            span(classes = "nav-label") { +"Home" }
        }

        a(href = "/favorites", classes = if (activeTab == "favorites") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"❤️" }
            span(classes = "nav-label") { +"Favorites" }
        }

        a(href = "/notifications", classes = if (activeTab == "notifications") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🔔" }
            span(classes = "nav-label") { +"Notifications" }
        }

        a(href = "/profile", classes = if (activeTab == "profile") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"👤" }
            span(classes = "nav-label") { +"Profile" }
        }
    }
}