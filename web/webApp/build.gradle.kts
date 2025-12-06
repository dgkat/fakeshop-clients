plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "spa-bundle.js"
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            // Kotlin wrappers for React
            implementation(libs.kotlin.react)
            implementation(libs.kotlin.react.dom)
            implementation(libs.kotlin.react.router.dom)
            implementation(libs.kotlin.emotion)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Koin
            implementation(libs.koin.core)

            // Shared modules
            implementation(project(":web:common"))
            implementation(project(":shared"))
        }
    }
}

// Copy webCommon CSS to webApp resources
/*tasks.register<Copy>("copyWebCommonResources") {
    from("${project(":web:common").projectDir}/src/commonMain/resources")
    into("${projectDir}/src/jsMain/resources/common")
}

tasks.named("jsProcessResources") {
    dependsOn("copyWebCommonResources")
}*/

tasks.register<Copy>("copySpaResources") {
    from("${project(":web:webApp").projectDir}/src/jsMain/resources")
    into("${project(":web:ssr").projectDir}/src/main/resources/common")
}

tasks.register<Copy>("copySpaBundle") {
    dependsOn("jsBrowserProductionWebpack")
    dependsOn("copySpaResources")

    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    into(project(":web:ssr").projectDir.resolve("src/main/resources/static/js"))

    include("*.js", "*.js.map")

    doLast {
        println("✅ SPA bundle copied to SSR static folder")
    }
}