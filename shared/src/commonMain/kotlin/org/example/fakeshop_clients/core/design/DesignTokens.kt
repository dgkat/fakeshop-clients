package org.example.fakeshop_clients.core.design

/**
 * Single source of truth for all design tokens.
 * All color values are ARGB hex as Long constants.
 *
 * Consumed by:
 * - Android Compose: Color(DesignTokens.Colors.Light.PRIMARY)
 * - iOS SwiftUI: via FakeShopColors Swift extension (reads from Kotlin)
 * - Web: generated into theme.css via Gradle task (generateThemeCss)
 */
object DesignTokens {

    object Colors {
        object Light {
            // Primary
            const val PRIMARY = 0xFF555992L
            const val PRIMARY_DARK = 0xFF3B4083L
            const val PRIMARY_LIGHT = 0xFFBEC2FFL
            const val ON_PRIMARY = 0xFFFFFFFFL
            const val PRIMARY_CONTAINER = 0xFFE0E0FFL
            const val ON_PRIMARY_CONTAINER = 0xFF3E4278L

            // Secondary
            const val SECONDARY = 0xFF5C5D72L
            const val SECONDARY_DARK = 0xFF454556L
            const val SECONDARY_LIGHT = 0xFFE2E0F6L
            const val ON_SECONDARY = 0xFFFFFFFFL
            const val SECONDARY_CONTAINER = 0xFFE1E0F9L
            const val ON_SECONDARY_CONTAINER = 0xFF444559L

            // Tertiary
            const val TERTIARY = 0xFF845416L
            const val ON_TERTIARY = 0xFFFFFFFFL
            const val TERTIARY_CONTAINER = 0xFFFFDCBBL
            const val ON_TERTIARY_CONTAINER = 0xFF673D00L

            // Surface
            const val SURFACE = 0xFFFBF8FFL
            const val SURFACE_TINT = 0xFF555992L
            const val SURFACE_VARIANT = 0xFFE4E1ECL
            const val ON_SURFACE = 0xFF1B1B21L
            const val ON_SURFACE_VARIANT = 0xFF46464FL
            const val SURFACE_DIM = 0xFFDBD9E0L
            const val SURFACE_BRIGHT = 0xFFFBF8FFL
            const val SURFACE_CONTAINER_LOWEST = 0xFFFFFFFFL
            const val SURFACE_CONTAINER_LOW = 0xFFF5F2FAL
            const val SURFACE_CONTAINER = 0xFFF0ECF4L
            const val SURFACE_CONTAINER_HIGH = 0xFFEAE7EFL
            const val SURFACE_CONTAINER_HIGHEST = 0xFFE4E1E9L

            // Background
            const val BACKGROUND = 0xFFFBF8FFL
            const val ON_BACKGROUND = 0xFF1B1B21L

            // Error
            const val ERROR = 0xFFBA1A1AL
            const val ON_ERROR = 0xFFFFFFFFL
            const val ERROR_CONTAINER = 0xFFFFDAD6L
            const val ON_ERROR_CONTAINER = 0xFF93000AL

            // Success
            const val SUCCESS = 0xFF2E7D32L
            const val SUCCESS_CONTAINER = 0xFFE8F5E9L

            // Warning / Rating
            const val WARNING = 0xFFFFB300L
            const val WARNING_CONTAINER = 0xFFFFF3E0L

            // Favorite / Like
            const val FAVORITE = 0xFFFF4444L
            const val FAVORITE_CONTAINER = 0xFFFFE8E8L

            // Outline
            const val OUTLINE = 0xFF777680L
            const val OUTLINE_VARIANT = 0xFFC7C5D0L

            // Inverse
            const val INVERSE_SURFACE = 0xFF303036L
            const val INVERSE_ON_SURFACE = 0xFFF2EFF7L
            const val INVERSE_PRIMARY = 0xFFBEC2FFL

            // Scrim & Shadow
            const val SCRIM = 0xFF000000L
            const val SHADOW = 0xFF000000L

            // Disabled
            const val DISABLED_BACKGROUND = 0xFFE4E1E9L
            const val DISABLED_CONTENT = 0xFF78767AL
        }

        object Dark {
            // Primary
            const val PRIMARY = 0xFFBEC2FFL
            const val PRIMARY_DARK = 0xFFE0E0FFL
            const val PRIMARY_LIGHT = 0xFF3B4083L
            const val ON_PRIMARY = 0xFF272B60L
            const val PRIMARY_CONTAINER = 0xFF3E4278L
            const val ON_PRIMARY_CONTAINER = 0xFFE0E0FFL

            // Secondary
            const val SECONDARY = 0xFFC5C4DDL
            const val SECONDARY_DARK = 0xFFE2E0F6L
            const val SECONDARY_LIGHT = 0xFF454556L
            const val ON_SECONDARY = 0xFF2E2F42L
            const val SECONDARY_CONTAINER = 0xFF444559L
            const val ON_SECONDARY_CONTAINER = 0xFFE1E0F9L

            // Tertiary
            const val TERTIARY = 0xFFFBBA73L
            const val ON_TERTIARY = 0xFF482900L
            const val TERTIARY_CONTAINER = 0xFF673D00L
            const val ON_TERTIARY_CONTAINER = 0xFFFFDCBBL

            // Surface
            const val SURFACE = 0xFF131318L
            const val SURFACE_TINT = 0xFFBEC2FFL
            const val SURFACE_VARIANT = 0xFF46464FL
            const val ON_SURFACE = 0xFFE4E1E9L
            const val ON_SURFACE_VARIANT = 0xFFC7C5D0L
            const val SURFACE_DIM = 0xFF131318L
            const val SURFACE_BRIGHT = 0xFF39393FL
            const val SURFACE_CONTAINER_LOWEST = 0xFF0E0E13L
            const val SURFACE_CONTAINER_LOW = 0xFF1B1B21L
            const val SURFACE_CONTAINER = 0xFF1F1F25L
            const val SURFACE_CONTAINER_HIGH = 0xFF2A292FL
            const val SURFACE_CONTAINER_HIGHEST = 0xFF34343AL

            // Background
            const val BACKGROUND = 0xFF131318L
            const val ON_BACKGROUND = 0xFFE4E1E9L

            // Error
            const val ERROR = 0xFFFFB4ABL
            const val ON_ERROR = 0xFF690005L
            const val ERROR_CONTAINER = 0xFF93000AL
            const val ON_ERROR_CONTAINER = 0xFFFFDAD6L

            // Success
            const val SUCCESS = 0xFF81C784L
            const val SUCCESS_CONTAINER = 0xFF1B5E20L

            // Warning / Rating
            const val WARNING = 0xFFFFD54FL
            const val WARNING_CONTAINER = 0xFF5D4037L

            // Favorite / Like
            const val FAVORITE = 0xFFFF6B6BL
            const val FAVORITE_CONTAINER = 0xFF5C1A1AL

            // Outline
            const val OUTLINE = 0xFF91909AL
            const val OUTLINE_VARIANT = 0xFF46464FL

            // Inverse
            const val INVERSE_SURFACE = 0xFFE4E1E9L
            const val INVERSE_ON_SURFACE = 0xFF303036L
            const val INVERSE_PRIMARY = 0xFF555992L

            // Scrim & Shadow
            const val SCRIM = 0xFF000000L
            const val SHADOW = 0xFF000000L

            // Disabled
            const val DISABLED_BACKGROUND = 0xFF313033L
            const val DISABLED_CONTENT = 0xFF78767AL
        }
    }

    object Typography {
        // Font sizes in SP/px (same numeric value, unit depends on platform)
        const val DISPLAY_LARGE = 57
        const val DISPLAY_MEDIUM = 45
        const val DISPLAY_SMALL = 36

        const val HEADLINE_LARGE = 32
        const val HEADLINE_MEDIUM = 28
        const val HEADLINE_SMALL = 24

        const val TITLE_LARGE = 22
        const val TITLE_MEDIUM = 16
        const val TITLE_SMALL = 14

        const val BODY_LARGE = 16
        const val BODY_MEDIUM = 14
        const val BODY_SMALL = 12

        const val LABEL_LARGE = 14
        const val LABEL_MEDIUM = 12
        const val LABEL_SMALL = 11

        // Line heights in SP/px
        const val DISPLAY_LARGE_LINE_HEIGHT = 64
        const val HEADLINE_MEDIUM_LINE_HEIGHT = 36
        const val TITLE_LARGE_LINE_HEIGHT = 28
        const val TITLE_MEDIUM_LINE_HEIGHT = 24
        const val BODY_LARGE_LINE_HEIGHT = 24
        const val BODY_MEDIUM_LINE_HEIGHT = 20
        const val LABEL_SMALL_LINE_HEIGHT = 16

        // Letter spacing in SP/px
        const val TITLE_MEDIUM_LETTER_SPACING = 0.15f
        const val BODY_LARGE_LETTER_SPACING = 0.5f
        const val BODY_MEDIUM_LETTER_SPACING = 0.25f
        const val LABEL_SMALL_LETTER_SPACING = 0.5f

        // Font weights (numeric CSS/Compose values)
        const val WEIGHT_REGULAR = 400
        const val WEIGHT_MEDIUM = 500
        const val WEIGHT_SEMI_BOLD = 600
        const val WEIGHT_BOLD = 700

        // Web font family (Android/iOS use platform default)
        const val WEB_FONT_FAMILY =
            "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', sans-serif"
    }

    object Spacing {
        const val XS = 4
        const val SM = 8
        const val MD = 12
        const val LG = 16
        const val XL = 24
        const val XXL = 32
        const val XXXL = 48
    }

    object BorderRadius {
        const val XS = 4
        const val SM = 8
        const val MD = 12
        const val LG = 16
        const val XL = 24
        const val FULL = 9999
    }
}
