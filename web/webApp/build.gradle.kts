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
            implementation(project(":web:searchCommon"))
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

tasks.register("generateWebpackEnv") {
    val url = project.findProperty("backendBaseUrl")?.toString() ?: ""
    inputs.property("backendBaseUrl", url)
    val outputFile = layout.projectDirectory.file("webpack.config.d/env.js")
    outputs.file(outputFile)
    doLast {
        outputFile.asFile.writeText(
            """
            config.plugins = (config.plugins || []).concat([
                new (require('webpack')).DefinePlugin({
                    '__BACKEND_BASE_URL__': JSON.stringify('$url')
                })
            ]);
            """.trimIndent()
        )
    }
}

tasks.named("jsBrowserProductionWebpack") { dependsOn("generateWebpackEnv") }
tasks.named("jsBrowserDevelopmentWebpack") { dependsOn("generateWebpackEnv") }

tasks.register<Copy>("copySpaBundle") {
    dependsOn("jsBrowserProductionWebpack")
    dependsOn("copySpaResources")

    val backendBaseUrl = project.findProperty("backendBaseUrl")

    doFirst {
        if (backendBaseUrl == null) {
            throw GradleException(
                "\n\n  'backendBaseUrl' property is required for production builds.\n" +
                "  Pass it with: ./gradlew :web:webApp:copySpaBundle -PbackendBaseUrl=https://your-api.com\n"
            )
        }
    }

    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    val jsDir = project(":web:ssr").projectDir.resolve("src/main/resources/static/js")
    into(jsDir)

    include("*.js")

    doLast {
        println("✅ SPA bundle copied to SSR static folder")
        val hashedPattern = Regex("spa-bundle\\.[a-f0-9]{8}\\.js")
        val bundles = jsDir.listFiles { f -> f.name.matches(hashedPattern) }?.toList() ?: emptyList()
        val bundle = bundles.maxByOrNull { it.lastModified() }
            ?: error("Hashed spa-bundle not found in $jsDir")
        // Remove stale hashed bundles left from previous builds
        bundles.filter { it != bundle }.forEach { stale ->
            stale.delete()
            jsDir.resolve("${stale.name}.map").delete()
        }
        // Remove source map for current bundle if it exists from a previous build
        jsDir.resolve("${bundle.name}.map").delete()
        jsDir.resolve("spa-manifest.json").writeText("""{"spa-bundle.js":"${bundle.name}"}""")
        println("✅ spa-manifest.json written → ${bundle.name}")
    }
}