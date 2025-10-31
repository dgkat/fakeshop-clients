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
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlin.react)
            implementation(libs.kotlin.react.dom)
            implementation(libs.kotlin.emotion)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)

            implementation(project(":web:common"))
            implementation(project(":shared"))
        }
    }
}

tasks.register<Copy>("copyIslandResources") {
    from("${project(":web:islands").projectDir}/src/jsMain/resources")
    into("${project(":web:ssr").projectDir}/src/main/resources/common")
}

tasks.register<Copy>("copyIslandsBundle") {
    dependsOn("jsBrowserProductionWebpack")
    dependsOn("copyIslandResources")

    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    into(project(":web:ssr").projectDir.resolve("src/main/resources/static/js"))

    include("*.js", "*.js.map")

    doLast {
        println("✅ Islands bundle copied to SSR static folder")
    }
}