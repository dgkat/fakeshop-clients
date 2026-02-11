package org.example.fakeshop_clients.core.navigation.mobile

import kotlinx.browser.window
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.core.i18n.getString
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import react.router.useLocation
import web.cssom.ClassName

// Bottom Navigation
val BottomNav = FC<Props> {
    val location = useLocation()
    val locale = I18n.locale
    val isHome = window.location.pathname == "/$locale/" || window.location.pathname == "/$locale"

    nav {
        className = ClassName("bottom-nav")

        a {
            href = "/$locale/"
            className = ClassName(if (isHome) "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"🏠"
            }
            span {
                className = ClassName("nav-label")
                +getString("tab_home")
            }
        }
        Link {
            to = "/$locale/favorites"
            className = ClassName(if (location.pathname.contains("/favorites")) "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"❤️"
            }
            span {
                className = ClassName("nav-label")
                +getString("tab_favorites")
            }
        }
        Link {
            to = "/$locale/notifications"
            className = ClassName(if (location.pathname.contains("/notifications")) "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"🔔"
            }
            span {
                className = ClassName("nav-label")
                +getString("tab_notifications")
            }
        }
        Link {
            to = "/$locale/profile"
            className = ClassName(if (location.pathname.contains("/profile")) "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"👤"
            }
            span {
                className = ClassName("nav-label")
                +getString("tab_profile")
            }
        }
    }
}
