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
            const val PrimaryDark = 0xFF4F378BL
            const val PrimaryLight = 0xFFD0BCFFL
            const val OnPrimary = 0xFFFFFFFFL
            const val PrimaryContainer = 0xFFEADDFFL
            const val OnPrimaryContainer = 0xFF21005DL

            // Secondary
            const val Secondary = 0xFF625B71L
            const val SecondaryDark = 0xFF4A4458L
            const val SecondaryLight = 0xFFCCC2DCL
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

            // Warning / Rating
            const val Warning = 0xFFFFB300L
            const val WarningContainer = 0xFFFFF3E0L

            // Favorite / Like
            const val Favorite = 0xFFFF4444L
            const val FavoriteContainer = 0xFFFFE8E8L

            // Outline
            const val Outline = 0xFF79747EL
            const val OutlineVariant = 0xFFCAC4D0L

            // Disabled
            const val DisabledBackground = 0xFFE0E0E0L
            const val DisabledContent = 0xFF9E9E9EL
        }

        object Dark {
            // Primary
            const val Primary = 0xFFD0BCFFL
            const val PrimaryDark = 0xFFEADDFFL
            const val PrimaryLight = 0xFF4F378BL
            const val OnPrimary = 0xFF381E72L
            const val PrimaryContainer = 0xFF4F378BL
            const val OnPrimaryContainer = 0xFFEADDFFL

            // Secondary
            const val Secondary = 0xFFCCC2DCL
            const val SecondaryDark = 0xFFE8DEF8L
            const val SecondaryLight = 0xFF4A4458L
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

            // Error
            const val Error = 0xFFCF6679L
            const val OnError = 0xFF000000L
            const val ErrorContainer = 0xFF93000AL
            const val OnErrorContainer = 0xFFF9DEDCL

            // Success
            const val Success = 0xFF81C784L
            const val SuccessContainer = 0xFF1B5E20L

            // Warning / Rating
            const val Warning = 0xFFFFD54FL
            const val WarningContainer = 0xFF5D4037L

            // Favorite / Like
            const val Favorite = 0xFFFF6B6BL
            const val FavoriteContainer = 0xFF5C1A1AL

            // Outline
            const val Outline = 0xFF938F99L
            const val OutlineVariant = 0xFF49454FL

            // Disabled
            const val DisabledBackground = 0xFF424242L
            const val DisabledContent = 0xFF757575L
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
