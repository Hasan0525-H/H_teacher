package com.hasan0525.hteacher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF116A5B), onPrimary = Color.White,
    primaryContainer = Color(0xFFD5F3EA), onPrimaryContainer = Color(0xFF073B33),
    secondary = Color(0xFF52665F), background = Color(0xFFF7FAF8), surface = Color.White,
    surfaceVariant = Color(0xFFE5ECE8), onSurface = Color(0xFF18201D), onSurfaceVariant = Color(0xFF59635F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7AD7C1), onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF075043), onPrimaryContainer = Color(0xFF9CF4DC),
    secondary = Color(0xFFB7CCC4), background = Color(0xFF0F1513), surface = Color(0xFF171D1B),
    surfaceVariant = Color(0xFF27312D), onSurface = Color(0xFFE4EAE7), onSurfaceVariant = Color(0xFFBEC9C4)
)

@Composable
fun HTeacherTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, typography = AppTypography, content = content)
}
