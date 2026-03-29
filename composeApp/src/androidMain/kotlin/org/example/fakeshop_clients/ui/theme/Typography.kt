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
        fontSize = Typo.DISPLAY_LARGE.sp,
        lineHeight = Typo.DISPLAY_LARGE_LINE_HEIGHT.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.HEADLINE_MEDIUM.sp,
        lineHeight = Typo.HEADLINE_MEDIUM_LINE_HEIGHT.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.TITLE_LARGE.sp,
        lineHeight = Typo.TITLE_LARGE_LINE_HEIGHT.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(Typo.WEIGHT_MEDIUM),
        fontSize = Typo.TITLE_MEDIUM.sp,
        lineHeight = Typo.TITLE_MEDIUM_LINE_HEIGHT.sp,
        letterSpacing = Typo.TITLE_MEDIUM_LETTER_SPACING.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.BODY_LARGE.sp,
        lineHeight = Typo.BODY_LARGE_LINE_HEIGHT.sp,
        letterSpacing = Typo.BODY_LARGE_LETTER_SPACING.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Typo.BODY_MEDIUM.sp,
        lineHeight = Typo.BODY_MEDIUM_LINE_HEIGHT.sp,
        letterSpacing = Typo.BODY_MEDIUM_LETTER_SPACING.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(Typo.WEIGHT_MEDIUM),
        fontSize = Typo.LABEL_SMALL.sp,
        lineHeight = Typo.LABEL_SMALL_LINE_HEIGHT.sp,
        letterSpacing = Typo.LABEL_SMALL_LETTER_SPACING.sp
    )
)
