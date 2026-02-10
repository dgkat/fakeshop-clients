package org.example.fakeshop_clients.features.profile.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import org.example.fakeshop_clients.core.strings.Strings
import web.cssom.ClassName

val LoadingView = FC<Props> {
    div {
        className = ClassName("loading-container")
        p {
            +Strings.LOADING
        }
    }
}