package org.example.fakeshop_clients.features.profile.presentation.components

import org.example.fakeshop_clients.core.i18n.getString
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

val LoadingView = FC<Props> {
    div {
        className = ClassName("loading-container")
        p {
            +getString("loading")
        }
    }
}
