package org.example.fakeshop_clients.features.product_list.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

external interface ErrorViewProps : Props {
    var errorMessage: String
}

val ErrorView = FC<ErrorViewProps> { props ->
    div {
        className = ClassName("error-view")
        p {
            className = ClassName("error-message")
            +"Error: ${props.errorMessage}"
        }
    }
}