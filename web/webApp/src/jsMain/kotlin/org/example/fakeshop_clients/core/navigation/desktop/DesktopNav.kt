package org.example.fakeshop_clients.core.navigation.desktop

import kotlinx.browser.window
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.nav
import react.router.dom.Link
import react.router.useLocation
import org.example.fakeshop_clients.core.strings.Strings
import web.cssom.ClassName

val DesktopNav = FC<Props> {
    val location = useLocation()
    val isHome = window.location.pathname == "/"

    nav {
        className = ClassName("desktop-nav")

        a {
            href = "/"
            className = ClassName(if (isHome) "nav-link active" else "nav-link")
            +Strings.TAB_HOME
        }
        Link {
            to = "/favorites"
            className = ClassName(if (location.pathname == "/favorites") "nav-link active" else "nav-link")
            +Strings.TAB_FAVORITES
        }
        Link {
            to = "/notifications"
            className = ClassName(if (location.pathname == "/notifications") "nav-link active" else "nav-link")
            +Strings.TAB_NOTIFICATIONS
        }
        Link {
            to = "/profile"
            className = ClassName(if (location.pathname == "/profile") "nav-link active" else "nav-link")
            +Strings.TAB_PROFILE
        }
    }
}