package org.example.fakeshop_clients.features.search.presentation.components

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior
import org.example.fakeshop_clients.features.search.presentation.SearchEvent
import org.example.fakeshop_clients.features.search.presentation.SearchState
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.hooks.useScrollOffset
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectWithCleanup
import react.useRef
import react.useState
import web.cssom.ClassName
import web.html.HTMLDivElement

external interface SearchBarProps : Props {
    var viewModel: SearchViewModel
    var behavior: SearchBarBehavior
    var onNavigateToProduct: (String) -> Unit
}

val SearchBar = FC<SearchBarProps> { props ->
    var searchState by useState(SearchState())

    // Subscribe to ViewModel state changes
    useEffectWithCleanup(props.viewModel) {
        val scope = MainScope()
        val job = props.viewModel.uiState
            .onEach { newState ->
                searchState = newState
            }
            .launchIn(scope)

        onCleanup {
            job.cancel()
        }
    }

    // Always call useScrollOffset (hooks must be called unconditionally)
    val scrollState = useScrollOffset(maxOffset = 72.0)
    val containerRef = useRef<HTMLDivElement>(null)

    // Apply transform to DOM element when scroll state changes (only for scroll-reactive behavior)
    useEffect(scrollState.offset, props.behavior) {
        if (props.behavior == SearchBarBehavior.SCROLL_REACTIVE) {
            containerRef.current?.style?.transform = "translateY(${scrollState.offset}px)"
        } else {
            // Reset transform for non-scroll-reactive behaviors
            containerRef.current?.style?.transform = "translateY(0px)"
        }
    }

    // Don't render if hidden
    if (props.behavior == SearchBarBehavior.HIDDEN) {
        return@FC
    }

    // Show shadow based on behavior
    val showShadow = props.behavior == SearchBarBehavior.STATIC

    div {
        className = ClassName("search-bar-container")
        ref = containerRef

        // Search input
        SearchBarInput {
            this.query = searchState.query
            this.showShadow = showShadow
            this.onQueryChange = { newQuery ->
                props.viewModel.onEvent(SearchEvent.QueryChanged(newQuery))
            }
            this.onClear = {
                props.viewModel.onEvent(SearchEvent.ClearQuery)
            }
        }
    }

    // Backdrop when active (rendered as sibling to container)
    if (searchState.isActive && props.behavior != SearchBarBehavior.HIDDEN) {
        div {
            SearchBackdrop {
                this.onClick = {
                    props.viewModel.onEvent(SearchEvent.CancelClicked)
                }
            }
        }
    }

    // Results overlay when active (rendered as sibling to container)
    if (searchState.isActive && props.behavior != SearchBarBehavior.HIDDEN) {
        div {
            SearchResultsOverlay {
                this.results = searchState.results
                this.isLoading = searchState.isLoading
                this.onResultClick = { result ->
                    props.onNavigateToProduct(result.productId)
                    props.viewModel.onEvent(SearchEvent.CancelClicked)
                }
            }
        }
    }
}
