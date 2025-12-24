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

// ============================================
// CSS Bundling Configuration
// ============================================

val cssBundles = mapOf(
    // === COMMON BUNDLE (shared across all pages) ===
    "common" to listOf(
        "shared/theme.css",       // Design tokens (colors, spacing, typography)
        "shared/base.css",        // CSS resets, body styles, typography
        "shared/components.css",  // Reusable components (buttons, cards, forms)
        "shared/navigation.css"   // Navigation bar styles
    ),

    // === PAGE-SPECIFIC BUNDLES (loaded per-page) ===
    "home" to listOf(
        "shared/view-transitions.css",  // Page transition animations
        "shared/search-bar.css",        // Search bar component
        "pages/product-list.css"        // Product grid layout
    ),

    "product-detail" to listOf(
        "shared/view-transitions.css",
        "shared/search-bar.css",
        "pages/product-details.css"     // Product detail page layout
    ),

    "spa" to listOf(
        "shared/view-transitions.css",
        "shared/search-bar.css",
        "pages/spa.css",                // SPA-specific styles
        "pages/profile-page.css"        // Profile page styles
    )
)

tasks.register("bundleCss") {
    group = "build"
    description = "Bundle CSS files into common + page-specific bundles"

    val cssSourceDir = file("src/commonMain/resources/css")
    val outputDir = layout.buildDirectory.dir("bundled-css")

    inputs.dir(cssSourceDir)
    outputs.dir(outputDir)

    doLast {
        cssBundles.forEach { (bundleName, cssFiles) ->
            // Read and concatenate CSS files
            val bundleContent = cssFiles.joinToString("\n\n/* ========================================== */\n\n") { cssPath ->
                val cssFile = cssSourceDir.resolve(cssPath)
                if (cssFile.exists()) {
                    "/* ===== ${cssPath.substringAfterLast('/')} ===== */\n${cssFile.readText()}"
                } else {
                    logger.warn("⚠️  WARNING: ${cssPath} not found!")
                    "/* WARNING: ${cssPath} not found */"
                }
            }

            // Write bundle file
            val bundleFile = outputDir.get().file("${bundleName}.css").asFile
            bundleFile.parentFile.mkdirs()
            bundleFile.writeText(bundleContent)

            logger.lifecycle("✅ Created ${bundleName}.css (${bundleContent.lines().size} lines)")
        }

        logger.lifecycle("🎉 All CSS bundles created successfully!")
    }
}

// Copy bundles to SSR static folder
tasks.register<Copy>("copyBundledCss") {
    dependsOn("bundleCss")

    from(layout.buildDirectory.dir("bundled-css"))
    into(project(":web:ssr").projectDir.resolve("src/main/resources/static/css/bundles"))

    doLast {
        logger.lifecycle("✅ CSS bundles copied to SSR static folder")
    }
}

// Run bundling automatically when processing JVM resources
tasks.named("jvmProcessResources") {
    dependsOn("copyBundledCss")
}