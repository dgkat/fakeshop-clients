package org.example.fakeshop_clients.features.search.presentation.components

import org.example.fakeshop_clients.features.search.domain.models.SearchResult
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

external interface SearchResultItemProps : Props {
    var result: SearchResult
    var onClick: () -> Unit
}

val SearchResultItem = FC<SearchResultItemProps> { props ->
    div {
        className = ClassName("search-result-item")
        onClick = { props.onClick() }

        div {
            className = ClassName("search-result-content")

            p {
                className = ClassName("search-result-title")
                +props.result.productName
            }
        }
    }
}
