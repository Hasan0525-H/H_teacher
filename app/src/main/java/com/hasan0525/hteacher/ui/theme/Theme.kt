package com.hasan0525.hteacher.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val TeacherLightColors=lightColorScheme(primary=Color(0xFF2F5D50),onPrimary=Color.White,primaryContainer=Color(0xFFD7EEE5),onPrimaryContainer=Color(0xFF0B211B),secondary=Color(0xFF4D635C),onSecondary=Color.White,secondaryContainer=Color(0xFFDCE9E4),onSecondaryContainer=Color(0xFF0F1F1A),tertiary=Color(0xFF7A5A2B),onTertiary=Color.White,tertiaryContainer=Color(0xFFFFE8C2),onTertiaryContainer=Color(0xFF291A05),background=Color(0xFFF7F9F8),onBackground=Color(0xFF171C1A),surface=Color.White,onSurface=Color(0xFF171C1A),surfaceVariant=Color(0xFFEDF2F0),onSurfaceVariant=Color(0xFF66716D),outline=Color(0xFFD1DAD6),outlineVariant=Color(0xFFE3E9E6))
@Composable fun HTeacherTheme(darkTheme:Boolean=false,content:@Composable()->Unit){MaterialTheme(colorScheme=TeacherLightColors,typography=AppTypography,shapes=TeacherShapes,content=content)}