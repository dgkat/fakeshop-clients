package org.example.fakeshop_clients.features.core.navigation.desktop

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.nav
import kotlinx.html.span
import org.example.fakeshop_clients.core.i18n.WebStrings

fun FlowContent.desktopNavigation(
    activeTab: String?,
    locale: String,
    strings: Map<String, String>,
    currentPath: String = "/$locale/"
) {
    nav(classes = "desktop-nav") {
        a(href = "/$locale/", classes = if (activeTab == "home") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +(strings["tab_home"] ?: "Home")
        }
        a(href = "/$locale/favorites", classes = if (activeTab == "favorites") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +(strings["tab_favorites"] ?: "Favorites")
        }
        a(href = "/$locale/notifications", classes = if (activeTab == "notifications") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +(strings["tab_notifications"] ?: "Notifications")
        }
        a(href = "/$locale/profile", classes = if (activeTab == "profile") "nav-link active" else "nav-link") {
            attributes["data-transition-link"] = ""
            +(strings["tab_profile"] ?: "Profile")
        }

        // Language switcher
        div(classes = "lang-switcher") {
            WebStrings.SUPPORTED_LOCALES.forEach { loc ->
                span(classes = if (loc == locale) "lang-option active" else "lang-option") {
                    if (loc != locale) {
                        a(href = currentPath.replaceFirst("/$locale/", "/$loc/")) {
                            +loc.uppercase()
                        }
                    } else {
                        +loc.uppercase()
                    }
                }
            }
        }
    }
}
