package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Distinct visual structures: masthead, bookshelf, timeline, tool rail and exam stages. */
@Composable
fun WorkspaceMasthead(
    kicker: String,
    title: String,
    caption: String,
    icon: EduGlyph,
    modifier: Modifier = Modifier,
    onIconClick: (() -> Unit)? = null
) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(kicker, color = Edu.Teal, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(title, color = Edu.Navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(caption, color = Edu.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Surface(
            onClick = onIconClick ?: {},
            enabled = onIconClick != null,
            color = Edu.Navy,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.size(59.dp)
        ) { Box(contentAlignment = Alignment.Center) { Glyph(icon, Modifier.size(29.dp), Edu.Paper) } }
    }
}

@Composable
fun WorkspaceAction(
    title: String,
    caption: String,
    icon: EduGlyph,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (dark) Edu.Navy else Edu.Paper,
        shape = RoundedCornerShape(23.dp),
        border = if (dark) null else BorderStroke(1.dp, Edu.Line)
    ) {
        Row(
            Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(47.dp).clip(RoundedCornerShape(16.dp))
                    .background(if (dark) Edu.Paper.copy(alpha = .13f) else Edu.BlueSoft),
                contentAlignment = Alignment.Center
            ) { Glyph(icon, Modifier.size(25.dp), if (dark) Edu.Paper else Edu.Blue) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, color = if (dark) Edu.Paper else Edu.Navy, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text(caption, color = if (dark) Edu.Paper.copy(alpha = .77f) else Edu.Muted,
                    style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
            Glyph(EduGlyph.ARROW, Modifier.size(18.dp), if (dark) Edu.Paper else Edu.Muted)
        }
    }
}

@Composable
fun LibraryBookCover(
    title: String,
    subtitle: String,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shades = listOf(Edu.Navy, Edu.Teal, Color(0xFF955D42), Color(0xFF475D8C), Color(0xFF52644F))
    val tone = shades[index % shades.size]
    Column(
        modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick)
            .background(Edu.Paper).padding(7.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().height(148.dp)
                .clip(RoundedCornerShape(15.dp)).background(tone)
        ) {
            // Book-spine accent and baseline deliberately evoke a real book cover.
            Box(Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterStart)
                .background(Color.White.copy(alpha = .23f)))
            Column(
                Modifier.fillMaxSize().padding(15.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Glyph(EduGlyph.BOOK, Modifier.size(34.dp), Color.White)
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = .45f))
                    Text("H / LIBRARY", color = Color.White.copy(alpha = .72f),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(title, maxLines = 2, overflow = TextOverflow.Ellipsis, color = Edu.Navy,
            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 5.dp))
        Spacer(Modifier.height(3.dp))
        Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis,
            color = Edu.Muted, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 5.dp))
    }
}

@Composable
fun WorkflowSteps(current: Int, titles: List<String>, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        titles.forEachIndexed { i, title ->
            Column(
                Modifier.weight(1f).clickable { onSelect(i) },
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(27.dp).clip(CircleShape)
                        .background(if (current == i) Edu.Navy else if (current > i) Edu.Teal else Edu.BlueSoft),
                        contentAlignment = Alignment.Center) {
                        Text(if (current > i) "✓" else "${i + 1}",
                            color = if (current >= i) Color.White else Edu.Navy,
                            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(title, color = if (current == i) Edu.Navy else Edu.Muted,
                        maxLines = 1, style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (current == i) FontWeight.Bold else FontWeight.Normal)
                }
                Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape)
                    .background(if (current >= i) Edu.Teal else Edu.Line))
            }
        }
    }
}

@Composable
fun TimelineEntry(
    heading: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    last: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(16.dp).clip(CircleShape).background(Edu.Teal))
            if (!last) Box(Modifier.width(2.dp).heightIn(min = 156.dp)
                .background(Edu.Line))
        }
        Column(Modifier.weight(1f).padding(bottom = 13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(heading, color = Edu.Navy, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = Edu.Teal, style = MaterialTheme.typography.labelMedium)
            content()
        }
    }
}
