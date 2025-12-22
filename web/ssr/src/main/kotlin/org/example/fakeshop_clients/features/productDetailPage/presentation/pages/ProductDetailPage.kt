package org.example.fakeshop_clients.features.productDetailPage.presentation.pages

import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title
import org.example.fakeshop_clients.features.core.navigation.desktop.desktopNavigation
import org.example.fakeshop_clients.features.core.navigation.mobile.bottomNavigation
import org.example.fakeshop_clients.features.productDetailPage.domain.models.FullProduct
import org.example.fakeshop_clients.features.productDetailPage.presentation.likeButton

fun HTML.productDetailPage(product: FullProduct) {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +product.name }

        // ===== PRELOAD ISLAND BUNDLE =====
        link(rel = "preload", href = "/static/js/islands-bundle.js") {
            attributes["as"] = "script"
            attributes["crossorigin"] = ""
        }

        // Prefetch SPA bundle
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

        // Shared CSS from webCommon
        link(rel = "stylesheet", href = "/common/css/theme.css")
        link(rel = "stylesheet", href = "/common/css/base.css")
        link(rel = "stylesheet", href = "/common/css/components.css")
        link(rel = "stylesheet", href = "/common/css/navigation.css")
        link(rel = "stylesheet", href = "/common/css/view-transitions.css")
        link(rel = "stylesheet", href = "/common/css/search-bar.css")

        // Page-specific CSS
        link(rel = "stylesheet", href = "/static/css/product-details.css")
    }

    body {
        attributes["data-page"] = "product-detail"
        // Header with search island
        header(classes = "header") {
            attributes["data-scroll-behavior"] = "scroll-reactive"
            div(classes = "container header-content") {

                button(classes = "back-button") {
                    onClick = "window.history.back()"
                    +"← Back"
                }

                h1(classes = "logo") {
                    a(href = "/") {
                        +"E-Shop"
                    }
                }

                // ===== SEARCH ISLAND CONTAINER =====
                div {
                    id = "search-island"
                    attributes["data-island"] = "search"
                    classes = setOf("island-container", "header-center")

                    // React will hydrate here
                    div {
                        id = "search-island-root"
                        attributes["data-hydrate"] = "true"
                    }
                }

                // Desktop navigation
                desktopNavigation(activeTab = null)
            }
        }

        // Main Content
        main(classes = "main-content") {
            div(classes = "container") {

                div(classes = "product-detail") {
                    // Product Image
                    div(classes = "product-image-section") {
                        img(src = product.imageUrl, alt = product.name, classes = "product-image")
                    }

                    // Product Info
                    div(classes = "product-info-section") {
                        span(classes = "product-category") {
                            +product.category
                        }

                        h1(classes = "product-title") {
                            +product.name
                        }

                        // Rating
                        div(classes = "product-rating") {
                            span(classes = "stars") {
                                repeat(5) { index ->
                                    if (index < product.rating.toInt()) {
                                        +"★"
                                    } else {
                                        +"☆"
                                    }
                                }
                            }
                            span(classes = "rating-text") {
                                +"${product.rating} (${product.reviews} reviews)"
                            }
                        }

                        p(classes = "product-price") {
                            +"$${String.format("%.2f", product.price)}"
                        }

                        // Stock status
                        div(classes = if (product.inStock) "in-stock" else "out-of-stock") {
                            +if (product.inStock) "In Stock" else "Out of Stock"
                        }

                        // Description
                        p(classes = "product-description") {
                            +product.description
                        }

                        // Actions
                        div(classes = "product-actions") {
                            // Like button with HTMX
                            likeButton(product)

                            // Add to cart button
                            if (product.inStock) {
                                button(classes = "btn btn-primary") {
                                    +"Add to Cart"
                                }
                            } else {
                                button(classes = "btn btn-disabled") {
                                    attributes["disabled"] = "true"
                                    +"Out of Stock"
                                }
                            }
                        }
                    }
                }
            }
        }

        // Footer
        footer(classes = "footer") {
            div(classes = "container") {
                p { +"© 2024 E-Shop. Built with Kotlin Multiplatform + HTMX" }
            }
        }

        // Bottom Navigation
        bottomNavigation(activeTab = null)

        // ===== LOAD ISLAND BUNDLE =====
        script(src = "/static/js/islands-bundle.js") {
            attributes["type"] = "module"
        }

        // ===== HYDRATOR SCRIPT =====
        script(src = "/static/js/universal-hydrator.js") {}
        script(src = "/static/js/view-transitions.js") {}

        // ===== HEADER SCROLL BEHAVIOR (Desktop only) =====
        script(src = "/static/js/header-scroll.js") {}
    }
}