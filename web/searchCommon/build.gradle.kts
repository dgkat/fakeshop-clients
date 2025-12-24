plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlin.react)
            implementation(libs.kotlin.react.dom)
            implementation(libs.kotlin.emotion)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)

            implementation(project(":web:common"))
            implementation(project(":shared"))
        }
    }
}
