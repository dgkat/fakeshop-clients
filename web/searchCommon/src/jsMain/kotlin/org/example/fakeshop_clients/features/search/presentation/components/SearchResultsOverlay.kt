package org.example.fakeshop_clients.features.search.presentation.components

import org.example.fakeshop_clients.features.search.domain.models.SearchResult
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

external interface SearchResultsOverlayProps : Props {
    var results: List<SearchResult>
    var isLoading: Boolean
    var onResultClick: (SearchResult) -> Unit
}

val SearchResultsOverlay = FC<SearchResultsOverlayProps> { props ->
    div {
        className = ClassName("search-results-overlay")

        when {
            props.isLoading -> {
                div {
                    className = ClassName("search-loading")
                    p { +"Loading..." }
                }
            }
            props.results.isEmpty() -> {
                div {
                    className = ClassName("search-empty")
                    p { +"No results found" }
                }
            }
            else -> {
                props.results.forEach { result ->
                    SearchResultItem {
                        this.result = result
                        this.onClick = { props.onResultClick(result) }
                    }
                }
            }
        }
    }
}
