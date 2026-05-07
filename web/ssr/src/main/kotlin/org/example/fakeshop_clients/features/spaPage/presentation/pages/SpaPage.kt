package org.example.fakeshop_clients.features.spaPage.presentation.pages

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe
import org.example.fakeshop_clients.core.assets.AssetManifest
import org.example.fakeshop_clients.core.i18n.WebStrings

fun HTML.spaPage(
    locale: String,
    strings: Map<String, String>,
    stringsJson: String,
    firebaseConfigJson: String,
    firebaseVapidKeyJson: String,
) {
    lang = locale
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +(strings["app_name"] ?: "E-Shop") }
        link(rel = "icon", type = "image/x-icon", href = "/static/favicon_io/favicon.ico")
        link(rel = "icon", type = "image/png", href = "/static/favicon_io/favicon-32x32.png") { attributes["sizes"] = "32x32" }
        link(rel = "icon", type = "image/png", href = "/static/favicon_io/favicon-16x16.png") { attributes["sizes"] = "16x16" }
        link(rel = "apple-touch-icon", href = "/static/favicon_io/apple-touch-icon.png") { attributes["sizes"] = "180x180" }
        link(rel = "manifest", href = "/static/favicon_io/site.webmanifest")

        // SEO: hreflang alternate links
        WebStrings.SUPPORTED_LOCALES.forEach { loc ->
            link(rel = "alternate", href = "/$loc/favorites") {
                attributes["hreflang"] = loc
            }
        }
        link(rel = "alternate", href = "/en/favorites") {
            attributes["hreflang"] = "x-default"
        }

        // Fonts
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
        link(rel = "stylesheet", href = AssetManifest.spaCss)     // SPA specific

        // Inject locale and strings for client-side use (SPA)
        script {
            unsafe {
                +"""window.__LOCALE__ = "${locale}"; window.__STRINGS__ = ${stringsJson};"""
            }
        }

        // Firebase Cloud Messaging config — injected from application.conf / env vars.
        // These values are public (they ship to every browser); prod overrides are set
        // via FIREBASE_* env vars to point at a different Firebase project than dev.
        script {
            unsafe {
                +"window.__FIREBASE_CONFIG__ = $firebaseConfigJson; window.__FIREBASE_VAPID_KEY__ = $firebaseVapidKeyJson;"
            }
        }
    }

    body {
        attributes["data-page"] = "spa"

        // Root for React SPA
        div {
            id = "spa-root"
        }

        // Load SPA bundle
        script(src = AssetManifest.spaBundle) {
            attributes["type"] = "module"
        }
        script(src = "/static/js/view-transitions.js") {}
    }
}
