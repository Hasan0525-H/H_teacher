package com.hasan0525.hteacher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EducationColors=lightColorScheme(
    primary=Color(0xFF265DE0),onPrimary=Color.White,
    primaryContainer=Color(0xFFEAF0FF),onPrimaryContainer=Color(0xFF162841),
    secondary=Color(0xFF087E83),onSecondary=Color.White,
    secondaryContainer=Color(0xFFE3F3F1),onSecondaryContainer=Color(0xFF162841),
    tertiary=Color(0xFFF4AA40),onTertiary=Color(0xFF162841),
    background=Color(0xFFF6F8FC),onBackground=Color(0xFF162841),
    surface=Color.White,onSurface=Color(0xFF162841),
    surfaceVariant=Color(0xFFF0F3F9),onSurfaceVariant=Color(0xFF68768A),
    outline=Color(0xFFE1E6EF)
)
@Composable
fun HTeacherTheme(darkTheme:Boolean=false,content:@Composable()->Unit){
    MaterialTheme(colorScheme=EducationColors,typography=AppTypography,shapes=TeacherShapes,content=content)
}
