package org.example.fakeshop_clients.features.product_list.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import org.example.fakeshop_clients.core.i18n.getString
import web.cssom.ClassName

external interface LoadingViewProps : Props {
    var onLoadClick: () -> Unit
}

val LoadingView = FC<LoadingViewProps> { props ->
    div {
        className = ClassName("loading-view")
        p { +getString("loading_products") }
        button {
            className = ClassName("btn btn-primary")
            onClick = {
                props.onLoadClick()
            }
            +"Get fake items"
        }
    }
}
