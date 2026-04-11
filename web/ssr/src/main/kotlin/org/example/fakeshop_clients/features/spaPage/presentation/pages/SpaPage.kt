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

fun HTML.spaPage(locale: String, strings: Map<String, String>, stringsJson: String) {
    lang = locale
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +(strings["app_name"] ?: "E-Shop") }

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

        // Firebase Cloud Messaging config. Replace placeholder values with
        // your project's config from Firebase Console (Project settings → Web app),
        // and the VAPID public key from Project settings → Cloud Messaging → Web Push certificates.
        script {
            unsafe {
                +"""
                window.__FIREBASE_CONFIG__ = {
                    apiKey: "AIzaSyD9SCsHb9x0cQzmv-E9heCkj1kg36Bf_68",
                    authDomain: "fakeshop-dev.firebaseapp.com",
                    projectId: "fakeshop-dev",
                    storageBucket: "fakeshop-dev.firebasestorage.app",
                    messagingSenderId: "606435380761",
                    appId: "1:606435380761:web:db13728b8ee53a4e2fa488"
                };
                window.__FIREBASE_VAPID_KEY__ = "BOcWNVeCZGwz7_CFGRAkdZ8UkU0H9y6S6U99-FJbEErgZf8nC1wZ1z3kttIofT032n_0cL6k1LEPi3NKpSsSzuc";
                """.trimIndent()
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
