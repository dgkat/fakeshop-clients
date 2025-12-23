package org.example.fakeshop_clients.core.presentation.components

import kotlinx.browser.window
import org.example.fakeshop_clients.core.navigation.desktop.DesktopNav
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.components.SearchBar
import org.example.fakeshop_clients.features.search.presentation.hooks.useScrollOffset
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.header
import react.useEffect
import react.useRef
import react.useState
import web.cssom.ClassName
import web.html.HTMLElement

external interface HeaderProps : Props {
    var behavior: String? // "scroll-reactive", "static", or "hidden"
    var searchViewModel: SearchViewModel
    var searchBehavior: SearchBarBehavior
    var onNavigateToProduct: (String) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val headerRef = useRef<HTMLElement>(null)

    // Check if we're on desktop (>= 768px)
    val isDesktop by useState { window.matchMedia("(min-width: 768px)").matches }

    // Reuse existing scroll offset hook (same as SearchBar uses on mobile)
    val scrollState = useScrollOffset(maxOffset = 100.0) // header height

    // Apply transform on desktop (same pattern as SearchBar does on mobile)
    useEffect(scrollState.offset, isDesktop, props.behavior) {
        if (isDesktop && props.behavior == "scroll-reactive") {
            // Apply same transform pattern as SearchBar on mobile
            headerRef.current?.style?.transform = "translateY(${scrollState.offset}px)"
        } else {
            // Mobile or non-scroll-reactive: no transform
            headerRef.current?.style?.transform = "translateY(0px)"
        }
    }

    header {
        className = ClassName("header")
        ref = headerRef

        div {
            className = ClassName("container header-content")
            h1 {
                className = ClassName("logo")
                a {
                    href = "/"
                    +"E-Shop"
                }
            }

            // SearchBar component rendered inside header
            SearchBar {
                this.viewModel = props.searchViewModel
                this.behavior = props.searchBehavior
                this.onNavigateToProduct = props.onNavigateToProduct
            }

            DesktopNav()
        }
    }
}