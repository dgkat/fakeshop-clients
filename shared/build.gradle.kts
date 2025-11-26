import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SuspendInterop
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.skieLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(libs.koin.core)
        }
    }

    js(IR) {
        outputModuleName = "shared"
        browser()
        binaries.library()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Koin DI
            api(libs.koin.core)

            // DateTime
            implementation(libs.kotlinx.datetime)

            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // Ktor dependencies shared between Android and iOS
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.auth)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(mobileMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }

        jsMain.dependencies {
            implementation(npm("axios", "1.13.2"))
        }
    }
}

android {
    namespace = "org.example.fakeshop_clients.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

skie {
    isEnabled = false
    features {
        group {
            // Enable Flow interop - makes Flow → AsyncSequence
            FlowInterop.Enabled(true)
            // Enable suspend interop - makes suspend → async
            SuspendInterop.Enabled(true)
        }
    }
}