# Shared Resources Guide

This guide documents the steps to centralize design resources (colors, theme, typography, spacing) and strings across all platforms (Android, iOS, Web) in this Kotlin Multiplatform project.

**Approach**:
- **Colors & Theme**: Define once in Kotlin (`shared/commonMain`), consume natively per platform. Web CSS is generated via a Gradle task.
- **Strings**: Define in JSON in `shared/commonMain`, generate platform-native files via Gradle tasks (Android `strings.xml`, iOS `Localizable.strings`, Web JSON).

---

## Table of Contents

1. [Part 1: Colors, Theme & Typography](#part-1-colors-theme--typography)
   - [Step 1: Create DesignTokens in shared/commonMain](#step-1-create-designtokens-in-sharedcommonmain)
   - [Step 2: Migrate Android to use DesignTokens](#step-2-migrate-android-to-use-designtokens)
   - [Step 3: Create iOS SwiftUI theme bridge](#step-3-create-ios-swiftui-theme-bridge)
   - [Step 4: Gradle task to generate theme.css for Web](#step-4-gradle-task-to-generate-themecss-for-web)
   - [Step 5: Wire CSS generation into the build](#step-5-wire-css-generation-into-the-build)
2. [Part 2: Strings & Localization](#part-2-strings--localization)
   - [Step 6: Define strings source format](#step-6-define-strings-source-format)
   - [Step 7: Gradle task to generate Android strings.xml](#step-7-gradle-task-to-generate-android-stringsxml)
   - [Step 8: Gradle task to generate iOS Localizable.strings](#step-8-gradle-task-to-generate-ios-localizablestrings)
   - [Step 9: Gradle task to generate Web JSON](#step-9-gradle-task-to-generate-web-json)
   - [Step 10: Generate Kotlin accessor object](#step-10-generate-kotlin-accessor-object)
   - [Step 11: Wire string generation into the build](#step-11-wire-string-generation-into-the-build)
3. [Web Resource Distribution Pipeline](#web-resource-distribution-pipeline)
4. [File Structure Overview](#file-structure-overview)
5. [Adding a New Color or String](#adding-a-new-color-or-string)

---

## Part 1: Colors, Theme & Typography

### Step 1: Create DesignTokens in shared/commonMain

Create the single source of truth for all design values.

**File**: `shared/src/commonMain/kotlin/org/example/fakeshop_clients/core/design/DesignTokens.kt`

```kotlin
package org.example.fakeshop_clients.core.design

/**
 * Single source of truth for all design tokens.
 * All color values are ARGB hex as Long constants.
 *
 * Consumed by:
 * - Android Compose: Color(DesignTokens.Colors.Light.Primary)
 * - iOS SwiftUI: via FakeShopColors Swift extension (reads from Kotlin)
 * - Web: generated into theme.css via Gradle task (generateThemeCss)
 */
object DesignTokens {

    object Colors {
        object Light {
            // Primary
            const val Primary = 0xFF6750A4L
            const val OnPrimary = 0xFFFFFFFFL
            const val PrimaryContainer = 0xFFEADDFFL
            const val OnPrimaryContainer = 0xFF21005DL

            // Secondary
            const val Secondary = 0xFF625B71L
            const val OnSecondary = 0xFFFFFFFFL
            const val SecondaryContainer = 0xFFE8DEF8L
            const val OnSecondaryContainer = 0xFF1D192BL

            // Surface
            const val Surface = 0xFFFFFFFFL
            const val SurfaceVariant = 0xFFE7E0ECL
            const val OnSurface = 0xFF1C1B1FL
            const val OnSurfaceVariant = 0xFF49454FL

            // Background
            const val Background = 0xFFFFFBFEL
            const val OnBackground = 0xFF1C1B1FL

            // Error
            const val Error = 0xFFB3261EL
            const val OnError = 0xFFFFFFFFL
            const val ErrorContainer = 0xFFF9DEDCL
            const val OnErrorContainer = 0xFF410E0BL

            // Success
            const val Success = 0xFF2E7D32L
            const val SuccessContainer = 0xFFE8F5E9L

            // Outline
            const val Outline = 0xFF79747EL
            const val OutlineVariant = 0xFFCAC4D0L
        }

        object Dark {
            // Primary
            const val Primary = 0xFFD0BCFFL
            const val OnPrimary = 0xFF381E72L
            const val PrimaryContainer = 0xFF4F378BL
            const val OnPrimaryContainer = 0xFFEADDFFL

            // Secondary
            const val Secondary = 0xFFCCC2DCL
            const val OnSecondary = 0xFF332D41L
            const val SecondaryContainer = 0xFF4A4458L
            const val OnSecondaryContainer = 0xFFE8DEF8L

            // Surface
            const val Surface = 0xFF1C1B1FL
            const val SurfaceVariant = 0xFF49454FL
            const val OnSurface = 0xFFE6E1E5L
            const val OnSurfaceVariant = 0xFFCAC4D0L

            // Background
            const val Background = 0xFF1C1B1FL
            const val OnBackground = 0xFFE6E1E5L

            // Outline
            const val Outline = 0xFF938F99L
            const val OutlineVariant = 0xFF49454FL
        }
    }

    object Typography {
        // Font sizes in SP/px (same numeric value, unit depends on platform)
        const val DisplayLarge = 57
        const val DisplayMedium = 45
        const val DisplaySmall = 36

        const val HeadlineLarge = 32
        const val HeadlineMedium = 28
        const val HeadlineSmall = 24

        const val TitleLarge = 22
        const val TitleMedium = 16
        const val TitleSmall = 14

        const val BodyLarge = 16
        const val BodyMedium = 14
        const val BodySmall = 12

        const val LabelLarge = 14
        const val LabelMedium = 12
        const val LabelSmall = 11

        // Line heights in SP/px
        const val DisplayLargeLineHeight = 64
        const val HeadlineMediumLineHeight = 36
        const val TitleLargeLineHeight = 28
        const val TitleMediumLineHeight = 24
        const val BodyLargeLineHeight = 24
        const val BodyMediumLineHeight = 20
        const val LabelSmallLineHeight = 16

        // Letter spacing in SP/px
        const val TitleMediumLetterSpacing = 0.15f
        const val BodyLargeLetterSpacing = 0.5f
        const val BodyMediumLetterSpacing = 0.25f
        const val LabelSmallLetterSpacing = 0.5f

        // Font weights (numeric CSS/Compose values)
        const val WeightRegular = 400
        const val WeightMedium = 500
        const val WeightSemiBold = 600
        const val WeightBold = 700

        // Web font family (Android/iOS use platform default)
        const val WebFontFamily =
            "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', sans-serif"
    }

    object Spacing {
        const val Xs = 4
        const val Sm = 8
        const val Md = 12
        const val Lg = 16
        const val Xl = 24
        const val Xxl = 32
        const val Xxxl = 48
    }

    object BorderRadius {
        const val Xs = 4
        const val Sm = 8
        const val Md = 12
        const val Lg = 16
        const val Xl = 24
        const val Full = 9999
    }
}
```

**Notes**:
- Colors use `Long` (not `Int`) so the `0xFF` alpha prefix doesn't overflow.
- Only values that make sense cross-platform are included. Web-specific values (shadows, transitions, z-index, breakpoints) stay in the CSS — they have no mobile equivalent.
- `WebFontFamily` is stored here for the CSS generator but not used by mobile (which uses system default fonts).

---

### Step 2: Migrate Android to use DesignTokens

Update existing Android theme files to read from `DesignTokens` instead of hardcoding hex values.

**File**: `composeApp/src/androidMain/kotlin/org/example/fakeshop_clients/ui/theme/Color.kt`

```kotlin
package org.example.fakeshop_clients.ui.theme

import androidx.compose.ui.graphics.Color
import org.example.fakeshop_clients.core.design.DesignTokens.Colors

// Light Theme Colors
val md_theme_light_primary = Color(Colors.Light.Primary)
val md_theme_light_onPrimary = Color(Colors.Light.OnPrimary)
val md_theme_light_primaryContainer = Color(Colors.Light.PrimaryContainer)
val md_theme_light_onPrimaryContainer = Color(Colors.Light.OnPrimaryContainer)
val md_theme_light_secondary = Color(Colors.Light.Secondary)
val md_theme_light_onSecondary = Color(Colors.Light.OnSecondary)
val md_theme_light_secondaryContainer = Color(Colors.Light.SecondaryContainer)
val md_theme_light_onSecondaryContainer = Color(Colors.Light.OnSecondaryContainer)

// Dark Theme Colors
val md_theme_dark_primary = Color(Colors.Dark.Primary)
val md_theme_dark_onPrimary = Color(Colors.Dark.OnPrimary)
val md_theme_dark_primaryContainer = Color(Colors.Dark.PrimaryContainer)
val md_theme_dark_onPrimaryContainer = Color(Colors.Dark.OnPrimaryContainer)
val md_theme_dark_secondary = Color(Colors.Dark.Secondary)
val md_theme_dark_onSecondary = Color(Colors.Dark.OnSecondary)
val md_theme_dark_secondaryContainer = Color(Colors.Dark.SecondaryContainer)
val md_theme_dark_onSecondaryContainer = Color(Colors.Dark.OnSecondaryContainer)
```

**File**: `composeApp/src/androidMain/kotlin/org/example/fakeshop_clients/ui/theme/Typography.kt`

```kotlin
package org.example.fakeshop_clients.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.example.fakeshop_clients.core.design.DesignTokens.Typography as Typo

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.DisplayLarge.sp,
        lineHeight = Typo.DisplayLargeLineHeight.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.HeadlineMedium.sp,
        lineHeight = Typo.HeadlineMediumLineHeight.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.TitleLarge.sp,
        lineHeight = Typo.TitleLargeLineHeight.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(Typo.WeightMedium),
        fontSize = Typo.TitleMedium.sp,
        lineHeight = Typo.TitleMediumLineHeight.sp,
        letterSpacing = Typo.TitleMediumLetterSpacing.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.BodyLarge.sp,
        lineHeight = Typo.BodyLargeLineHeight.sp,
        letterSpacing = Typo.BodyLargeLetterSpacing.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.BodyMedium.sp,
        lineHeight = Typo.BodyMediumLineHeight.sp,
        letterSpacing = Typo.BodyMediumLetterSpacing.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(Typo.WeightMedium),
        fontSize = Typo.LabelSmall.sp,
        lineHeight = Typo.LabelSmallLineHeight.sp,
        letterSpacing = Typo.LabelSmallLetterSpacing.sp
    )
)
```

`Theme.kt` **stays unchanged** — it already references these variables.

---

### Step 3: Generate iOS SwiftUI color bridge

`FakeShopColors.swift` is **auto-generated** from `DesignTokens.kt` via a Gradle task — do not edit it manually.

**Generated file**: `iosApp/iosApp/Theme/FakeShopColors.swift`

**Generate command**:
```bash
./gradlew generateIosColors
```

The task parses `DesignTokens.kt` source text with regex (same approach as `generateThemeCss`), extracts all color constants from the `Light` and `Dark` objects, pairs them by name, and produces a Swift file with `adaptive(light:dark:)` calls that auto-adapt to system dark mode.

**What gets generated**:
- A `FakeShopColors` struct with `static let` properties for every color in `DesignTokens.Colors`
- `MARK` comments grouping colors by category (Primary, Secondary, Surface, etc.)
- `Color(hex:)` and `UIColor(hex:)` extensions for hex-to-color conversion
- Property names use Swift `camelCase` convention (e.g., `Primary` → `primary`, `OnPrimary` → `onPrimary`)

**Usage in SwiftUI views**:

```swift
Text("Product")
    .foregroundColor(FakeShopColors.onSurface)
    .background(FakeShopColors.surface)
```

Colors automatically adapt to light/dark mode via `UIColor { traits in ... }`.

---

### Step 4: Gradle task to generate theme.css for Web

Create a Gradle task in the `web/common` module that reads `DesignTokens.kt` source and generates `theme.css`.

**Why parse the source file instead of running Kotlin?** The `web/common` module is a plain resource module — it doesn't compile the shared Kotlin code. Parsing the `.kt` file as text is simpler than setting up a classpath dependency on the compiled shared module.

**File**: `web/common/buildSrc/src/main/kotlin/GenerateThemeCssTask.kt`

If you don't already have a `buildSrc` in `web/common`, you can define the task inline in `web/common/build.gradle.kts` instead. The logic is the same either way.

**Inline approach in `web/common/build.gradle.kts`**:

```kotlin
// Add at the bottom of web/common/build.gradle.kts

val generateThemeCss by tasks.registering {
    group = "build"
    description = "Generates theme.css from shared DesignTokens.kt"

    val tokensFile = rootProject.file(
        "shared/src/commonMain/kotlin/org/example/fakeshop_clients/core/design/DesignTokens.kt"
    )
    val outputFile = file(
        "src/commonMain/resources/css/shared/theme.css"
    )

    inputs.file(tokensFile)
    outputs.file(outputFile)

    doLast {
        val source = tokensFile.readText()

        // --- Parse color constants ---
        // Matches: const val Name = 0xFFRRGGBBL
        val colorRegex = Regex("""const val (\w+)\s*=\s*0x([0-9A-Fa-f]+)L""")

        fun parseColorBlock(blockName: String): Map<String, String> {
            // Find the object block (Light or Dark)
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n        \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()

            return colorRegex.findAll(block).associate { match ->
                val name = match.groupValues[1]
                val hex = match.groupValues[2]
                // Convert ARGB hex to CSS #RRGGBB (drop alpha FF prefix)
                val cssHex = if (hex.length == 8) hex.substring(2) else hex
                val cssName = name.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
                cssName to "#$cssHex"
            }
        }

        val lightColors = parseColorBlock("Light")
        val darkColors = parseColorBlock("Dark")

        // --- Parse typography constants ---
        fun parseIntConsts(blockName: String): Map<String, Int> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n    \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()
            val constRegex = Regex("""const val (\w+)\s*=\s*(\d+)""")
            return constRegex.findAll(block).associate { it.groupValues[1] to it.groupValues[2].toInt() }
        }

        fun parseFloatConsts(blockName: String): Map<String, Float> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n    \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()
            val constRegex = Regex("""const val (\w+)\s*=\s*([0-9.]+)f""")
            return constRegex.findAll(block).associate { it.groupValues[1] to it.groupValues[2].toFloat() }
        }

        fun parseStringConsts(blockName: String): Map<String, String> {
            val blockRegex = Regex("""object $blockName\s*\{([\s\S]*?)\n    \}""")
            val block = blockRegex.find(source)?.groupValues?.get(1) ?: return emptyMap()
            val constRegex = Regex("""const val (\w+)\s*=\s*\n?\s*"([^"]+)"""")
            return constRegex.findAll(block).associate { it.groupValues[1] to it.groupValues[2] }
        }

        val typoInts = parseIntConsts("Typography")
        val typoFloats = parseFloatConsts("Typography")
        val typoStrings = parseStringConsts("Typography")
        val spacingInts = parseIntConsts("Spacing")
        val radiusInts = parseIntConsts("BorderRadius")

        fun camelToKebab(s: String) = s.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()

        // --- Build CSS ---
        val css = buildString {
            appendLine("/* ===========================================")
            appendLine("   THEME.CSS - Design Tokens & CSS Variables")
            appendLine("   AUTO-GENERATED from DesignTokens.kt")
            appendLine("   Do not edit manually — run: ./gradlew :web:common:generateThemeCss")
            appendLine("   =========================================== */")
            appendLine()
            appendLine(":root {")

            // Light colors
            appendLine("    /* ===== Color Palette (Light) ===== */")
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
            listOf("WeightRegular", "WeightMedium", "WeightSemiBold", "WeightBold").forEach { key ->
                typoInts[key]?.let { weight ->
                    val cssName = camelToKebab(key)
                    appendLine("    --font-$cssName: $weight;")
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
                val unit = if (value == 9999) "" else "px"
                appendLine("    --radius-${name.lowercase()}: ${value}${unit};")
            }
            appendLine()

            // Web-only tokens (not in DesignTokens.kt — kept here as they have no mobile equivalent)
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
            appendLine("    /* Breakpoints */")
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
            val typoClasses = listOf(
                Triple("display-large", "display-large", "tight"),
                Triple("headline-medium", "headline-medium", "normal"),
                Triple("headline-small", "headline-small", "normal"),
                Triple("title-large", "title-large", "normal"),
                Triple("title-medium", "title-medium", "normal"),
                Triple("body-large", "body-large", "normal"),
                Triple("body-medium", "body-medium", "normal"),
                Triple("label-small", "label-small", "normal"),
            )
            typoClasses.forEach { (className, sizeVar, lineHeightVar) ->
                val weight = if (className.startsWith("title") || className == "label-small") "medium" else "regular"
                appendLine(".$className {")
                appendLine("    font-size: var(--font-size-$sizeVar);")
                appendLine("    font-weight: var(--font-weight-$weight);")
                appendLine("    line-height: var(--line-height-$lineHeightVar);")
                if (className == "label-small") {
                    appendLine("    letter-spacing: 0.5px;")
                }
                appendLine("}")
                appendLine()
            }
        }

        outputFile.writeText(css)
        println("Generated ${outputFile.absolutePath}")
    }
}
```

**Important**: The regex parsing above is a starting point. The exact patterns may need tuning depending on formatting changes in `DesignTokens.kt`. An alternative approach is to write a small Kotlin script (in `buildSrc`) that compiles and reads `DesignTokens` directly via reflection, but source parsing is simpler for a project of this size.

---

### Step 5: Wire CSS generation into the build

In `web/common/build.gradle.kts`, make the existing `bundleCss` task depend on `generateThemeCss`:

```kotlin
// In web/common/build.gradle.kts
tasks.named("bundleCss") {
    dependsOn(generateThemeCss)
}
```

This ensures that whenever CSS bundles are rebuilt (which happens automatically during `jvmProcessResources`), the theme is regenerated from `DesignTokens.kt` first.

**Build flow**:
```
DesignTokens.kt (source of truth)
    ↓ generateThemeCss
theme.css (generated)
    ↓ bundleCss
common.css, home.css, etc. (bundles)
    ↓ copyBundledCss
SSR static/css/bundles/ (served to browser)
```

**Add to `.gitignore`**:
```
# theme.css is now generated — do not commit
# (It is already part of the CSS bundles which are also gitignored)
```

Since `theme.css` is an input to `bundleCss` which produces the gitignored bundles, you have two choices:
1. **Keep theme.css committed** (easier — developers can see it, and the bundleCss step just picks it up)
2. **Gitignore theme.css too** (purer — but requires running the Gradle task before CSS changes are visible)

Recommendation: **Keep it committed** for now. It's small, readable, and avoids confusion. Just ensure it's always regenerated before commits.

---

## Part 2: Strings & Localization

### Step 6: Define strings source format

Create a `strings/` directory in the shared module with one JSON file per locale.

**Directory**: `shared/src/commonMain/resources/strings/`

**File**: `shared/src/commonMain/resources/strings/en.json` (default/base locale)

```json
{
  "app_name": "Fake Shop",
  "search_placeholder": "Search products...",
  "add_to_cart": "Add to Cart",
  "remove_from_cart": "Remove from Cart",
  "price_format": "{{price}} €",
  "view_all": "View All",
  "no_results": "No results found",
  "retry": "Retry",
  "loading": "Loading...",
  "error_no_connection": "No internet connection",
  "error_timeout": "Request timed out",
  "error_generic": "Something went wrong",
  "tab_home": "Home",
  "tab_favorites": "Favorites",
  "tab_notifications": "Notifications",
  "tab_profile": "Profile",
  "product_in_stock": "In Stock",
  "product_out_of_stock": "Out of Stock",
  "login": "Log In",
  "logout": "Log Out",
  "items_count": {
    "one": "{{count}} item",
    "other": "{{count}} items"
  }
}
```

**File**: `shared/src/commonMain/resources/strings/es.json` (example: Spanish)

```json
{
  "app_name": "Tienda Falsa",
  "search_placeholder": "Buscar productos...",
  "add_to_cart": "Añadir al Carrito",
  "remove_from_cart": "Quitar del Carrito",
  "price_format": "{{price}} €",
  "view_all": "Ver Todo",
  "no_results": "No se encontraron resultados",
  "retry": "Reintentar",
  "loading": "Cargando...",
  "error_no_connection": "Sin conexión a Internet",
  "error_timeout": "Tiempo de espera agotado",
  "error_generic": "Algo salió mal",
  "tab_home": "Inicio",
  "tab_favorites": "Favoritos",
  "tab_notifications": "Notificaciones",
  "tab_profile": "Perfil",
  "product_in_stock": "En Stock",
  "product_out_of_stock": "Agotado",
  "login": "Iniciar Sesión",
  "logout": "Cerrar Sesión",
  "items_count": {
    "one": "{{count}} artículo",
    "other": "{{count}} artículos"
  }
}
```

**Conventions**:
- Keys use `snake_case` (maps naturally to Android XML resource names)
- Simple strings are `"key": "value"`
- Plurals are `"key": { "one": "...", "other": "..." }` (matching CLDR plural categories: `zero`, `one`, `two`, `few`, `many`, `other`)
- Placeholders use `{{name}}` — the Gradle task converts them to the appropriate format per platform (`%s`/`%d` for Android, `%@`/`%d` for iOS, kept as-is or converted for web i18n libraries)

---

### Step 7: Gradle task to generate Android strings.xml

Add to **root `build.gradle.kts`** or create a standalone `buildSrc` plugin. Here we show the inline approach in the **shared module's `build.gradle.kts`** (since it owns the source strings).

**Add to**: `shared/build.gradle.kts`

```kotlin
val generateAndroidStrings by tasks.registering {
    group = "strings"
    description = "Generates Android strings.xml files from shared JSON strings"

    val stringsDir = file("src/commonMain/resources/strings")
    val androidResDir = rootProject.file("composeApp/src/androidMain/res")

    inputs.dir(stringsDir)
    outputs.dir(androidResDir)

    doLast {
        val jsonParser = groovy.json.JsonSlurper()

        stringsDir.listFiles { f -> f.extension == "json" }?.forEach { jsonFile ->
            val locale = jsonFile.nameWithoutExtension  // "en", "es", etc.
            @Suppress("UNCHECKED_CAST")
            val strings = jsonParser.parseText(jsonFile.readText()) as Map<String, Any>

            // Android resource folder: values (default), values-es, values-fr, etc.
            val valuesDirName = if (locale == "en") "values" else "values-$locale"
            val valuesDir = File(androidResDir, valuesDirName)
            valuesDir.mkdirs()

            val xml = buildString {
                appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
                appendLine("<!-- AUTO-GENERATED from strings/$locale.json — do not edit manually -->")
                appendLine("<resources>")

                strings.forEach { (key, value) ->
                    when (value) {
                        is String -> {
                            val androidValue = value
                                .replace("'", "\\'")
                                .replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                // Convert {{name}} placeholders to Android %s
                                // For numbered: {{price}} -> %1$s, etc.
                                .let { convertPlaceholders(it) }
                            appendLine("""    <string name="$key">$androidValue</string>""")
                        }
                        is Map<*, *> -> {
                            // Plurals
                            appendLine("""    <plurals name="$key">""")
                            @Suppress("UNCHECKED_CAST")
                            (value as Map<String, String>).forEach { (quantity, text) ->
                                val androidText = text
                                    .replace("'", "\\'")
                                    .let { convertPlaceholders(it) }
                                appendLine("""        <item quantity="$quantity">$androidText</item>""")
                            }
                            appendLine("    </plurals>")
                        }
                    }
                }

                appendLine("</resources>")
            }

            File(valuesDir, "strings.xml").writeText(xml)
            println("Generated Android strings: $valuesDirName/strings.xml")
        }
    }
}

/**
 * Converts {{name}} placeholders to Android format.
 * - Single placeholder: {{price}} -> %s
 * - If you need numbered placeholders: {{1:price}} -> %1$s
 * - Integer hint: {{count:d}} -> %d
 */
fun convertPlaceholders(input: String): String {
    var index = 1
    return Regex("""\{\{(\w+)\}\}""").replace(input) {
        val name = it.groupValues[1]
        if (name == "count") "%d" else "%${index++}\$s"
    }
}
```

**Output example** (`composeApp/src/androidMain/res/values/strings.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- AUTO-GENERATED from strings/en.json — do not edit manually -->
<resources>
    <string name="app_name">Fake Shop</string>
    <string name="search_placeholder">Search products...</string>
    <string name="add_to_cart">Add to Cart</string>
    <string name="price_format">%1$s €</string>
    <plurals name="items_count">
        <item quantity="one">%d item</item>
        <item quantity="other">%d items</item>
    </plurals>
</resources>
```

---

### Step 8: Gradle task to generate iOS Localizable.strings

**Add to**: `shared/build.gradle.kts`

```kotlin
val generateIosStrings by tasks.registering {
    group = "strings"
    description = "Generates iOS Localizable.strings files from shared JSON strings"

    val stringsDir = file("src/commonMain/resources/strings")
    val iosAppDir = rootProject.file("iosApp/iosApp")

    inputs.dir(stringsDir)
    outputs.dir(iosAppDir)

    doLast {
        val jsonParser = groovy.json.JsonSlurper()

        stringsDir.listFiles { f -> f.extension == "json" }?.forEach { jsonFile ->
            val locale = jsonFile.nameWithoutExtension
            @Suppress("UNCHECKED_CAST")
            val strings = jsonParser.parseText(jsonFile.readText()) as Map<String, Any>

            // iOS locale folder: en.lproj, es.lproj, etc.
            val lprojDir = File(iosAppDir, "$locale.lproj")
            lprojDir.mkdirs()

            val stringsContent = buildString {
                appendLine("/* AUTO-GENERATED from strings/$locale.json — do not edit manually */")
                appendLine()

                strings.forEach { (key, value) ->
                    when (value) {
                        is String -> {
                            val iosValue = value
                                .replace("\"", "\\\"")
                                .let { convertPlaceholdersIos(it) }
                            appendLine("\"$key\" = \"$iosValue\";")
                        }
                        is Map<*, *> -> {
                            // For plurals, iOS uses .stringsdict (see below)
                            // Write the "other" form as the base .strings entry
                            @Suppress("UNCHECKED_CAST")
                            val plurals = value as Map<String, String>
                            val otherForm = plurals["other"] ?: plurals.values.first()
                            val iosValue = otherForm
                                .replace("\"", "\\\"")
                                .let { convertPlaceholdersIos(it) }
                            appendLine("\"$key\" = \"$iosValue\";")
                        }
                    }
                }
            }

            File(lprojDir, "Localizable.strings").writeText(stringsContent)
            println("Generated iOS strings: $locale.lproj/Localizable.strings")

            // Generate .stringsdict for plurals
            val plurals = strings.filter { it.value is Map<*, *> }
            if (plurals.isNotEmpty()) {
                val plist = buildString {
                    appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
                    appendLine("""<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">""")
                    appendLine("""<!-- AUTO-GENERATED from strings/$locale.json — do not edit manually -->""")
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
                            val iosText = convertPlaceholdersIos(text).replace("%d", "%d")
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
                println("Generated iOS stringsdict: $locale.lproj/Localizable.stringsdict")
            }
        }
    }
}

/**
 * Converts {{name}} placeholders to iOS format.
 * - {{count}} -> %d (integer)
 * - {{name}} -> %@ (string, generic)
 */
fun convertPlaceholdersIos(input: String): String {
    return Regex("""\{\{(\w+)\}\}""").replace(input) {
        val name = it.groupValues[1]
        if (name == "count") "%d" else "%@"
    }
}
```

**Output example** (`iosApp/iosApp/en.lproj/Localizable.strings`):

```
/* AUTO-GENERATED from strings/en.json — do not edit manually */

"app_name" = "Fake Shop";
"search_placeholder" = "Search products...";
"add_to_cart" = "Add to Cart";
"price_format" = "%@ €";
"items_count" = "%d items";
```

**iOS usage in SwiftUI**:

```swift
Text(NSLocalizedString("add_to_cart", comment: ""))
// or with String(localized:) on iOS 15+:
Text(String(localized: "add_to_cart"))
```

**Xcode setup**: After the first generation, add the `.lproj` folders to the Xcode project. Xcode will automatically recognize the localization structure.

---

### Step 9: Gradle task to generate Web JSON

**Add to**: `shared/build.gradle.kts`

```kotlin
val generateWebStrings by tasks.registering {
    group = "strings"
    description = "Generates Web i18n JSON files from shared JSON strings"

    val stringsDir = file("src/commonMain/resources/strings")
    val webOutputDir = rootProject.file("web/common/src/commonMain/resources/strings")

    inputs.dir(stringsDir)
    outputs.dir(webOutputDir)

    doLast {
        webOutputDir.mkdirs()

        // For web, the JSON format is already native.
        // Just copy and optionally flatten plurals for your i18n library.
        stringsDir.listFiles { f -> f.extension == "json" }?.forEach { jsonFile ->
            val outputFile = File(webOutputDir, jsonFile.name)
            jsonFile.copyTo(outputFile, overwrite = true)
            println("Copied web strings: ${jsonFile.name}")
        }
    }
}
```

If you later adopt an i18n library with a different format (e.g., i18next uses `"key_one"` / `"key_other"` for plurals), modify this task to transform the JSON accordingly.

For now, the source JSON is already web-compatible. The placeholders (`{{price}}`, `{{count}}`) match the conventions of most JS i18n libraries.

---

### Step 10: Generate Kotlin accessor object

Generate a type-safe `Strings` object so shared Kotlin code can reference string keys without raw strings.

**Add to**: `shared/build.gradle.kts`

```kotlin
val generateStringKeys by tasks.registering {
    group = "strings"
    description = "Generates Kotlin Strings object with string key constants"

    val stringsDir = file("src/commonMain/resources/strings")
    val baseFile = File(stringsDir, "en.json")
    val outputDir = file("build/generated/strings/kotlin")
    val outputFile = File(outputDir,
        "org/example/fakeshop_clients/core/strings/Strings.kt"
    )

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
            appendLine(" * AUTO-GENERATED from strings/en.json — do not edit manually.")
            appendLine(" * Contains string keys and default (English) values.")
            appendLine(" * Use these constants in shared code for consistency.")
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
                        // For plurals, store the "other" form as default
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
        println("Generated Kotlin string keys: ${outputFile.path}")
    }
}
```

**Register the generated source directory** so it's compiled as part of `commonMain`:

```kotlin
// In shared/build.gradle.kts, inside kotlin { sourceSets { } }
kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/strings/kotlin")
        }
    }
}
```

**Output** (`build/generated/strings/kotlin/.../Strings.kt`):

```kotlin
package org.example.fakeshop_clients.core.strings

object Strings {
    const val APP_NAME = "Fake Shop"
    const val SEARCH_PLACEHOLDER = "Search products..."
    const val ADD_TO_CART = "Add to Cart"
    const val PRICE_FORMAT = "{{price}} €"
    const val ITEMS_COUNT = "{{count}} items"
    // ...
}
```

**Usage in shared ViewStores**: Use `Strings.ADD_TO_CART` as default values or for non-localized contexts. Platform UI layers should use their native localization APIs (`stringResource()` on Android, `NSLocalizedString` on iOS) for user-facing text.

---

### Step 11: Wire string generation into the build

**Option A: Run manually** (recommended to start with):

```bash
# Generate all platform string files
./gradlew generateAndroidStrings generateIosStrings generateWebStrings generateStringKeys
```

**Option B: Wire into build tasks** (for CI/automation):

```kotlin
// In shared/build.gradle.kts

// Make Android compilation depend on string generation
tasks.matching { it.name == "compileDebugKotlinAndroid" || it.name == "compileReleaseKotlinAndroid" }
    .configureEach { dependsOn(generateAndroidStrings) }

// Make commonMain compilation depend on Kotlin string keys
tasks.matching { it.name.startsWith("compileKotlin") }
    .configureEach { dependsOn(generateStringKeys) }
```

For iOS, run `generateIosStrings` as a pre-build step in Xcode:
1. In Xcode, select the iosApp target → Build Phases
2. Add a "Run Script" phase before "Compile Sources"
3. Script: `cd "$SRCROOT/.." && ./gradlew generateIosStrings`

---

## Web Resource Distribution Pipeline

Understanding how resources flow through the web modules is important for knowing where generated files end up and how they reach the browser.

### Current Architecture

The web application has four modules, and resources flow from source modules into the SSR server which serves everything:

```
web/common/                          (Source: CSS, design tokens)
web/islands/                         (Source: interactive island components)
web/webApp/                          (Source: SPA React application)
web/ssr/                             (Destination: serves everything to the browser)
```

### Resource Flow Diagram

```
                    DesignTokens.kt (shared/commonMain)
                           │
                           │ generateThemeCss
                           ▼
┌─────────────────────────────────────────────────────────┐
│  web/common/src/commonMain/resources/                   │
│  ├── css/shared/theme.css  ◄── GENERATED                │
│  ├── css/shared/base.css                                │
│  ├── css/shared/components.css                          │
│  ├── css/shared/navigation.css                          │
│  ├── css/shared/search-bar.css                          │
│  ├── css/shared/view-transitions.css                    │
│  ├── css/pages/product-list.css                         │
│  ├── css/pages/product-details.css                      │
│  ├── css/pages/spa.css                                  │
│  ├── css/pages/profile-page.css                         │
│  └── strings/en.json, es.json  ◄── GENERATED (copy)    │
└──────────────┬──────────────────────────────────────────┘
               │
               │ bundleCss (concatenates CSS files into bundles)
               ▼
┌──────────────────────────────────────┐
│  web/common/build/bundled-css/       │
│  ├── common.css   (theme + base +   │
│  │                  components +     │
│  │                  navigation)      │
│  ├── home.css                        │
│  ├── product-detail.css              │
│  └── spa.css                         │
└──────────────┬───────────────────────┘
               │
               │ copyBundledCss
               ▼
┌──────────────────────────────────────────────────────────┐
│  web/ssr/src/main/resources/                             │
│  ├── static/css/bundles/           ◄── CSS BUNDLES       │
│  │   ├── common.css                                      │
│  │   ├── home.css                                        │
│  │   ├── product-detail.css                              │
│  │   └── spa.css                                         │
│  ├── static/js/                    ◄── JS BUNDLES        │
│  │   ├── islands-bundle.js         (from web/islands)    │
│  │   ├── spa-bundle.js             (from web/webApp)     │
│  │   ├── universal-hydrator.js     (SSR-native)          │
│  │   └── header-scroll.js          (SSR-native)          │
│  └── common/                       ◄── LEGACY COPIES     │
│      ├── css/ (individual CSS)     (from islands/webApp) │
│      ├── static/                                         │
│      └── index.html                                      │
└──────────────────────────────────────────────────────────┘
```

### Gradle Task Chain

The tasks are wired together so everything builds in the correct order:

**CSS flow** (triggered automatically by `jvmProcessResources`):
```
generateThemeCss  →  bundleCss  →  copyBundledCss  →  jvmProcessResources
```

**Islands JS flow** (triggered manually via `copyIslandsBundle`):
```
jsBrowserProductionWebpack  →  copyIslandResources  →  copyIslandsBundle
       (webpack build)         (resources → SSR        (JS → SSR
                                 common/)                static/js/)
```

**SPA JS flow** (triggered manually via `copySpaBundle`):
```
jsBrowserProductionWebpack  →  copySpaResources  →  copySpaBundle
       (webpack build)        (resources → SSR       (JS → SSR
                                common/)              static/js/)
```

### Task Details

| Task | Module | What it does | Triggered by |
|------|--------|-------------|-------------|
| `generateThemeCss` | `web:common` | Generates `theme.css` from `DesignTokens.kt` | `bundleCss` (dependency) |
| `bundleCss` | `web:common` | Concatenates CSS files into 4 bundles (common, home, product-detail, spa) | `copyBundledCss` (dependency) |
| `copyBundledCss` | `web:common` | Copies bundles to `web/ssr/.../static/css/bundles/` | `jvmProcessResources` (auto) |
| `copyIslandResources` | `web:islands` | Copies `web/islands/src/jsMain/resources/` → `web/ssr/.../common/` | `copyIslandsBundle` (dependency) |
| `copyIslandsBundle` | `web:islands` | Copies webpack output (`islands-bundle.js`) → `web/ssr/.../static/js/` | Manual |
| `copySpaResources` | `web:webApp` | Copies `web/webApp/src/jsMain/resources/` → `web/ssr/.../common/` | `copySpaBundle` (dependency) |
| `copySpaBundle` | `web:webApp` | Copies webpack output (`spa-bundle.js`) → `web/ssr/.../static/js/` | Manual |
| `generateWebStrings` | `shared` | Copies string JSONs → `web/common/.../resources/strings/` | Manual |

### How Generated Strings Reach the Web

The web string JSON files follow this path:

```
shared/src/commonMain/resources/strings/en.json  (source of truth)
    │
    │  generateWebStrings (copies to web/common)
    ▼
web/common/src/commonMain/resources/strings/en.json
    │
    │  Available via web:common dependency
    ▼
web/islands, web/webApp, web/ssr  (all depend on web:common)
```

Since `web/islands`, `web/webApp`, and `web/ssr` all declare `implementation(project(":web:common"))` in their dependencies, the string JSON files from `web/common/src/commonMain/resources/` are available on the classpath at runtime.

- **SSR (Ktor server)**: Load strings via `ClassLoader.getResource("strings/en.json")` for server-side rendering
- **Islands/SPA (Kotlin/JS)**: Access via `js("require('strings/en.json')")` or fetch from an API endpoint served by SSR
- **Alternative for SPA/Islands**: Have the SSR server expose a `/api/strings/{locale}` endpoint that reads the JSON from classpath and serves it — this way the SPA can fetch strings dynamically based on the user's locale

### Gitignored Files

All generated/copied files are already in `.gitignore`:

```gitignore
# JS bundles (copied by copyIslandsBundle / copySpaBundle)
web/ssr/src/main/resources/static/js/islands-bundle.js
web/ssr/src/main/resources/static/js/islands-bundle.js.map
web/ssr/src/main/resources/static/js/spa-bundle.js
web/ssr/src/main/resources/static/js/spa-bundle.js.map

# CSS bundles (created by bundleCss / copyBundledCss)
web/common/build/bundled-css/
web/ssr/src/main/resources/static/css/bundles/

# Legacy resource copies (from islands/webApp resource copy tasks)
web/ssr/src/main/resources/common/
web/webApp/src/jsMain/resources/common/
```

When adding shared resources, also add these to `.gitignore`:

```gitignore
# Generated web strings (copied by generateWebStrings)
web/common/src/commonMain/resources/strings/

# Generated Kotlin string accessor
shared/build/generated/strings/
```

### Build Commands Summary

```bash
# Full web rebuild (CSS + Islands + SPA)
./gradlew :web:islands:copyIslandsBundle
./gradlew :web:webApp:copySpaBundle
./gradlew :web:ssr:run

# Just regenerate theme.css from DesignTokens.kt
./gradlew :web:common:generateThemeCss

# Regenerate and bundle CSS (theme.css → bundles → SSR)
./gradlew :web:common:copyBundledCss

# Generate all string files for all platforms
./gradlew generateAndroidStrings generateIosStrings generateWebStrings generateStringKeys

# Full rebuild with everything regenerated
./gradlew generateAndroidStrings generateIosStrings generateWebStrings generateStringKeys :web:common:copyBundledCss :web:islands:copyIslandsBundle :web:webApp:copySpaBundle :web:ssr:run
```

---

## File Structure Overview

After implementing all steps, the resource-related files will look like this:

```
fakeshop-clients/
├── shared/src/commonMain/
│   ├── kotlin/org/example/fakeshop_clients/core/
│   │   └── design/
│   │       └── DesignTokens.kt              ← SOURCE OF TRUTH (colors, typography, spacing)
│   └── resources/strings/
│       ├── en.json                           ← SOURCE OF TRUTH (English strings)
│       ├── es.json                           ← Spanish translation
│       └── fr.json                           ← French translation (add as needed)
│
├── shared/build/generated/strings/kotlin/
│   └── .../Strings.kt                       ← GENERATED (Kotlin accessor)
│
├── composeApp/src/androidMain/
│   ├── kotlin/.../ui/theme/
│   │   ├── Color.kt                         ← Reads from DesignTokens
│   │   ├── Theme.kt                         ← Unchanged
│   │   └── Typography.kt                    ← Reads from DesignTokens
│   └── res/
│       ├── values/strings.xml               ← GENERATED (English)
│       └── values-es/strings.xml            ← GENERATED (Spanish)
│
├── iosApp/iosApp/
│   ├── Theme/
│   │   └── FakeShopColors.swift             ← GENERATED from DesignTokens.kt
│   ├── en.lproj/
│   │   ├── Localizable.strings              ← GENERATED
│   │   └── Localizable.stringsdict          ← GENERATED (plurals)
│   └── es.lproj/
│       ├── Localizable.strings              ← GENERATED
│       └── Localizable.stringsdict          ← GENERATED (plurals)
│
├── web/common/src/commonMain/resources/
│   ├── css/shared/
│   │   └── theme.css                        ← GENERATED from DesignTokens
│   └── strings/
│       ├── en.json                          ← GENERATED (copy from shared)
│       └── es.json                          ← GENERATED (copy from shared)
│
└── docs/
    └── shared-resources-guide.md            ← This file
```

---

## Adding a New Color or String

### Adding a new color

1. Add the color value to `DesignTokens.kt` (both `Light` and `Dark` objects)
2. Android: Reference it in `Color.kt` → `Color(Colors.Light.NewColor)`
3. iOS SwiftUI: Run `./gradlew generateIosColors` (regenerates `FakeShopColors.swift`)
4. Web: Run `./gradlew :web:common:generateThemeCss` (or it runs automatically on build)

### Adding a new string

1. Add the key/value to `shared/src/commonMain/resources/strings/en.json`
2. Add translations to other locale files (`es.json`, etc.)
3. Run `./gradlew generateAndroidStrings generateIosStrings generateWebStrings generateStringKeys`
4. Use it:
   - **Android**: `stringResource(R.string.new_key)`
   - **iOS**: `NSLocalizedString("new_key", comment: "")`
   - **Web**: Read from the JSON using your i18n approach
   - **Shared Kotlin**: `Strings.NEW_KEY` (for non-localized contexts)

### Adding a new locale

1. Create a new JSON file: `shared/src/commonMain/resources/strings/de.json`
2. Translate all keys
3. Run the generation tasks — the Gradle tasks automatically pick up any `*.json` file in the strings directory
4. **iOS only**: Add the new language in Xcode → Project → Info → Localizations
