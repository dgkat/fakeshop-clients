package org.example.fakeshop_clients.features.favorites.presentation

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import org.example.fakeshop_clients.core.strings.Strings
import web.cssom.ClassName

val FavoritesPage = FC<Props> {
    div {
        className = ClassName("page-content page-placeholder")

        span {
            className = ClassName("placeholder-icon")
            +"❤️"
        }
        h2 { +Strings.TAB_FAVORITES }
        p { +Strings.FAVORITES_EMPTY }
    }
}