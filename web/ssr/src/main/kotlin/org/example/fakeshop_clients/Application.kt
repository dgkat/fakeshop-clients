package org.example.fakeshop_clients

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
import org.example.fakeshop_clients.features.homePage.presentation.homeRoute
import org.example.fakeshop_clients.features.productDetailPage.presentation.productRoutes
import org.example.fakeshop_clients.features.spaPage.presentation.spaRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        // Serve static files (CSS, images, etc.)
        staticResources("/common", "common") {
            // This will serve files from webCommon module's resources
        }

        staticResources("/static", "static")

        // Home page
        homeRoute()
        // Product routes
        productRoutes()
        // Spa routes
        spaRoutes()
    }
}