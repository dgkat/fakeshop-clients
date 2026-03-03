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
import react.Fragment
import react.Props
import react.create
import react.dom.createPortal
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectWithCleanup
import react.useRef
import react.useState
import web.cssom.ClassName
import web.dom.document
import web.html.HTMLDivElement
import web.html.HTMLElement

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

    // Check if we're on desktop (>= 768px)
    val isDesktop by useState { kotlinx.browser.window.matchMedia("(min-width: 768px)").matches }

    // Apply transform to DOM element when scroll state changes
    // Mobile: Apply transform for scroll-reactive behavior
    // Desktop: No transform (header handles scrolling)
    useEffect(scrollState.offset, props.behavior, isDesktop) {
        if (!isDesktop && props.behavior == SearchBarBehavior.SCROLL_REACTIVE) {
            // Mobile: apply transform (current behavior)
            containerRef.current?.style?.transform = "translateY(${scrollState.offset}px)"
        } else if (props.behavior == SearchBarBehavior.HIDDEN) {
            // Hidden: let CSS class handle the transform
            containerRef.current?.style?.transform = ""
        } else {
            // Desktop or STATIC: no transform
            containerRef.current?.style?.transform = "translateY(0px)"
        }
    }

    // Raise header z-index on desktop when search is active so it appears above the backdrop
    // Also dismiss search on scroll
    useEffectWithCleanup(searchState.isActive, isDesktop) {
        if (isDesktop) {
            val header = document.querySelector(".header") as? HTMLElement
            if (searchState.isActive) {
                header?.style?.zIndex = "var(--z-index-modal)"
            } else {
                header?.style?.zIndex = ""
            }
        }

        val scrollListener: (dynamic) -> Unit = {
            if (searchState.isActive) {
                props.viewModel.onEvent(SearchEvent.CancelClicked)
            }
        }

        if (searchState.isActive) {
            kotlinx.browser.window.addEventListener("scroll", scrollListener)
        }

        onCleanup {
            kotlinx.browser.window.removeEventListener("scroll", scrollListener)
        }
    }

    // Show shadow based on behavior (only on mobile with STATIC behavior)
    val showShadow = !isDesktop && props.behavior == SearchBarBehavior.STATIC

    // Build class name based on behavior and active state
    val containerClass = buildString {
        append("search-bar-container")
        if (props.behavior == SearchBarBehavior.HIDDEN) append(" search-bar-hidden")
        if (searchState.isActive) append(" search-bar-active")
    }

    div {
        className = ClassName(containerClass)
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

    // Backdrop and results overlay when active
    // On desktop, render via portal to escape the header's stacking context
    // (header has will-change: transform which traps position: fixed children)
    if (searchState.isActive && props.behavior != SearchBarBehavior.HIDDEN) {
        if (isDesktop) {
            // Portal to escape the header's stacking context
            +createPortal(
                Fragment.create {
                    SearchBackdrop {
                        this.onClick = {
                            props.viewModel.onEvent(SearchEvent.CancelClicked)
                        }
                    }
                    SearchResultsOverlay {
                        this.results = searchState.results
                        this.isLoading = searchState.isLoading
                        this.onResultClick = { result ->
                            props.onNavigateToProduct(result.productId)
                            props.viewModel.onEvent(SearchEvent.CancelClicked)
                        }
                    }
                },
                document.body
            )
        } else {
            SearchBackdrop {
                this.onClick = {
                    props.viewModel.onEvent(SearchEvent.CancelClicked)
                }
            }
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
