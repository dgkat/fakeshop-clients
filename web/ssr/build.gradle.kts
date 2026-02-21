import java.net.URI
import java.security.MessageDigest
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "org.example.fakeshop_clients"
version = "1.0.0"

application {
    mainClass.set("org.example.fakeshop_clients.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.kotlinx.html)

    // Ktor Client (for making API calls to backend)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Koin for dependency injection
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    //Shared modules
    implementation(project(":web:common"))
    implementation(project(":shared"))
}

/**
 * Fetches all CDN scripts, computes their SHA-384 SRI hashes, and rewrites ReactCdn.kt.
 *
 * Usage (current versions):
 *   ./gradlew :web:ssr:updateCdnHashes
 *
 * Usage (specific versions):
 *   ./gradlew :web:ssr:updateCdnHashes -PreactVersion=18.3.1 -PhtmxVersion=1.9.10 -PreactRouterVersion=6.30.3
 */
tasks.register("updateCdnHashes") {
    group = "web"
    description = "Fetches CDN scripts and regenerates SRI hashes in ReactCdn.kt"

    doLast {
        val reactVersion = project.findProperty("reactVersion")?.toString() ?: "18.3.1"
        val htmxVersion = project.findProperty("htmxVersion")?.toString() ?: "1.9.10"
        val reactRouterVersion = project.findProperty("reactRouterVersion")?.toString() ?: "6.30.3"

        fun fetchHash(url: String): String {
            println("  Fetching $url")
            val bytes = URI(url).toURL().readBytes()
            val digest = MessageDigest.getInstance("SHA-384").digest(bytes)
            return "sha384-" + Base64.getEncoder().encodeToString(digest)
        }

        println("Computing SRI hashes...")
        val reactProdHash    = fetchHash("https://unpkg.com/react@$reactVersion/umd/react.production.min.js")
        val reactDevHash     = fetchHash("https://unpkg.com/react@$reactVersion/umd/react.development.js")
        val reactDomProdHash = fetchHash("https://unpkg.com/react-dom@$reactVersion/umd/react-dom.production.min.js")
        val reactDomDevHash  = fetchHash("https://unpkg.com/react-dom@$reactVersion/umd/react-dom.development.js")
        val htmxHash         = fetchHash("https://unpkg.com/htmx.org@$htmxVersion/dist/htmx.min.js")
        val reactRouterHash  = fetchHash("https://unpkg.com/react-router-dom@$reactRouterVersion/dist/umd/react-router-dom.production.min.js")

        val outputFile = projectDir.resolve(
            "src/main/kotlin/org/example/fakeshop_clients/core/assets/ReactCdn.kt"
        )

        outputFile.writeText(buildString {
            appendLine("package org.example.fakeshop_clients.core.assets")
            appendLine()
            appendLine("/**")
            appendLine(" * Provides React CDN URLs and SRI integrity hashes based on the current environment.")
            appendLine(" *")
            appendLine(" * React version: $reactVersion")
            appendLine(" * To update versions and regenerate hashes:")
            appendLine(" *   ./gradlew :web:ssr:updateCdnHashes -PreactVersion=X.Y.Z -PhtmxVersion=X.Y.Z -PreactRouterVersion=X.Y.Z")
            appendLine(" */")
            appendLine("object ReactCdn {")
            appendLine("    private val isDevelopment: Boolean =")
            appendLine("        System.getProperty(\"io.ktor.development\")?.toBoolean() ?: false")
            appendLine()
            appendLine("    val react: String = if (isDevelopment) {")
            appendLine("        \"https://unpkg.com/react@$reactVersion/umd/react.development.js\"")
            appendLine("    } else {")
            appendLine("        \"https://unpkg.com/react@$reactVersion/umd/react.production.min.js\"")
            appendLine("    }")
            appendLine()
            appendLine("    val reactIntegrity: String = if (isDevelopment) {")
            appendLine("        \"$reactDevHash\"")
            appendLine("    } else {")
            appendLine("        \"$reactProdHash\"")
            appendLine("    }")
            appendLine()
            appendLine("    val reactDom: String = if (isDevelopment) {")
            appendLine("        \"https://unpkg.com/react-dom@$reactVersion/umd/react-dom.development.js\"")
            appendLine("    } else {")
            appendLine("        \"https://unpkg.com/react-dom@$reactVersion/umd/react-dom.production.min.js\"")
            appendLine("    }")
            appendLine()
            appendLine("    val reactDomIntegrity: String = if (isDevelopment) {")
            appendLine("        \"$reactDomDevHash\"")
            appendLine("    } else {")
            appendLine("        \"$reactDomProdHash\"")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("/**")
            appendLine(" * CDN URLs and SRI integrity hashes for non-React external scripts.")
            appendLine(" *")
            appendLine(" * HTMX version: $htmxVersion")
            appendLine(" * React Router DOM version: $reactRouterVersion")
            appendLine(" * To update versions and regenerate hashes:")
            appendLine(" *   ./gradlew :web:ssr:updateCdnHashes -PreactVersion=X.Y.Z -PhtmxVersion=X.Y.Z -PreactRouterVersion=X.Y.Z")
            appendLine(" */")
            appendLine("object ExternalScripts {")
            appendLine("    const val HTMX_SRC =")
            appendLine("        \"https://unpkg.com/htmx.org@$htmxVersion/dist/htmx.min.js\"")
            appendLine("    const val HTMX_INTEGRITY =")
            appendLine("        \"$htmxHash\"")
            appendLine()
            appendLine("    const val REACT_ROUTER_SRC =")
            appendLine("        \"https://unpkg.com/react-router-dom@$reactRouterVersion/dist/umd/react-router-dom.production.min.js\"")
            appendLine("    const val REACT_ROUTER_INTEGRITY =")
            appendLine("        \"$reactRouterHash\"")
            appendLine("}")
        })

        println("✅ ReactCdn.kt updated:")
        println("   React         $reactVersion")
        println("   HTMX          $htmxVersion")
        println("   React Router  $reactRouterVersion")
    }
}

// CSS bundles are now generated and copied by web:common:copyBundledCss
// This runs automatically when building the common module
tasks.named("processResources") {
    dependsOn(":web:common:copyBundledCss")
    dependsOn(":shared:generateWebStrings")
    dependsOn(":web:webApp:copySpaResources")
    dependsOn(":web:islands:copyIslandResources")
    dependsOn(":web:webApp:copySpaBundle")
    dependsOn(":web:islands:copyIslandsBundle")
}