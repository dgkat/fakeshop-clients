package com.yourapp.eshop.server.templates

import FullProduct
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
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
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title

fun HTML.productDetailPage(product: FullProduct) {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +product.name }

        // HTMX
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}

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
        link(rel = "stylesheet", href = "/webcommon/css/theme.css")
        link(rel = "stylesheet", href = "/webcommon/css/base.css")
        link(rel = "stylesheet", href = "/webcommon/css/components.css")

        // Page-specific CSS
        link(rel = "stylesheet", href = "/static/css/product-detail.css")
    }

    body {
        // Header
        header(classes = "header") {
            div(classes = "container") {
                a(href = "/") {
                    h1 { +"E-Shop" }
                }
            }
        }

        // Main Content
        main(classes = "main-content") {
            div(classes = "container") {
                // Back button
                a(href = "/", classes = "back-button") {
                    +"← Back to Products"
                }

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
    }
}

fun FlowContent.likeButton(product: FullProduct) {
    button(classes = "btn btn-like ${if (product.isLiked) "liked" else ""}") {
        id = "like-button"

        // HTMX attributes
        attributes["hx-post"] = "/api/products/${product.id}/like"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-target"] = "#like-button"

        span(classes = "like-icon") {
            if (product.isLiked) +"❤️" else +"🤍"
        }
        span(classes = "like-text") {
            +if (product.isLiked) "Liked" else "Like"
        }
    }
}