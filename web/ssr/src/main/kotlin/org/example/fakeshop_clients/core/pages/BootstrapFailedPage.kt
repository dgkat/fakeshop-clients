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
        link(rel = "stylesheet", href = AssetManifest.commonCss)
    }
    body {
        div(classes = "page-placeholder") {
            p {
                +(strings["profile_error_network"]
                    ?: "Can't reach the server. Check your connection and try again.")
            }
            button(classes = "btn-primary") {
                attributes["onclick"] = "window.location.reload()"
                +(strings["retry"] ?: "Retry")
            }
            script {
                unsafe {
                    +"""window.__LOCALE__ = "$locale";"""
                }
            }
        }
    }
}
