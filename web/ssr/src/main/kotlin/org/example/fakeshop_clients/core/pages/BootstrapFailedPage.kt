package org.example.fakeshop_clients.core.pages

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe
import org.example.fakeshop_clients.core.assets.AssetManifest

fun HTML.bootstrapFailedPage(locale: String, strings: Map<String, String>) {
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
        link(rel = "stylesheet", href = AssetManifest.commonCss)
    }
    body {
        div(classes = "page-placeholder") {
            p {
                +(strings["bootstrap_failed_body"]
                    ?: "Check your connection and try again.")
            }
            button(classes = "btn-primary") {
                attributes["onclick"] = "window.location.reload()"
                +(strings["bootstrap_failed_retry"] ?: "Retry")
            }
            script {
                unsafe {
                    +"""window.__LOCALE__ = "$locale";"""
                }
            }
        }
    }
}
