plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "islands-bundle.js"
                cssSupport {
                    enabled.set(true)
                }

                devServer = devServer?.copy(
                    port = 3000, // pick any available port
                    open = false
                )
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlin.react)
            implementation(libs.kotlin.react.dom)
            implementation(libs.kotlin.react.router.dom)
            implementation(libs.kotlin.emotion)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)

            implementation(project(":web:common"))
            implementation(project(":web:searchCommon"))
            implementation(project(":shared"))
        }
    }
}

tasks.register<Copy>("copyIslandResources") {
    from("${project(":web:islands").projectDir}/src/jsMain/resources")
    into("${project(":web:ssr").projectDir}/src/main/resources/common")
}

tasks.register("generateWebpackEnv") {
    val url = project.findProperty("backendBaseUrl")?.toString() ?: "http://localhost:8080"
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

tasks.register<Copy>("copyIslandsBundle") {
    dependsOn("jsBrowserProductionWebpack")
    dependsOn("copyIslandResources")

    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    into(project(":web:ssr").projectDir.resolve("src/main/resources/static/js"))

    include("*.js", "*.js.map")

    doLast {
        println("✅ Islands bundle copied to SSR static folder")
        val jsDir = project(":web:ssr").projectDir.resolve("src/main/resources/static/js")
        val hashedPattern = Regex("islands-bundle\\.[a-f0-9]{8}\\.js")
        val bundles = jsDir.listFiles { f -> f.name.matches(hashedPattern) }?.toList() ?: emptyList()
        val bundle = bundles.maxByOrNull { it.lastModified() }
            ?: error("Hashed islands-bundle not found in $jsDir")
        // Remove stale hashed bundles left from previous builds
        bundles.filter { it != bundle }.forEach { stale ->
            stale.delete()
            jsDir.resolve("${stale.name}.map").delete()
        }
        jsDir.resolve("islands-manifest.json").writeText("""{"islands-bundle.js":"${bundle.name}"}""")
        println("✅ islands-manifest.json written → ${bundle.name}")
    }
}