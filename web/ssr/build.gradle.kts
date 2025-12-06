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

// Copy webCommon CSS to server resources
tasks.register<Copy>("copyWebCommonResources") {
    from("${project(":web:common").projectDir}/src/commonMain/resources")
    into("${projectDir}/src/main/resources/common")
}

tasks.named("processResources") {
    dependsOn("copyWebCommonResources")
}