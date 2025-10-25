package org.example.fakeshop_clients

import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.title
import kotlinx.html.ul
import org.example.fakeshop_clients.features.homePage.presentation.homeRoute
import org.example.fakeshop_clients.features.productDetailPage.presentation.productRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        // Serve static files (CSS, images, etc.)
        staticResources("/webcommon", "webcommon") {
            // This will serve files from webCommon module's resources
        }

        staticResources("/static", "static")

        // Home page
        homeRoute()
        // Product routes
        productRoutes()
    }
}