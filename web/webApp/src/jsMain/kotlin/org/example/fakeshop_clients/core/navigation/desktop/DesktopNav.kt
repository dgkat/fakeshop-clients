package org.example.fakeshop_clients.core.navigation.desktop

import kotlinx.browser.window
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.core.i18n.getString
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import react.router.useLocation
import web.cssom.ClassName

val DesktopNav = FC<Props> {
    val location = useLocation()
    val locale = I18n.locale
    val isHome = window.location.pathname == "/$locale/" || window.location.pathname == "/$locale"

    nav {
        className = ClassName("desktop-nav")

        a {
            href = "/$locale/"
            className = ClassName(if (isHome) "nav-link active" else "nav-link")
            +getString("tab_home")
        }
        Link {
            to = "/$locale/favorites"
            className = ClassName(if (location.pathname.contains("/favorites")) "nav-link active" else "nav-link")
            +getString("tab_favorites")
        }
        Link {
            to = "/$locale/notifications"
            className = ClassName(if (location.pathname.contains("/notifications")) "nav-link active" else "nav-link")
            +getString("tab_notifications")
        }
        Link {
            to = "/$locale/profile"
            className = ClassName(if (location.pathname.contains("/profile")) "nav-link active" else "nav-link")
            +getString("tab_profile")
        }

        // Language switcher
        div {
            className = ClassName("lang-switcher")
            listOf("en", "es").forEach { loc ->
                span {
                    className = ClassName(if (loc == locale) "lang-option active" else "lang-option")
                    if (loc != locale) {
                        a {
                            href = swapLocaleInPath(window.location.pathname, locale, loc)
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

private fun swapLocaleInPath(currentPath: String, currentLocale: String, newLocale: String): String {
    return currentPath.replaceFirst("/$currentLocale", "/$newLocale")
}
