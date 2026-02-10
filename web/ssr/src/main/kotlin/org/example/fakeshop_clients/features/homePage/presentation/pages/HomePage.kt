package org.example.fakeshop_clients.features.homePage.presentation.pages

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import org.example.fakeshop_clients.core.strings.Strings
import org.example.fakeshop_clients.features.core.navigation.desktop.desktopNavigation
import org.example.fakeshop_clients.features.core.navigation.mobile.bottomNavigation

fun HTML.homePage() {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +Strings.HOME_PAGE_TITLE }

        // ===== PRELOAD ISLAND BUNDLES =====
        link(rel = "preload", href = "/static/js/islands-bundle.js") {
            attributes["as"] = "script"
            attributes["crossorigin"] = ""
        }

        // PREFETCH SPA BUNDLE for smooth transition to SPA pages
        link(rel = "prefetch", href = "/static/js/spa-bundle.js") {
            attributes["as"] = "script"
        }

        // HTMX
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}

        // ===== REACT (needed for islands) =====
        script(src = "https://unpkg.com/react@18/umd/react.development.js") {}
        script(src = "https://unpkg.com/react-dom@18/umd/react-dom.development.js") {}

        // Google Fonts
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = ""
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
        )

        // CSS Bundles (split bundle approach for optimal caching)
        link(rel = "stylesheet", href = "/static/css/bundles/common.css")  // Cached across all pages
        link(rel = "stylesheet", href = "/static/css/bundles/home.css")    // Home page specific
    }

    body {
        attributes["data-page"] = "home"
        // Header with E-Shop branding and search island
        header(classes = "header") {
            attributes["data-scroll-behavior"] = "scroll-reactive"
            div(classes = "container header-content") {
                h1(classes = "logo") { +Strings.APP_NAME }

                // ===== SEARCH ISLAND CONTAINER =====
                div {
                    id = "search-island-root"
                    classes = setOf("island-container", "header-center")
                }

                // Desktop navigation
                desktopNavigation(activeTab = "home")
            }
        }

        // Main Content with product list island
        div(classes = "main-content") {
            div(classes = "container") {
                // ===== PRODUCT LIST ISLAND CONTAINER =====
                div {
                    id = "product-list-island-root"
                    classes = setOf("island-container")
                }
            }
        }

        // Footer
        footer(classes = "footer") {
            div(classes = "container") {
                p { +"© ${Strings.FOOTER_COPYRIGHT}" }
            }
        }

        // Bottom Navigation (Mobile only)
        bottomNavigation(activeTab = "home")

        // ===== LOAD ISLAND BUNDLES =====
        script(src = "/static/js/islands-bundle.js") {
            attributes["type"] = "module"
        }

        // ===== UNIVERSAL HYDRATOR SCRIPT =====
        script(src = "/static/js/universal-hydrator.js") {}

        // ===== HEADER SCROLL BEHAVIOR (Desktop only) =====
        script(src = "/static/js/header-scroll.js") {}
    }
}