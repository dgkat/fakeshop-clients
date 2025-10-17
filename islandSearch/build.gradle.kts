plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "search-island.js"
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlin.react)
            implementation(libs.kotlin.react.dom)
        }
    }
}

tasks.register<Copy>("copyIslandBundle") {
    dependsOn("jsBrowserProductionWebpack")

    // Update the source path to match actual webpack output
    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    into(project(":ssr").projectDir.resolve("src/main/resources/static/js"))

    include("*.js", "*.js.map")

    doLast {
        println("✅ Island bundle copied to SSR static folder")
    }
}