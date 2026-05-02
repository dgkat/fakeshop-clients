package org.example.fakeshop_clients

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
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

private data class FirebaseWebConfig(
    val configJson: String,
    val vapidKeyJson: String,
)

private fun Application.loadFirebaseWebConfig(): FirebaseWebConfig {
    val cfg = environment.config.config("firebase")
    val configMap = mapOf(
        "apiKey" to cfg.property("apiKey").getString(),
        "authDomain" to cfg.property("authDomain").getString(),
        "projectId" to cfg.property("projectId").getString(),
        "storageBucket" to cfg.property("storageBucket").getString(),
        "messagingSenderId" to cfg.property("messagingSenderId").getString(),
        "appId" to cfg.property("appId").getString(),
    )
    val mapSerializer = MapSerializer(String.serializer(), String.serializer())
    return FirebaseWebConfig(
        configJson = Json.encodeToString(mapSerializer, configMap),
        vapidKeyJson = Json.encodeToString(String.serializer(), cfg.property("vapidKey").getString()),
    )
}

fun Application.configureRouting() {
    val firebase = loadFirebaseWebConfig()
    routing {
        // Serve static files (CSS, images, etc.)
        staticResources("/common", "common") {
            // This will serve files from webCommon module's resources
        }

        staticResources("/static", "static") {
            modify { url, call ->
                val filename = url.path.substringAfterLast("/")
                if (filename.matches(Regex(""".*\.[a-f0-9]{8}\.(css|js)$"""))) {
                    call.response.headers.append(HttpHeaders.CacheControl, "public, max-age=31536000, immutable")
                }
            }
        }

        // FCM service worker must be served from the root to claim the whole-origin scope.
        // Served dynamically so the Firebase config can be injected without duplicating it
        // from application.conf into the static file.
        get("/firebase-messaging-sw.js") {
            call.response.headers.append(HttpHeaders.CacheControl, "no-cache")
            call.respondText(firebaseSwScript(firebase.configJson), ContentType.Application.JavaScript)
        }

        // HTMX API routes (no locale prefix)
        productApiRoutes()

        get("/health") {
            call.respondText("OK", status = HttpStatusCode.OK)
        }

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
            spaRoutes(firebase.configJson, firebase.vapidKeyJson)
        }
    }
}
