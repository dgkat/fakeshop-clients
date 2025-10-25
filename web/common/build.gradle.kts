plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser()
    }

    jvm()

    sourceSets {
        commonMain {
            resources.srcDir("src/commonMain/resources")
        }

        val jsMain by getting {
        }

        val jvmMain by getting {
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}