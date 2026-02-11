package org.example.fakeshop_clients

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.example.fakeshop_clients.core.di.jvmInfrastructureModule
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.features.homePage.presentation.homeRoute
import org.example.fakeshop_clients.features.productDetailPage.di.ssrProductDetailModule
import org.example.fakeshop_clients.features.productDetailPage.presentation.productApiRoutes
import org.example.fakeshop_clients.features.productDetailPage.presentation.productRoutes
import org.example.fakeshop_clients.features.spaPage.presentation.spaRoutes
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureRouting()
}

fun Application.configureKoin() {
    startKoin {
        modules(
            jvmInfrastructureModule,
            ssrProductDetailModule
        )
    }
}

fun Application.configureRouting() {
    routing {
        // Serve static files (CSS, images, etc.)
        staticResources("/common", "common") {
            // This will serve files from webCommon module's resources
        }

        staticResources("/static", "static")

        // HTMX API routes (no locale prefix)
        productApiRoutes()

        // Bare "/" redirect based on Accept-Language
        get("/") {
            val acceptLanguage = call.request.headers["Accept-Language"]
            val locale = WebStrings.parseAcceptLanguage(acceptLanguage)
            call.respondRedirect("/$locale/")
        }

        // Locale-prefixed routes
        route("/{locale}") {
            // Home page
            homeRoute()
            // Product routes
            productRoutes()
            // Spa routes
            spaRoutes()
        }
    }
}
