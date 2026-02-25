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
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe
import org.example.fakeshop_clients.core.assets.AssetManifest
import org.example.fakeshop_clients.core.assets.ExternalScripts
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.features.core.navigation.desktop.desktopNavigation
import org.example.fakeshop_clients.features.core.navigation.mobile.bottomNavigation

fun HTML.homePage(locale: String, strings: Map<String, String>, stringsJson: String) {
    lang = locale
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +(strings["home_page_title"] ?: "E-Shop Home") }

        // SEO: hreflang alternate links
        WebStrings.SUPPORTED_LOCALES.forEach { loc ->
            link(rel = "alternate", href = "/$loc/") {
                attributes["hreflang"] = loc
            }
        }
        link(rel = "alternate", href = "/en/") {
            attributes["hreflang"] = "x-default"
        }

        // ===== PRELOAD ISLAND BUNDLES =====
        link(rel = "preload", href = AssetManifest.islandsBundle) {
            attributes["as"] = "script"
            attributes["crossorigin"] = ""
        }

        // PREFETCH SPA BUNDLE for smooth transition to SPA pages
        link(rel = "prefetch", href = AssetManifest.spaBundle) {
            attributes["as"] = "script"
        }

        // HTMX
        script(src = ExternalScripts.HTMX_SRC) {
            attributes["integrity"] = ExternalScripts.HTMX_INTEGRITY
            attributes["crossorigin"] = "anonymous"
            attributes["defer"] = ""
        }

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
        link(rel = "stylesheet", href = AssetManifest.commonCss)  // Cached across all pages
        link(rel = "stylesheet", href = AssetManifest.homeCss)    // Home page specific

        // Inject locale and strings for client-side use (islands)
        script {
            unsafe {
                +"""window.__LOCALE__ = "${locale}"; window.__STRINGS__ = ${stringsJson};"""
            }
        }
    }

    body {
        attributes["data-page"] = "home"
        // Header with E-Shop branding and search island
        header(classes = "header") {
            attributes["data-scroll-behavior"] = "scroll-reactive"
            div(classes = "container header-content") {
                h1(classes = "logo") { +(strings["app_name"] ?: "E-Shop") }

                // ===== SEARCH ISLAND CONTAINER =====
                div {
                    id = "search-island-root"
                    classes = setOf("island-container", "header-center")
                }

                // Desktop navigation
                desktopNavigation(activeTab = "home", locale = locale, strings = strings)
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
                p { +"© ${strings["footer_copyright"] ?: "2024 E-Shop. Built with Kotlin Multiplatform"}" }
            }
        }

        // Bottom Navigation (Mobile only)
        bottomNavigation(activeTab = "home", locale = locale, strings = strings)

        // ===== LOAD ISLAND BUNDLES =====
        script(src = AssetManifest.islandsBundle) {
            attributes["type"] = "module"
        }

        // ===== UNIVERSAL HYDRATOR SCRIPT =====
        script(src = "/static/js/universal-hydrator.js") {}

        // ===== HEADER SCROLL BEHAVIOR (Desktop only) =====
        script(src = "/static/js/header-scroll.js") {}
    }
}
