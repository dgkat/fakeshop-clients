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
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.example.fakeshop_clients.core.di.jvmInfrastructureModule
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.features.homePage.presentation.homeRoute
import org.example.fakeshop_clients.features.bdui.di.ssrBduiModule
import org.example.fakeshop_clients.features.bdui.presentation.bduiActionRoute
import org.example.fakeshop_clients.features.bdui.presentation.bduiReplaceRoute
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
            ssrBduiModule,
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

private data class AppLinksConfig(
    val androidPackageName: String,
    val androidCertFingerprint: String,
    val appleAppId: String,
)

private fun Application.loadAppLinksConfig(): AppLinksConfig {
    val cfg = environment.config.config("applinks")
    return AppLinksConfig(
        androidPackageName = cfg.property("androidPackageName").getString(),
        androidCertFingerprint = cfg.property("androidCertFingerprint").getString(),
        appleAppId = cfg.property("appleAppId").getString(),
    )
}

private fun assetLinksJson(cfg: AppLinksConfig): String = buildJsonArray {
    addJsonObject {
        putJsonArray("relation") { add("delegate_permission/common.handle_all_urls") }
        putJsonObject("target") {
            put("namespace", "android_app")
            put("package_name", cfg.androidPackageName)
            putJsonArray("sha256_cert_fingerprints") { add(cfg.androidCertFingerprint) }
        }
    }
}.toString()

// Paths mirror AppRouteParser's allowed destinations; "??" matches the 2-letter locale prefix.
private val universalLinkPaths = listOf(
    "/", "/??/",
    "/product/*", "/??/product/*",
    "/favorites", "/??/favorites",
    "/notifications", "/??/notifications",
    "/profile", "/??/profile",
)

private fun appleAppSiteAssociationJson(cfg: AppLinksConfig): String = buildJsonObject {
    putJsonObject("applinks") {
        putJsonArray("apps") {}
        putJsonArray("details") {
            addJsonObject {
                put("appID", cfg.appleAppId)
                putJsonArray("paths") { universalLinkPaths.forEach { add(it) } }
            }
        }
    }
}.toString()

fun Application.configureRouting() {
    val firebase = loadFirebaseWebConfig()
    val applinks = loadAppLinksConfig()
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
        bduiActionRoute()
        bduiReplaceRoute()

        get("/health") {
            call.respondText("OK", status = HttpStatusCode.OK)
        }

        get("/robots.txt") {
            val content = Application::class.java.getResourceAsStream("/robots.txt")
                ?.bufferedReader()?.readText() ?: ""
            call.respondText(content, ContentType.Text.Plain)
        }

        // OS deeplink association files. Must live at the domain root (outside the
        // /{locale} group), be served without redirects, and use application/json.
        get("/.well-known/assetlinks.json") {
            call.respondText(assetLinksJson(applinks), ContentType.Application.Json)
        }
        get("/.well-known/apple-app-site-association") {
            call.respondText(appleAppSiteAssociationJson(applinks), ContentType.Application.Json)
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
