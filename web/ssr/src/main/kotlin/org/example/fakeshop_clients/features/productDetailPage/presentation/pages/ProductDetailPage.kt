package org.example.fakeshop_clients.features.productDetailPage.presentation.pages

import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.section
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.unsafe
import org.example.fakeshop_clients.core.assets.AssetManifest
import org.example.fakeshop_clients.core.assets.ExternalScripts
import org.example.fakeshop_clients.core.design.IconPaths
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.core.interactions.domain.InteractionQuery
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.core.ui.svgIcon
import org.example.fakeshop_clients.features.bdui.presentation.render.renderBduiNode
import org.example.fakeshop_clients.features.core.navigation.desktop.desktopNavigation
import org.example.fakeshop_clients.features.core.navigation.mobile.bottomNavigation
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData

fun HTML.productDetailPage(
    pdpData: PdpData,
    locale: String,
    strings: Map<String, String>,
    stringsJson: String
) {
    val brief = pdpData.brief
    lang = locale
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +brief.name }
        link(rel = "icon", type = "image/x-icon", href = "/static/favicon_io/favicon.ico")
        link(
            rel = "icon",
            type = "image/png",
            href = "/static/favicon_io/favicon-32x32.png"
        ) { attributes["sizes"] = "32x32" }
        link(
            rel = "icon",
            type = "image/png",
            href = "/static/favicon_io/favicon-16x16.png"
        ) { attributes["sizes"] = "16x16" }
        link(
            rel = "apple-touch-icon",
            href = "/static/favicon_io/apple-touch-icon.png"
        ) { attributes["sizes"] = "180x180" }
        link(rel = "manifest", href = "/static/favicon_io/site.webmanifest")

        // SEO: hreflang alternate links
        WebStrings.SUPPORTED_LOCALES.forEach { loc ->
            link(rel = "alternate", href = "/$loc/product/${brief.id}") {
                attributes["hreflang"] = loc
            }
        }
        link(rel = "alternate", href = "/en/product/${brief.id}") {
            attributes["hreflang"] = "x-default"
        }

        // ===== PRELOAD ISLAND BUNDLE =====
        link(rel = "preload", href = AssetManifest.islandsBundle) {
            attributes["as"] = "script"
            attributes["crossorigin"] = ""
        }

        // Prefetch SPA bundle
        link(rel = "prefetch", href = AssetManifest.spaBundle) {
            attributes["as"] = "script"
        }

        // HTMX
        script(src = ExternalScripts.HTMX_SRC) {
            attributes["integrity"] = ExternalScripts.HTMX_INTEGRITY
            attributes["crossorigin"] = "anonymous"
            attributes["defer"] = ""
        }

        // Fonts: self-hosted Inter (@font-face in base.css). Preload the primary latin subset so it
        // downloads before the CSS bundle is parsed; crossorigin is required even for same-origin fonts.
        link(rel = "preload", href = "/static/fonts/inter-latin-wght-normal.woff2") {
            attributes["as"] = "font"
            attributes["type"] = "font/woff2"
            attributes["crossorigin"] = ""
        }

        // CSS Bundles (split bundle approach for optimal caching)
        link(rel = "stylesheet", href = AssetManifest.commonCss)         // Cached across all pages
        link(rel = "stylesheet", href = AssetManifest.productDetailCss)  // Product detail specific

        // Inject locale and strings for client-side use (islands)
        script {
            unsafe {
                +"""window.__LOCALE__ = "${locale}"; window.__STRINGS__ = ${stringsJson};"""
            }
        }
    }

    body {
        attributes["data-page"] = "product-detail"
        // Header with search island
        header(classes = "header") {
            attributes["data-scroll-behavior"] = "scroll-reactive"
            div(classes = "container header-content") {

                h1(classes = "logo") {
                    a(href = "/$locale/") {
                        +(strings["app_name"] ?: "E-Shop")
                    }
                }

                // ===== SEARCH ISLAND CONTAINER =====
                div {
                    id = "search-island-root"
                    classes = setOf("island-container", "header-center")
                }

                // Desktop navigation
                desktopNavigation(
                    activeTab = null,
                    locale = locale,
                    strings = strings,
                    currentPath = "/$locale/product/${brief.id}"
                )
            }
        }

        // Main Content
        main(classes = "main-content") {
            div(classes = "product-detail") {
                // Top section: image + basic info (side-by-side on desktop)
                div(classes = "product-detail-top") {
                    // Product Image Section with carousel and like button overlay
                    div(classes = "product-image-section") {
                        // Like button - HTMX loads real state on first intersection
                        button(classes = "btn-like-circle") {
                            id = "like-button"
                            attributes["hx-get"] = "/product/like/${brief.id}"
                            attributes["hx-trigger"] = "intersect once"
                            attributes["hx-swap"] = "outerHTML"
                            svgIcon(pathData = IconPaths.HEART, cssClass = "like-icon")
                        }

                        // Carousel: main image + gallery images
                        val allImages = listOf(brief.imageUrl) + pdpData.galleryUrls
                        div(classes = "carousel-track") {
                            allImages.forEach { url ->
                                img(src = url, alt = brief.name, classes = "carousel-slide")
                            }
                        }

                        // Arrow buttons (visible on desktop only via CSS)
                        if (allImages.size > 1) {
                            button(classes = "carousel-arrow carousel-arrow-prev") {
                                attributes["aria-label"] = "Previous image"
                                unsafe { +"&#8249;" }
                            }
                            button(classes = "carousel-arrow carousel-arrow-next") {
                                attributes["aria-label"] = "Next image"
                                unsafe { +"&#8250;" }
                            }

                            // Dot indicators
                            div(classes = "carousel-dots") {
                                allImages.forEachIndexed { index, _ ->
                                    span(classes = if (index == 0) "carousel-dot active" else "carousel-dot")
                                }
                            }
                        }
                    }

                    // Product Info (category, name, price)
                    div(classes = "product-info-section") {
                        span(classes = "product-category") {
                            +brief.category
                        }

                        h1(classes = "product-title") {
                            +brief.name
                        }

                        p(classes = "product-price") {
                            +"$${String.format("%.2f", brief.price)}"
                        }
                    }
                }

                // BDUI body — server-driven bottom half
                div(classes = "product-detail-bdui") {
                    renderBduiNode(pdpData.template.root, pdpData.bindData, "pdp", brief.id, locale)
                }

                // Similar products shelf — server-rendered, so it needs no JS and is in the
                // initial HTML.
                similarProductsShelf(
                    products = pdpData.recommendations,
                    locale = locale,
                    strings = strings
                )

                // Toast region for BDUI action feedback (HTMX swaps content here)
                div {
                    id = "bdui-toast-region"
                    classes = setOf("bdui-toast-region")
                }
            }
        }

        // Footer
        footer(classes = "footer") {
            div(classes = "container") {
                p { +"© ${strings["footer_copyright"] ?: "2024 E-Shop. Built with Kotlin Multiplatform"}" }
            }
        }

        // Bottom Navigation
        bottomNavigation(activeTab = null, locale = locale, strings = strings)

        // ===== LOAD ISLAND BUNDLE =====
        script(src = AssetManifest.islandsBundle) {
            attributes["type"] = "module"
        }

        // ===== HYDRATOR SCRIPT =====
        script(src = "/static/js/universal-hydrator.js") {}
        script(src = "/static/js/view-transitions.js") {}

        // ===== BDUI selection — driven by click, not by server response =====
        // Navigate to new URL when a size or color chip is clicked in future
        script {
            unsafe {
                +"""
document.addEventListener('click', function(e) {
    var chip = e.target.closest('.bdui-size-chip');
    if (chip) {
        chip.closest('.bdui-size-selector').querySelectorAll('.bdui-size-chip').forEach(function(c) {
            c.classList.remove('selected');
            c.setAttribute('aria-pressed', 'false');
        });
        chip.classList.add('selected');
        chip.setAttribute('aria-pressed', 'true');
    }

    var swatch = e.target.closest('.bdui-color-swatch');
    if (swatch) {
        swatch.closest('.bdui-color-swatch-picker').querySelectorAll('.bdui-color-swatch').forEach(function(s) {
            s.classList.remove('selected');
        });
        swatch.classList.add('selected');
    }

    // BDUI navigate on subtrees with interactive content render as [data-href] divs
    // (a real <a> would nest interactive HTML). Innermost handler wins: if the nearest
    // interactive ancestor of the click is a link/button, it handles the click itself.
    var tappable = e.target.closest('a,button,[data-href]');
    if (tappable && tappable.hasAttribute('data-href')) {
        var href = tappable.getAttribute('data-href');
        if (tappable.getAttribute('data-replace') === 'true') {
            window.location.replace(href);
        } else {
            window.location.assign(href);
        }
    }
});
""".trimIndent()
            }
        }

        // ===== CAROUSEL (arrow nav + dot sync) =====
        script(src = "/static/js/carousel.js") {}

        // ===== HEADER SCROLL BEHAVIOR (Desktop only) =====
        script(src = "/static/js/header-scroll.js") {}

        // ===== BROWSING SESSION CLOCK (SSR mints, the browser maintains) =====
        script(src = "/static/js/session-id.js") {}
    }
}

fun FlowContent.similarProductsShelf(
    products: List<BriefProduct>,
    locale: String,
    strings: Map<String, String>
) {
    if (products.isEmpty()) return

    section(classes = "similar-products") {
        h2(classes = "similar-products-title") {
            +(strings["similar_products"] ?: "Similar products")
        }

        div(classes = "products-row") {
            products.forEachIndexed { index, product ->
                a(
                    href = InteractionQuery.productDetailPath(
                        locale = locale,
                        productId = product.id,
                        surface = InteractionSurface.RECOMMENDATIONS,
                        // 0-based rank within the shelf: gone the moment the page is rendered.
                        position = index
                    ),
                    classes = "product-card"
                ) {
                    div(classes = "product-image-container") {
                        img(src = product.imageUrl, alt = product.name, classes = "product-image")
                    }

                    div(classes = "product-info") {
                        div(classes = "product-name") { +product.name }
                        div(classes = "product-price") { +"$${String.format("%.2f", product.price)}" }
                    }
                }
            }
        }
    }
}
