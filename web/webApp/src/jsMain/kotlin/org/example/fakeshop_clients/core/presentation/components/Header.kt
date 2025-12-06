package org.example.fakeshop_clients.core.presentation.components

import org.example.fakeshop_clients.core.navigation.desktop.DesktopNav
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.header
import web.cssom.ClassName

val Header = FC<Props> {
    header {
        className = ClassName("header")
        div {
            className = ClassName("container header-content")
            h1 {
                className = ClassName("logo")
                a {
                    href = "/"
                    +"E-Shop"
                }
            }
            button {
                onClick = {
                    console.log("[SearchButton] Clicked!")
                    //TODO replace with actual search impl
                }
                +"Search functionality coming soon"
            }
            DesktopNav()
        }
    }
}