package org.example.fakeshop_clients.features.core.navigation.desktop

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.nav

fun FlowContent.desktopNavigation(activeTab: String?) {
    nav(classes = "desktop-nav") {
        a(href = "/", classes = if (activeTab == "home") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +"Home"
        }
        a(href = "/favorites", classes = if (activeTab == "favorites") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +"Favorites"
        }
        a(href = "/notifications", classes = if (activeTab == "notifications") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +"Notifications"
        }
        a(href = "/profile", classes = if (activeTab == "profile") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +"Profile"
        }
    }
}