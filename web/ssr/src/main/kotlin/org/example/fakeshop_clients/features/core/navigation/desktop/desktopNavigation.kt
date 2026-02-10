package org.example.fakeshop_clients.features.core.navigation.desktop

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.nav
import org.example.fakeshop_clients.core.strings.Strings

fun FlowContent.desktopNavigation(activeTab: String?) {
    nav(classes = "desktop-nav") {
        a(href = "/", classes = if (activeTab == "home") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +Strings.TAB_HOME
        }
        a(href = "/favorites", classes = if (activeTab == "favorites") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +Strings.TAB_FAVORITES
        }
        a(href = "/notifications", classes = if (activeTab == "notifications") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +Strings.TAB_NOTIFICATIONS
        }
        a(href = "/profile", classes = if (activeTab == "profile") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +Strings.TAB_PROFILE
        }
    }
}