package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Simple H Teacher palette shared by every destination. */
private val WorkbenchColors = lightColorScheme(
    primary = Color(0xFF102A43), onPrimary = Color.White,
    primaryContainer = Color(0xFFE4F5EF), onPrimaryContainer = Color(0xFF102A43),
    secondary = Color(0xFF168A78), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4F5EF), onSecondaryContainer = Color(0xFF102A43),
    tertiary = Color(0xFFFFC857), onTertiary = Color(0xFF102A43),
    background = Color(0xFFF7F9FB), onBackground = Color(0xFF102A43),
    surface = Color.White, onSurface = Color(0xFF102A43),
    surfaceVariant = Color(0xFFE7F3F7), onSurfaceVariant = Color(0xFF627486),
    outline = Color(0xFFDCE5EA), error = Color(0xFFB42318)
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
