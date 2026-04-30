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

    // Default BACKEND_BASE_URL for local development
    val backendUrl = findProperty("backendBaseUrl")?.toString() ?: "http://localhost:8080"
    tasks.named<JavaExec>("run") {
        environment("BACKEND_BASE_URL", backendUrl)
        environment("DEV_PROXY", "true")
    }
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
 * Fetches HTMX from CDN, computes its SHA-384 SRI hash, and rewrites ReactCdn.kt.
 *
 * Note: React 19 removed UMD builds, so React/ReactDOM are bundled by webpack
 * rather than loaded from CDN. Only HTMX uses the CDN + SRI approach.
 *
 * Usage (current version):
 *   ./gradlew :web:ssr:updateCdnHashes
 *
 * Usage (specific version):
 *   ./gradlew :web:ssr:updateCdnHashes -PhtmxVersion=2.0.0
 */
tasks.register("updateCdnHashes") {
    group = "web"
    description = "Fetches HTMX from CDN and regenerates its SRI hash in ReactCdn.kt"

    // Read property at configuration time to satisfy configuration cache
    val htmxVersion = (findProperty("htmxVersion") as String?) ?: "1.9.10"

    val outputFile = layout.projectDirectory.file(
        "src/main/kotlin/org/example/fakeshop_clients/core/assets/ReactCdn.kt"
    )

    outputs.file(outputFile)

    doLast {
        fun fetchHash(url: String): String {
            println("  Fetching $url")
            val bytes = URI(url).toURL().readBytes()
            val digest = MessageDigest.getInstance("SHA-384").digest(bytes)
            return "sha384-" + Base64.getEncoder().encodeToString(digest)
        }

        println("Computing SRI hashes...")
        val htmxHash = fetchHash("https://unpkg.com/htmx.org@$htmxVersion/dist/htmx.min.js")

        outputFile.asFile.writeText(buildString {
            appendLine("package org.example.fakeshop_clients.core.assets")
            appendLine()
            appendLine("/**")
            appendLine(" * CDN URLs and SRI integrity hashes for external scripts loaded from CDN.")
            appendLine(" *")
            appendLine(" * React and ReactDOM are NOT loaded from CDN — React 19 removed UMD builds,")
            appendLine(" * so React is bundled by webpack instead.")
            appendLine(" *")
            appendLine(" * HTMX version: $htmxVersion")
            appendLine(" * To update HTMX and regenerate hashes:")
            appendLine(" *   ./gradlew :web:ssr:updateCdnHashes -PhtmxVersion=X.Y.Z")
            appendLine(" */")
            appendLine("object ExternalScripts {")
            appendLine("    const val HTMX_SRC =")
            appendLine("        \"https://unpkg.com/htmx.org@$htmxVersion/dist/htmx.min.js\"")
            appendLine("    const val HTMX_INTEGRITY =")
            appendLine("        \"$htmxHash\"")
            appendLine("}")
        })

        println("✅ ReactCdn.kt updated — HTMX $htmxVersion")
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