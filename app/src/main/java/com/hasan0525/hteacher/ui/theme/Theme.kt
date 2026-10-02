package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Workbench 2026 identity shared by all six active destinations. */
private val WorkbenchColors = lightColorScheme(
    primary = Color(0xFF173342), onPrimary = Color.White,
    primaryContainer = Color(0xFFE7F4ED), onPrimaryContainer = Color(0xFF173342),
    secondary = Color(0xFF147968), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7F4ED), onSecondaryContainer = Color(0xFF173342),
    tertiary = Color(0xFFDBEF8B), onTertiary = Color(0xFF173342),
    background = Color(0xFFF6F5F1), onBackground = Color(0xFF173342),
    surface = Color.White, onSurface = Color(0xFF173342),
    surfaceVariant = Color(0xFFE9F1F2), onSurfaceVariant = Color(0xFF71817F),
    outline = Color(0xFFE2E8E4), error = Color(0xFFB43E4E)
)

@Composable
fun HTeacherTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WorkbenchColors,
        typography = AppTypography,
        shapes = TeacherShapes,
        content = content
    )
}
