package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val native = Typography()

/** Editorial headline scale with restrained compact reading text and Arabic system fallback. */
val AppTypography = Typography(
    displaySmall = native.displaySmall.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 34.sp, lineHeight = 43.sp, fontWeight = FontWeight.ExtraBold),
    headlineMedium = native.headlineMedium.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 29.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold),
    headlineSmall = native.headlineSmall.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 25.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold),
    titleLarge = native.titleLarge.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 21.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold),
    titleMedium = native.titleMedium.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 17.sp, lineHeight = 25.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = native.titleSmall.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = native.bodyLarge.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = native.bodyMedium.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall = native.bodySmall.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp, lineHeight = 19.sp),
    labelLarge = native.labelLarge.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = native.labelMedium.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium),
    labelSmall = native.labelSmall.copy(fontFamily = FontFamily.SansSerif,
        fontSize = 11.sp, lineHeight = 15.sp)
)
