package org.example.fakeshop_clients.features.core.navigation.mobile

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.nav
import kotlinx.html.span
import org.example.fakeshop_clients.core.strings.Strings

fun FlowContent.bottomNavigation(activeTab: String?) {
    nav(classes = "bottom-nav") {
        a(href = "/", classes = if (activeTab == "home") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🏠" }
            span(classes = "nav-label") { +Strings.TAB_HOME }
        }

        a(href = "/favorites", classes = if (activeTab == "favorites") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"❤️" }
            span(classes = "nav-label") { +Strings.TAB_FAVORITES }
        }

        a(href = "/notifications", classes = if (activeTab == "notifications") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"🔔" }
            span(classes = "nav-label") { +Strings.TAB_NOTIFICATIONS }
        }

        a(href = "/profile", classes = if (activeTab == "profile") "nav-item active" else "nav-item") {
            attributes["data-transition-link"] = ""
            span(classes = "nav-icon") { +"👤" }
            span(classes = "nav-label") { +Strings.TAB_PROFILE }
        }
    }
}