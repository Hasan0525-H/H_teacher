package com.hasan0525.hteacher.ui.premium

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Workbench 2026: ink, sea glass and soft citron—not the legacy theme. */
object Edu {
    val Navy = Color(0xFF173342)
    val Blue = Color(0xFF2B6170)
    val BlueSoft = Color(0xFFE9F1F2)
    val Teal = Color(0xFF147968)
    val Mint = Color(0xFFE7F4ED)
    val Canvas = Color(0xFFF6F5F1)
    val Paper = Color(0xFFFFFFFF)
    val Line = Color(0xFFE2E8E4)
    val Muted = Color(0xFF71817F)
    val Amber = Color(0xFFDBEF8B)
    val AmberSoft = Color(0xFFF1F8D6)
    val Error = Color(0xFFB43E4E)
}

/** Consistent density and spacing scale for the six active destinations. */
object EduSpace {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 18.dp
    val lg = 24.dp
    val xl = 32.dp
}

@Composable
fun AppTopBar(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null,
              action: (@Composable () -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().background(Edu.Canvas).statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (onBack != null) {
            Surface(
                onClick = onBack, shape = RoundedCornerShape(14.dp),
                color = Edu.Paper, border = BorderStroke(1.dp, Edu.Line),
                modifier = Modifier.size(44.dp)
            ) { Box(contentAlignment = Alignment.Center) { Glyph(EduGlyph.BACK, Modifier.size(22.dp), Edu.Navy) } }
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold, color = Edu.Navy)
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Edu.Muted, maxLines = 1)
            }
        }
        action?.invoke()
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true,
                  icon: EduGlyph? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 54.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Edu.Navy, contentColor = Edu.Paper,
            disabledContainerColor = Edu.Line, disabledContentColor = Edu.Muted
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 11.dp)
    ) {
        if (icon != null) { Glyph(icon, Modifier.size(21.dp), Edu.Paper); Spacer(Modifier.width(9.dp)) }
        Text(text, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true,
                    onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 50.dp),
        border = BorderStroke(1.dp, Edu.Line), shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Edu.Navy),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) { Text(text, color = Edu.Navy, maxLines = 1, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.background(Edu.Paper, RoundedCornerShape(22.dp))
            .then(Modifier.padding(1.dp)).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null,
                  action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = Edu.Navy, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold)
            subtitle?.let { Text(it, color = Edu.Muted, style = MaterialTheme.typography.bodySmall) }
        }
        if (action != null && onAction != null) TextButton(onClick = onAction) {
            Text(action, color = Edu.Teal, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, icon: EduGlyph,
                modifier: Modifier = Modifier, accent: Color = Edu.Blue,
                onClick: () -> Unit) {
    Row(
        modifier.background(Edu.Paper, RoundedCornerShape(19.dp)).clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(46.dp).background(accent.copy(alpha = .12f), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) { Glyph(icon, Modifier.size(25.dp), accent) }
        Column(Modifier.weight(1f)) {
            Text(title, color = Edu.Navy, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, color = Edu.Muted, style = MaterialTheme.typography.bodySmall,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Glyph(EduGlyph.ARROW, Modifier.size(18.dp), Edu.Muted)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: EduGlyph,
             modifier: Modifier = Modifier, tint: Color = Edu.Blue) {
    Column(
        modifier.background(Edu.Paper, RoundedCornerShape(18.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Glyph(icon, Modifier.size(23.dp), tint)
        Text(value, style = MaterialTheme.typography.headlineSmall,
            color = Edu.Navy, fontWeight = FontWeight.Black)
        Text(title, color = Edu.Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, icon: EduGlyph,
               action: String? = null, onAction: (() -> Unit)? = null) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 33.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Box(Modifier.size(104.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(95.dp).background(Edu.AmberSoft, RoundedCornerShape(29.dp)))
            Box(Modifier.size(66.dp).background(Edu.Mint, RoundedCornerShape(21.dp)),
                contentAlignment = Alignment.Center) { Glyph(icon, Modifier.size(35.dp), Edu.Teal) }
        }
        Text(title, fontWeight = FontWeight.ExtraBold, color = Edu.Navy,
            style = MaterialTheme.typography.titleLarge)
        Text(subtitle, color = Edu.Muted, style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (action != null && onAction != null) {
            PrimaryButton(action, icon = EduGlyph.PLUS, onClick = onAction)
        }
    }
}

@Composable
fun LoadingView(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(19.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(Modifier.size(23.dp), strokeWidth = 2.dp, color = Edu.Teal)
        Text(label, color = Edu.Muted)
    }
}

@Composable
fun FilterChipPill(text: String, selected: Boolean, onClick: () -> Unit) {
    val surface by animateColorAsState(
        targetValue = if (selected) Edu.Navy else Edu.Paper,
        animationSpec = tween(180), label = "filter background"
    )
    val ink by animateColorAsState(
        targetValue = if (selected) Color.White else Edu.Navy,
        animationSpec = tween(180), label = "filter text"
    )
    Surface(
        onClick = onClick, shape = RoundedCornerShape(13.dp), color = surface,
        border = if (selected) null else BorderStroke(1.dp, Edu.Line)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            color = ink, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun FormField(label: String, value: String, onChange: (String) -> Unit,
              modifier: Modifier = Modifier, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value, onValueChange = onChange, modifier = modifier,
        shape = RoundedCornerShape(16.dp), singleLine = singleLine, label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Edu.Teal, unfocusedBorderColor = Edu.Line,
            focusedLabelColor = Edu.Teal, unfocusedContainerColor = Edu.Paper
        )
    )
}

@Composable
fun EducationBottomNav(selected: String, onNavigate: (String) -> Unit) {
    // Purpose-built five-destination dock; it is not Material's default NavigationBar.
    Box(
        Modifier.fillMaxWidth().background(Edu.Canvas).navigationBarsPadding()
            .padding(horizontal = 11.dp, vertical = 7.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp),
            color = Edu.Paper, border = BorderStroke(1.dp, Edu.Line),
            shadowElevation = 7.dp
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                val destinations = listOf(
                    Triple("home", "الرئيسية", EduGlyph.DASH),
                    Triple("curricula", "المكتبة", EduGlyph.BOOK),
                    Triple("exams", "اختبار", EduGlyph.EXAM),
                    Triple("portfolio", "الإنجاز", EduGlyph.FOLDER),
                    Triple("tools", "الفصل", EduGlyph.GROUP)
                )
                destinations.forEach { (route, title, icon) ->
                    val active = selected == route
                    val iconColor by animateColorAsState(
                        if (active) Edu.Paper else Edu.Muted,
                        tween(180), label = "dock icon"
                    )
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                            .clickable { onNavigate(route) }
                            .padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            Modifier.size(if (active) 39.dp else 35.dp)
                                .background(if (active) Edu.Navy else Color.Transparent,
                                    RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) { Glyph(icon, Modifier.size(22.dp), iconColor) }
                        Text(
                            title, maxLines = 1,
                            color = if (active) Edu.Navy else Edu.Muted,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
