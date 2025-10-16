package org.example.fakeshop_clients.webcommon

/**
 * Design tokens that can be referenced in Kotlin code
 * These mirror the CSS variables defined in theme.css
 */
object DesignTokens {

    object Colors {
        const val PRIMARY = "#6750A4"
        const val PRIMARY_DARK = "#4F378B"
        const val PRIMARY_LIGHT = "#D0BCFF"
        const val PRIMARY_CONTAINER = "#EADDFF"
        const val ON_PRIMARY_CONTAINER = "#21005D"

        const val SECONDARY = "#625B71"
        const val SECONDARY_CONTAINER = "#E8DEF8"
        const val ON_SECONDARY_CONTAINER = "#1D192B"

        const val SURFACE = "#FFFFFF"
        const val ON_SURFACE = "#1C1B1F"
        const val BACKGROUND = "#FFFBFE"

        const val ERROR = "#B3261E"
        const val SUCCESS = "#2E7D32"
    }

    object Spacing {
        const val XS = "4px"
        const val SM = "8px"
        const val MD = "12px"
        const val LG = "16px"
        const val XL = "24px"
        const val XXL = "32px"
        const val XXXL = "48px"
    }

    object BorderRadius {
        const val XS = "4px"
        const val SM = "8px"
        const val MD = "12px"
        const val LG = "16px"
        const val XL = "24px"
        const val FULL = "9999px"
    }

    object FontSize {
        const val DISPLAY_LARGE = "57px"
        const val HEADLINE_MEDIUM = "28px"
        const val TITLE_LARGE = "22px"
        const val TITLE_MEDIUM = "16px"
        const val BODY_LARGE = "16px"
        const val BODY_MEDIUM = "14px"
        const val LABEL_SMALL = "11px"
    }
}