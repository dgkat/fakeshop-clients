package org.example.fakeshop_clients.features.search.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

external interface SearchBackdropProps : Props {
    var onClick: () -> Unit
}

/**
 * Semi-transparent backdrop overlay that appears when search is active.
 * Clicking it dismisses the search.
 */
val SearchBackdrop = FC<SearchBackdropProps> { props ->
    div {
        className = ClassName("search-backdrop")
        onClick = { props.onClick() }
    }
}
