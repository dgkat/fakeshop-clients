plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "fakeshop.js"
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
tasks.register<Copy>("copyWebCommonResources") {
    from("${project(":web:common").projectDir}/src/commonMain/resources")
    into("${projectDir}/src/jsMain/resources/common")
}

tasks.named("jsProcessResources") {
    dependsOn("copyWebCommonResources")
}