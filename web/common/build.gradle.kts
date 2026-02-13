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
// Theme CSS Generation from DesignTokens.kt
// ============================================

val generateThemeCss by tasks.registering {
    group = "build"
    description = "Generates theme.css from shared DesignTokens.kt"

    val tokensFile = rootProject.file(
        "shared/src/commonMain/kotlin/org/example/fakeshop_clients/core/design/DesignTokens.kt"
    )
    val outputFile = file("src/commonMain/resources/css/shared/theme.css")

    inputs.file(tokensFile)
    outputs.file(outputFile)

    doLast {
        val source = tokensFile.readText()

        // --- Parse color constants ---
        val colorRegex = Regex("""const val (\w+)\s*=\s*0x([0-9A-Fa-f]+)L""")

        fun parseColorBlock(blockName: String): Map<String, String> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n        \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()

            return buildMap {
                colorRegex.findAll(block).forEach { match ->
                    val name = match.groupValues[1]
                    val hex = match.groupValues[2]
                    val cssHex = if (hex.length == 8) hex.substring(2) else hex
                    val cssName = name.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
                    put(cssName, "#$cssHex")
                }
            }
        }

        val lightColors = parseColorBlock("Light")
        val darkColors = parseColorBlock("Dark")

        // --- Parse typography/spacing/radius constants ---
        fun parseIntConsts(blockName: String): Map<String, Int> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n    \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()
            val constRegex = Regex("""const val (\w+)\s*=\s*(\d+)""")
            return constRegex.findAll(block).associate { it.groupValues[1] to it.groupValues[2].toInt() }
        }

        fun parseStringConsts(blockName: String): Map<String, String> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n    \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()
            val constRegex = Regex("""const val (\w+)\s*=\s*\n?\s*"([^"]+)"""")
            return constRegex.findAll(block).associate { it.groupValues[1] to it.groupValues[2] }
        }

        val typoInts = parseIntConsts("Typography")
        val typoStrings = parseStringConsts("Typography")
        val spacingInts = parseIntConsts("Spacing")
        val radiusInts = parseIntConsts("BorderRadius")

        fun camelToKebab(s: String) = s.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()

        // --- Build CSS ---
        val css = buildString {
            appendLine("/* ==========================================")
            appendLine("   THEME.CSS - Design Tokens & CSS Variables")
            appendLine("   AUTO-GENERATED from DesignTokens.kt")
            appendLine("   Do not edit manually - run: ./gradlew :web:common:generateThemeCss")
            appendLine("   ========================================== */")
            appendLine()
            appendLine(":root {")

            // Light colors
            appendLine("    /* ===== Color Palette ===== */")
            lightColors.forEach { (name, hex) ->
                appendLine("    --color-$name: $hex;")
            }
            appendLine()

            // Typography
            appendLine("    /* ===== Typography ===== */")
            typoStrings["WebFontFamily"]?.let { font ->
                appendLine("    --font-family-base: $font;")
            }
            appendLine()

            appendLine("    /* Font Sizes */")
            val fontSizeKeys = listOf(
                "DisplayLarge", "DisplayMedium", "DisplaySmall",
                "HeadlineLarge", "HeadlineMedium", "HeadlineSmall",
                "TitleLarge", "TitleMedium", "TitleSmall",
                "BodyLarge", "BodyMedium", "BodySmall",
                "LabelLarge", "LabelMedium", "LabelSmall"
            )
            fontSizeKeys.forEach { key ->
                typoInts[key]?.let { size ->
                    appendLine("    --font-size-${camelToKebab(key)}: ${size}px;")
                }
            }
            appendLine()

            appendLine("    /* Font Weights */")
            val fontWeightMap = mapOf(
                "WeightRegular" to "regular",
                "WeightMedium" to "medium",
                "WeightSemiBold" to "semibold",
                "WeightBold" to "bold"
            )
            fontWeightMap.forEach { (key, cssName) ->
                typoInts[key]?.let { weight ->
                    appendLine("    --font-weight-$cssName: $weight;")
                }
            }
            appendLine()

            appendLine("    /* Line Heights */")
            appendLine("    --line-height-tight: 1.2;")
            appendLine("    --line-height-normal: 1.5;")
            appendLine("    --line-height-relaxed: 1.75;")
            appendLine()

            // Spacing
            appendLine("    /* ===== Spacing ===== */")
            spacingInts.forEach { (name, value) ->
                appendLine("    --spacing-${name.lowercase()}: ${value}px;")
            }
            appendLine()

            // Border Radius
            appendLine("    /* ===== Border Radius ===== */")
            radiusInts.forEach { (name, value) ->
                appendLine("    --radius-${name.lowercase()}: ${value}px;")
            }
            appendLine()

            // Web-only tokens (no mobile equivalent)
            appendLine("    /* ===== Web-Only Tokens ===== */")
            appendLine("    /* Shadows */")
            appendLine("    --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);")
            appendLine("    --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);")
            appendLine("    --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);")
            appendLine("    --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);")
            appendLine()
            appendLine("    /* Transitions */")
            appendLine("    --transition-fast: 150ms ease-in-out;")
            appendLine("    --transition-base: 250ms ease-in-out;")
            appendLine("    --transition-slow: 350ms ease-in-out;")
            appendLine()
            appendLine("    /* Z-Index */")
            appendLine("    --z-index-dropdown: 1000;")
            appendLine("    --z-index-sticky: 1020;")
            appendLine("    --z-index-fixed: 1030;")
            appendLine("    --z-index-modal-backdrop: 1040;")
            appendLine("    --z-index-modal: 1050;")
            appendLine("    --z-index-popover: 1060;")
            appendLine("    --z-index-tooltip: 1070;")
            appendLine()
            appendLine("    /* Breakpoints (for reference in JS) */")
            appendLine("    --breakpoint-mobile: 480px;")
            appendLine("    --breakpoint-tablet: 768px;")
            appendLine("    --breakpoint-desktop: 1024px;")
            appendLine("    --breakpoint-wide: 1200px;")
            appendLine("}")
            appendLine()

            // Dark mode
            appendLine("/* ===== Dark Mode Theme ===== */")
            appendLine("@media (prefers-color-scheme: dark) {")
            appendLine("    :root {")
            darkColors.forEach { (name, hex) ->
                appendLine("        --color-$name: $hex;")
            }
            appendLine()
            appendLine("        --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.3);")
            appendLine("        --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.4), 0 2px 4px -1px rgba(0, 0, 0, 0.3);")
            appendLine("        --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.3);")
            appendLine("        --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.4), 0 10px 10px -5px rgba(0, 0, 0, 0.3);")
            appendLine("    }")
            appendLine("}")
            appendLine()

            // Typography utility classes
            appendLine("/* ===== Typography Classes ===== */")
            data class TypoClass(val className: String, val sizeVar: String, val lineHeight: String, val weight: String)
            val typoClasses = listOf(
                TypoClass("display-large", "display-large", "tight", "regular"),
                TypoClass("headline-medium", "headline-medium", "normal", "regular"),
                TypoClass("headline-small", "headline-small", "normal", "regular"),
                TypoClass("title-large", "title-large", "normal", "medium"),
                TypoClass("title-medium", "title-medium", "normal", "medium"),
                TypoClass("body-large", "body-large", "normal", "regular"),
                TypoClass("body-medium", "body-medium", "normal", "regular"),
                TypoClass("label-small", "label-small", "normal", "medium"),
            )
            typoClasses.forEach { tc ->
                appendLine(".${tc.className} {")
                appendLine("    font-size: var(--font-size-${tc.sizeVar});")
                appendLine("    font-weight: var(--font-weight-${tc.weight});")
                appendLine("    line-height: var(--line-height-${tc.lineHeight});")
                if (tc.className == "label-small") {
                    appendLine("    letter-spacing: 0.5px;")
                }
                appendLine("}")
                appendLine()
            }
        }

        outputFile.writeText(css)
        logger.lifecycle("Generated ${outputFile.name} from DesignTokens.kt")
    }
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
    dependsOn(generateThemeCss)
    group = "build"
    description = "Bundle CSS files into common + page-specific bundles"

    val cssSourceDir = file("src/commonMain/resources/css")
    val outputDir = layout.buildDirectory.dir("bundled-css")

    inputs.dir(cssSourceDir)
    outputs.dir(outputDir)

    doLast {
        val bundles = mapOf(
            "common" to listOf(
                "shared/theme.css",
                "shared/base.css",
                "shared/components.css",
                "shared/navigation.css"
            ),
            "home" to listOf(
                "shared/view-transitions.css",
                "shared/search-bar.css",
                "pages/product-list.css"
            ),
            "product-detail" to listOf(
                "shared/view-transitions.css",
                "shared/search-bar.css",
                "pages/product-details.css"
            ),
            "spa" to listOf(
                "shared/view-transitions.css",
                "shared/search-bar.css",
                "pages/spa.css",
                "pages/profile-page.css"
            )
        )
        bundles.forEach { (bundleName, cssFiles) ->
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
    dependsOn(":shared:generateWebStrings")
}

// ============================================
// Run full web app with one command
// ============================================

tasks.register("runWeb") {
    group = "application"
    description = "Clean, generate strings, build JS bundles, and run the web SSR server"

    dependsOn(":clean")
    dependsOn(":web:ssr:run")
}

// Ensure :clean finishes before any build tasks start
listOf(
    ":shared:generateWebStrings",
    ":web:common:generateThemeCss",
    ":web:common:bundleCss",
    ":web:common:copyBundledCss",
    ":web:islands:copyIslandsBundle",
    ":web:islands:copyIslandResources",
    ":web:webApp:copySpaBundle",
    ":web:webApp:copySpaResources",
    ":web:ssr:processResources",
).forEach { taskPath ->
    rootProject.tasks.findByPath(taskPath)?.mustRunAfter(":clean")
}