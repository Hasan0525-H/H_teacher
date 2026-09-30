package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6B5BFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E5FF),
    onPrimaryContainer = Color(0xFF241B5E),
    secondary = Color(0xFF4F6278),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8F8),
    onSecondaryContainer = Color(0xFF182534),
    tertiary = Color(0xFF8B5E83),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9F3),
    onTertiaryContainer = Color(0xFF35132E),
    background = Color(0xFFF9F9FC),
    onBackground = Color(0xFF17171C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17171C),
    surfaceVariant = Color(0xFFF0F0F5),
    onSurfaceVariant = Color(0xFF686873),
    outline = Color(0xFFD9D9E2)
)

@Composable
fun HTeacherTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
