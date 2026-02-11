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

// CSS bundles are now generated and copied by web:common:copyBundledCss
// This runs automatically when building the common module
tasks.named("processResources") {
    dependsOn(":web:common:copyBundledCss")
    dependsOn(":shared:generateWebStrings")
    dependsOn(":web:webApp:copySpaResources")
    dependsOn(":web:islands:copyIslandResources")
}