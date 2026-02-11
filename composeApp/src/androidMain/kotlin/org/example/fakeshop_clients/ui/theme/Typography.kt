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
