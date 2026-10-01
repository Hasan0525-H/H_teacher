package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TeacherLightColors = lightColorScheme(
    primary = Color(0xFF245B4B), onPrimary = Color.White,
    primaryContainer = Color(0xFFD6EEE5), onPrimaryContainer = Color(0xFF082019),
    secondary = Color(0xFF50665E), onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE9E3), onSecondaryContainer = Color(0xFF0D1F19),
    tertiary = Color(0xFF7A5A2B), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE8C2), onTertiaryContainer = Color(0xFF291A05),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7F9F7), onBackground = Color(0xFF171C19),
    surface = Color.White, onSurface = Color(0xFF171C19),
    surfaceVariant = Color(0xFFEFF4F1), onSurfaceVariant = Color(0xFF63716B),
    outline = Color(0xFFC9D4CF), outlineVariant = Color(0xFFE0E7E3)
)

@Composable
fun HTeacherTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TeacherLightColors, typography = AppTypography, shapes = TeacherShapes, content = content)
}
