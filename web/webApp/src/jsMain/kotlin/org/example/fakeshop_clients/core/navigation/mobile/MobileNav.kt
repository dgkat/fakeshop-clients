package org.example.fakeshop_clients.core.navigation.mobile

import kotlinx.browser.window
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import react.router.useLocation
import org.example.fakeshop_clients.core.strings.Strings
import web.cssom.ClassName

// Bottom Navigation
val BottomNav = FC<Props> {
    val location = useLocation()
    val isHome = window.location.pathname == "/"

    nav {
        className = ClassName("bottom-nav")

        a {
            href = "/"
            className = ClassName(if (isHome) "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"🏠"
            }
            span {
                className = ClassName("nav-label")
                +Strings.TAB_HOME
            }
        }
        Link {
            to = "/favorites"
            className = ClassName(if (location.pathname == "/favorites") "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"❤️"
            }
            span {
                className = ClassName("nav-label")
                +Strings.TAB_FAVORITES
            }
        }
        Link {
            to = "/notifications"
            className = ClassName(if (location.pathname == "/notifications") "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"🔔"
            }
            span {
                className = ClassName("nav-label")
                +Strings.TAB_NOTIFICATIONS
            }
        }
        Link {
            to = "/profile"
            className = ClassName(if (location.pathname == "/profile") "nav-item active" else "nav-item")
            span {
                className = ClassName("nav-icon")
                +"👤"
            }
            span {
                className = ClassName("nav-label")
                +Strings.TAB_PROFILE
            }
        }
    }
}