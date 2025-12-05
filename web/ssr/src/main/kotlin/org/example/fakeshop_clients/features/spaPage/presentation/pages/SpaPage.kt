package org.example.fakeshop_clients.features.spaPage.presentation.pages

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title

fun HTML.spaPage() {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title { +"E-Shop" }

        // React
        script(src = "https://unpkg.com/react@18/umd/react.development.js") {}
        script(src = "https://unpkg.com/react-dom@18/umd/react-dom.development.js") {}

        // React Router for client-side routing
        script(src = "https://unpkg.com/react-router-dom@6/dist/umd/react-router-dom.production.min.js") {}

        // Fonts
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = ""
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
        )

        // CSS
        link(rel = "stylesheet", href = "/common/css/theme.css")
        link(rel = "stylesheet", href = "/common/css/base.css")
        link(rel = "stylesheet", href = "/common/css/components.css")
        link(rel = "stylesheet", href = "/common/css/navigation.css")
        link(rel = "stylesheet", href = "/common/css/view-transitions.css")
    }

    body {
        attributes["data-page"] = "spa"

        // Root for React SPA
        div {
            id = "spa-root"
        }

        // Load SPA bundle
        script(src = "/static/js/spa-bundle.js") {
            attributes["type"] = "module"
        }
        script(src = "/static/js/view-transitions.js") {}
    }
}