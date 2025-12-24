package org.example.fakeshop_clients.features.search.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.span
import web.cssom.ClassName
import web.html.InputType
import web.html.text

external interface SearchBarInputProps : Props {
    var query: String
    var showShadow: Boolean
    var onQueryChange: (String) -> Unit
    var onClear: () -> Unit
}

val SearchBarInput = FC<SearchBarInputProps> { props ->
    div {
        className = ClassName(
            if (props.showShadow) "search-bar-input search-bar-shadow"
            else "search-bar-input"
        )

        span {
            className = ClassName("search-icon")
            +"🔍"
        }

        input {
            type = InputType.text
            value = props.query
            placeholder = "Search products..."
            className = ClassName("search-input")
            onChange = { event ->
                props.onQueryChange(event.target.value)
            }
        }

        if (props.query.isNotEmpty()) {
            button {
                className = ClassName("search-clear-btn")
                onClick = { props.onClear() }
                +"✕"
            }
        }
    }
}
