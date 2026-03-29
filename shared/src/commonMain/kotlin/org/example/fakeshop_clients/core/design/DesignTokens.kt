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
            const val Primary = 0xFF555992L
            const val PrimaryDark = 0xFF3B4083L
            const val PrimaryLight = 0xFFBEC2FFL
            const val OnPrimary = 0xFFFFFFFFL
            const val PrimaryContainer = 0xFFE0E0FFL
            const val OnPrimaryContainer = 0xFF3E4278L

            // Secondary
            const val Secondary = 0xFF5C5D72L
            const val SecondaryDark = 0xFF454556L
            const val SecondaryLight = 0xFFE2E0F6L
            const val OnSecondary = 0xFFFFFFFFL
            const val SecondaryContainer = 0xFFE1E0F9L
            const val OnSecondaryContainer = 0xFF444559L

            // Tertiary
            const val Tertiary = 0xFF845416L
            const val OnTertiary = 0xFFFFFFFFL
            const val TertiaryContainer = 0xFFFFDCBBL
            const val OnTertiaryContainer = 0xFF673D00L

            // Surface
            const val Surface = 0xFFFBF8FFL
            const val SurfaceTint = 0xFF555992L
            const val SurfaceVariant = 0xFFE4E1ECL
            const val OnSurface = 0xFF1B1B21L
            const val OnSurfaceVariant = 0xFF46464FL
            const val SurfaceDim = 0xFFDBD9E0L
            const val SurfaceBright = 0xFFFBF8FFL
            const val SurfaceContainerLowest = 0xFFFFFFFFL
            const val SurfaceContainerLow = 0xFFF5F2FAL
            const val SurfaceContainer = 0xFFF0ECF4L
            const val SurfaceContainerHigh = 0xFFEAE7EFL
            const val SurfaceContainerHighest = 0xFFE4E1E9L

            // Background
            const val Background = 0xFFFBF8FFL
            const val OnBackground = 0xFF1B1B21L

            // Error
            const val Error = 0xFFBA1A1AL
            const val OnError = 0xFFFFFFFFL
            const val ErrorContainer = 0xFFFFDAD6L
            const val OnErrorContainer = 0xFF93000AL

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
            const val Outline = 0xFF777680L
            const val OutlineVariant = 0xFFC7C5D0L

            // Inverse
            const val InverseSurface = 0xFF303036L
            const val InverseOnSurface = 0xFFF2EFF7L
            const val InversePrimary = 0xFFBEC2FFL

            // Scrim & Shadow
            const val Scrim = 0xFF000000L
            const val Shadow = 0xFF000000L

            // Disabled
            const val DisabledBackground = 0xFFE4E1E9L
            const val DisabledContent = 0xFF78767AL
        }

        object Dark {
            // Primary
            const val Primary = 0xFFBEC2FFL
            const val PrimaryDark = 0xFFE0E0FFL
            const val PrimaryLight = 0xFF3B4083L
            const val OnPrimary = 0xFF272B60L
            const val PrimaryContainer = 0xFF3E4278L
            const val OnPrimaryContainer = 0xFFE0E0FFL

            // Secondary
            const val Secondary = 0xFFC5C4DDL
            const val SecondaryDark = 0xFFE2E0F6L
            const val SecondaryLight = 0xFF454556L
            const val OnSecondary = 0xFF2E2F42L
            const val SecondaryContainer = 0xFF444559L
            const val OnSecondaryContainer = 0xFFE1E0F9L

            // Tertiary
            const val Tertiary = 0xFFFBBA73L
            const val OnTertiary = 0xFF482900L
            const val TertiaryContainer = 0xFF673D00L
            const val OnTertiaryContainer = 0xFFFFDCBBL

            // Surface
            const val Surface = 0xFF131318L
            const val SurfaceTint = 0xFFBEC2FFL
            const val SurfaceVariant = 0xFF46464FL
            const val OnSurface = 0xFFE4E1E9L
            const val OnSurfaceVariant = 0xFFC7C5D0L
            const val SurfaceDim = 0xFF131318L
            const val SurfaceBright = 0xFF39393FL
            const val SurfaceContainerLowest = 0xFF0E0E13L
            const val SurfaceContainerLow = 0xFF1B1B21L
            const val SurfaceContainer = 0xFF1F1F25L
            const val SurfaceContainerHigh = 0xFF2A292FL
            const val SurfaceContainerHighest = 0xFF34343AL

            // Background
            const val Background = 0xFF131318L
            const val OnBackground = 0xFFE4E1E9L

            // Error
            const val Error = 0xFFFFB4ABL
            const val OnError = 0xFF690005L
            const val ErrorContainer = 0xFF93000AL
            const val OnErrorContainer = 0xFFFFDAD6L

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
            const val Outline = 0xFF91909AL
            const val OutlineVariant = 0xFF46464FL

            // Inverse
            const val InverseSurface = 0xFFE4E1E9L
            const val InverseOnSurface = 0xFF303036L
            const val InversePrimary = 0xFF555992L

            // Scrim & Shadow
            const val Scrim = 0xFF000000L
            const val Shadow = 0xFF000000L

            // Disabled
            const val DisabledBackground = 0xFF313033L
            const val DisabledContent = 0xFF78767AL
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
