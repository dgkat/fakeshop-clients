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
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.kotlinx.html)

    //Shared modules
    implementation(project(":web:common"))
    implementation(project(":shared"))
}

// CSS bundles are now generated and copied by web:common:copyBundledCss
// This runs automatically when building the common module
tasks.named("processResources") {
    dependsOn(":web:common:copyBundledCss")
}