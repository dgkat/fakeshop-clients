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
        commonMain {
            kotlin.srcDir("build/generated/strings/kotlin")
        }

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
            implementation(libs.kotlinx.coroutines.test)
        }

        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // Ktor dependencies shared between Android and iOS
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.logging)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.datastore.preferences)
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

            // Ktor client for SSR
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content)
            implementation(libs.ktor.serialization.kotlinx.json)
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
    buildFeatures { buildConfig = true }
    buildTypes {
        debug   {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080\"")
            buildConfigField("Boolean", "IS_DEBUG", "true")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.dgkat.com\"")
            buildConfigField("Boolean", "IS_DEBUG", "false")
        }
    }
}

skie {
    isEnabled = true
    features {
        group {
            // Enable Flow interop - makes Flow → AsyncSequence
            FlowInterop.Enabled(true)
            // Enable suspend interop - makes suspend → async
            SuspendInterop.Enabled(true)
        }
    }
}

// ============================================
// String Generation from shared JSON sources
// ============================================

val generateComposeStrings by tasks.registering {
    group = "strings"
    description = "Generates Compose Multiplatform strings.xml files from shared JSON strings"

    val stringsDir = layout.projectDirectory.dir("src/commonMain/resources/strings").asFile
    val composeResDir = layout.projectDirectory.dir("../composeApp/src/commonMain/composeResources").asFile

    inputs.dir(stringsDir)
    outputs.dir(composeResDir)

    doLast {
        val placeholderRegex = Regex("""\{\{(\w+)\}\}""")
        fun toAndroid(input: String): String {
            var idx = 1
            return placeholderRegex.replace(input) {
                if (it.groupValues[1] == "count") "%d" else "%${idx++}\$s"
            }
        }

        val jsonParser = groovy.json.JsonSlurper()

        stringsDir.listFiles { f -> f.extension == "json" }?.sorted()?.forEach { jsonFile ->
            val locale = jsonFile.nameWithoutExtension
            @Suppress("UNCHECKED_CAST")
            val strings = jsonParser.parseText(jsonFile.readText()) as Map<String, Any>

            val valuesDirName = if (locale == "en") "values" else "values-$locale"
            val valuesDir = File(composeResDir, valuesDirName)
            valuesDir.mkdirs()

            val xml = buildString {
                appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
                appendLine("<!-- AUTO-GENERATED from strings/$locale.json - do not edit manually -->")
                appendLine("<resources>")

                strings.forEach { (key, value) ->
                    when (value) {
                        is String -> {
                            val androidValue = value
                                .replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("'", "\\'")
                                .let { toAndroid(it) }
                            appendLine("""    <string name="$key">$androidValue</string>""")
                        }
                        is Map<*, *> -> {
                            appendLine("""    <plurals name="$key">""")
                            @Suppress("UNCHECKED_CAST")
                            (value as Map<String, String>).forEach { (quantity, text) ->
                                val androidText = text
                                    .replace("&", "&amp;")
                                    .replace("'", "\\'")
                                    .let { toAndroid(it) }
                                appendLine("""        <item quantity="$quantity">$androidText</item>""")
                            }
                            appendLine("    </plurals>")
                        }
                    }
                }

                appendLine("</resources>")
            }

            File(valuesDir, "strings.xml").writeText(xml)
            logger.lifecycle("Generated Compose Multiplatform strings: $valuesDirName/strings.xml")
        }
    }
}

val generateIosStrings by tasks.registering {
    group = "strings"
    description = "Generates iOS Localizable.strings files from shared JSON strings"

    dependsOn(generateComposeStrings)

    val stringsDir = layout.projectDirectory.dir("src/commonMain/resources/strings").asFile
    val iosAppDir = layout.projectDirectory.dir("../iosApp/iosApp").asFile

    inputs.dir(stringsDir)
    outputs.dir(iosAppDir)

    doLast {
        val placeholderRegex = Regex("""\{\{(\w+)\}\}""")
        fun toIos(input: String): String {
            return placeholderRegex.replace(input) {
                if (it.groupValues[1] == "count") "%d" else "%@"
            }
        }

        val jsonParser = groovy.json.JsonSlurper()

        stringsDir.listFiles { f -> f.extension == "json" }?.sorted()?.forEach { jsonFile ->
            val locale = jsonFile.nameWithoutExtension
            @Suppress("UNCHECKED_CAST")
            val strings = jsonParser.parseText(jsonFile.readText()) as Map<String, Any>

            val lprojDir = File(iosAppDir, "$locale.lproj")
            lprojDir.mkdirs()

            val stringsContent = buildString {
                appendLine("/* AUTO-GENERATED from strings/$locale.json - do not edit manually */")
                appendLine()

                strings.forEach { (key, value) ->
                    when (value) {
                        is String -> {
                            val iosValue = value
                                .replace("\"", "\\\"")
                                .let { toIos(it) }
                            appendLine("\"$key\" = \"$iosValue\";")
                        }
                        is Map<*, *> -> {
                            @Suppress("UNCHECKED_CAST")
                            val plurals = value as Map<String, String>
                            val otherForm = plurals["other"] ?: plurals.values.first()
                            val iosValue = otherForm
                                .replace("\"", "\\\"")
                                .let { toIos(it) }
                            appendLine("\"$key\" = \"$iosValue\";")
                        }
                    }
                }
            }

            File(lprojDir, "Localizable.strings").writeText(stringsContent)
            logger.lifecycle("Generated iOS strings: $locale.lproj/Localizable.strings")

            // Generate .stringsdict for plurals
            val plurals = strings.filter { it.value is Map<*, *> }
            if (plurals.isNotEmpty()) {
                val plist = buildString {
                    appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
                    appendLine("""<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">""")
                    appendLine("""<!-- AUTO-GENERATED from strings/$locale.json - do not edit manually -->""")
                    appendLine("""<plist version="1.0">""")
                    appendLine("<dict>")

                    plurals.forEach { (key, value) ->
                        @Suppress("UNCHECKED_CAST")
                        val forms = value as Map<String, String>
                        appendLine("    <key>$key</key>")
                        appendLine("    <dict>")
                        appendLine("        <key>NSStringLocalizedFormatKey</key>")
                        appendLine("        <string>%#@count@</string>")
                        appendLine("        <key>count</key>")
                        appendLine("        <dict>")
                        appendLine("            <key>NSStringFormatSpecTypeKey</key>")
                        appendLine("            <string>NSStringPluralRuleType</string>")
                        appendLine("            <key>NSStringFormatValueTypeKey</key>")
                        appendLine("            <string>d</string>")
                        forms.forEach { (quantity, text) ->
                            val iosText = toIos(text)
                            appendLine("            <key>$quantity</key>")
                            appendLine("            <string>$iosText</string>")
                        }
                        appendLine("        </dict>")
                        appendLine("    </dict>")
                    }

                    appendLine("</dict>")
                    appendLine("</plist>")
                }

                File(lprojDir, "Localizable.stringsdict").writeText(plist)
                logger.lifecycle("Generated iOS stringsdict: $locale.lproj/Localizable.stringsdict")
            }
        }
    }
}

val generateWebStrings by tasks.registering {
    group = "strings"
    description = "Generates Web i18n JSON files from shared JSON strings"

    val stringsDir = layout.projectDirectory.dir("src/commonMain/resources/strings").asFile
    val webOutputDir = layout.projectDirectory.dir("../web/common/src/commonMain/resources/strings").asFile
    val ssrStaticDir = layout.projectDirectory.dir("../web/ssr/src/main/resources/static/strings").asFile

    inputs.dir(stringsDir)
    outputs.dir(webOutputDir)
    outputs.dir(ssrStaticDir)

    doLast {
        webOutputDir.mkdirs()
        ssrStaticDir.mkdirs()

        stringsDir.listFiles { f -> f.extension == "json" }?.forEach { jsonFile ->
            val outputFile = File(webOutputDir, jsonFile.name)
            jsonFile.copyTo(outputFile, overwrite = true)
            logger.lifecycle("Copied web strings: ${jsonFile.name}")

            val ssrOutputFile = File(ssrStaticDir, jsonFile.name)
            jsonFile.copyTo(ssrOutputFile, overwrite = true)
            logger.lifecycle("Copied SSR static strings: ${jsonFile.name}")
        }
    }
}

val generateStringKeys by tasks.registering {
    group = "strings"
    description = "Generates Kotlin Strings object with string key constants"

    val baseFile = layout.projectDirectory.file("src/commonMain/resources/strings/en.json").asFile
    val outputFile = layout.buildDirectory.file(
        "generated/strings/kotlin/org/example/fakeshop_clients/core/strings/Strings.kt"
    ).get().asFile

    inputs.file(baseFile)
    outputs.file(outputFile)

    doLast {
        val jsonParser = groovy.json.JsonSlurper()
        @Suppress("UNCHECKED_CAST")
        val strings = jsonParser.parseText(baseFile.readText()) as Map<String, Any>

        outputFile.parentFile.mkdirs()

        val kotlin = buildString {
            appendLine("package org.example.fakeshop_clients.core.strings")
            appendLine()
            appendLine("/**")
            appendLine(" * AUTO-GENERATED from strings/en.json - do not edit manually.")
            appendLine(" * Contains string keys and default (English) values.")
            appendLine(" */")
            appendLine("object Strings {")

            strings.forEach { (key, value) ->
                val constName = key.uppercase()
                when (value) {
                    is String -> {
                        val escaped = value.replace("\"", "\\\"")
                        appendLine("    const val $constName = \"$escaped\"")
                    }
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        val forms = value as Map<String, String>
                        val defaultForm = forms["other"] ?: forms.values.first()
                        val escaped = defaultForm.replace("\"", "\\\"")
                        appendLine("    const val $constName = \"$escaped\"")
                    }
                }
            }

            appendLine("}")
        }

        outputFile.writeText(kotlin)
        logger.lifecycle("Generated Kotlin string keys: ${outputFile.path}")
    }
}

// ============================================
// iOS Color Generation from DesignTokens.kt
// ============================================

val generateIosColors by tasks.registering {
    group = "design"
    description = "Generates FakeShopColors.swift from shared DesignTokens.kt"

    val tokensFile = layout.projectDirectory.file(
        "src/commonMain/kotlin/org/example/fakeshop_clients/core/design/DesignTokens.kt"
    ).asFile
    val outputFile = layout.projectDirectory.dir("../iosApp/iosApp/Theme").asFile
        .resolve("FakeShopColors.swift")

    inputs.file(tokensFile)
    outputs.file(outputFile)

    doLast {
        val source = tokensFile.readText()

        // Parse color constants from Light and Dark objects
        val colorRegex = Regex("""const val (\w+)\s*=\s*0x([0-9A-Fa-f]+)L""")

        fun parseColorBlock(blockName: String): Map<String, String> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n        \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()

            return colorRegex.findAll(block).associate { match ->
                val name = match.groupValues[1]
                val hex = match.groupValues[2]
                // Drop the FF alpha prefix to get RGB hex
                val rgbHex = if (hex.length == 8) hex.substring(2) else hex
                name to rgbHex
            }
        }

        val lightColors = parseColorBlock("Light")
        val darkColors = parseColorBlock("Dark")

        // Get all color names (union of light and dark) preserving order from light
        val allNames = linkedSetOf<String>().apply {
            addAll(lightColors.keys)
            addAll(darkColors.keys)
        }

        // Convert SCREAMING_SNAKE_CASE to camelCase
        fun toCamelCase(name: String): String {
            return name.split("_").mapIndexed { index, part ->
                if (index == 0) part.lowercase()
                else part.lowercase().replaceFirstChar { it.uppercase() }
            }.joinToString("")
        }

        // Detect comment group headers from the source
        fun detectGroup(name: String): String? {
            // Map color names to their group based on prefix patterns
            return when {
                name.startsWith("PRIMARY") || name.startsWith("ON_PRIMARY") -> "Primary"
                name.startsWith("SECONDARY") || name.startsWith("ON_SECONDARY") -> "Secondary"
                name.startsWith("TERTIARY") || name.startsWith("ON_TERTIARY") -> "Tertiary"
                name.startsWith("SURFACE") || name.startsWith("ON_SURFACE") -> "Surface"
                name.startsWith("BACKGROUND") || name.startsWith("ON_BACKGROUND") -> "Background"
                name.startsWith("ERROR") || name.startsWith("ON_ERROR") -> "Error"
                name.startsWith("SUCCESS") -> "Success"
                name.startsWith("WARNING") -> "Warning / Rating"
                name.startsWith("FAVORITE") -> "Favorite / Like"
                name.startsWith("OUTLINE") -> "Outline"
                name.startsWith("INVERSE") -> "Inverse"
                name.startsWith("SCRIM") || name.startsWith("SHADOW") -> "Scrim & Shadow"
                name.startsWith("DISABLED") -> "Disabled"
                else -> null
            }
        }

        val swift = buildString {
            appendLine("// AUTO-GENERATED from DesignTokens.kt — do not edit manually.")
            appendLine("// Run: ./gradlew generateIosColors")
            appendLine()
            appendLine("import SwiftUI")
            appendLine()
            appendLine("struct FakeShopColors {")

            var lastGroup: String? = null

            allNames.forEach { name ->
                val lightHex = lightColors[name]
                val darkHex = darkColors[name]

                // Add MARK comment for group changes
                val group = detectGroup(name)
                if (group != null && group != lastGroup) {
                    if (lastGroup != null) appendLine()
                    appendLine("    // MARK: - $group")
                    lastGroup = group
                }

                val swiftName = toCamelCase(name)

                if (lightHex != null && darkHex != null) {
                    appendLine("    static let $swiftName = adaptive(light: 0x$lightHex, dark: 0x$darkHex)")
                } else if (lightHex != null) {
                    appendLine("    static let $swiftName = adaptive(light: 0x$lightHex, dark: 0x$lightHex)")
                } else if (darkHex != null) {
                    appendLine("    static let $swiftName = adaptive(light: 0x$darkHex, dark: 0x$darkHex)")
                }
            }

            appendLine()
            appendLine("    // MARK: - Helpers")
            appendLine()
            appendLine("    private static func adaptive(light: UInt32, dark: UInt32) -> Color {")
            appendLine("        Color(uiColor: UIColor { traits in")
            appendLine("            traits.userInterfaceStyle == .dark ? UIColor(hex: dark) : UIColor(hex: light)")
            appendLine("        })")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("// MARK: - Hex color extensions")
            appendLine()
            appendLine("extension Color {")
            appendLine("    init(hex: UInt32) {")
            appendLine("        self.init(")
            appendLine("            red: Double((hex >> 16) & 0xFF) / 255.0,")
            appendLine("            green: Double((hex >> 8) & 0xFF) / 255.0,")
            appendLine("            blue: Double(hex & 0xFF) / 255.0")
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("extension UIColor {")
            appendLine("    convenience init(hex: UInt32) {")
            appendLine("        self.init(")
            appendLine("            red: CGFloat((hex >> 16) & 0xFF) / 255.0,")
            appendLine("            green: CGFloat((hex >> 8) & 0xFF) / 255.0,")
            appendLine("            blue: CGFloat(hex & 0xFF) / 255.0,")
            appendLine("            alpha: 1.0")
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
        }

        outputFile.parentFile.mkdirs()
        outputFile.writeText(swift)
        logger.lifecycle("Generated iOS colors: ${outputFile.path}")
    }
}