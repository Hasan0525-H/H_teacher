package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.repository.AppSettings

private object SimpleHome {
    val ink = Color(0xFF193B49)
    val muted = Color(0xFF72848B)
    val background = Color(0xFFF7F9FB)
    val lilac = Color(0xFFF1EDFD)
    val sky = Color(0xFFE9F4FF)
    val mint = Color(0xFFE8F6EF)
    val sand = Color(0xFFFFF2E0)
}

/** Minimal starting screen. Keep every feature one obvious tap away. */
@Composable
fun HomeDashboard(onNavigate: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as HTeacherApplication
    val files by app.container.teacherRepository.curricula.collectAsStateWithLifecycle(initialValue = emptyList())
    val students by app.container.teacherRepository.students.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by app.container.settingsRepository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())

    Scaffold(
        containerColor = SimpleHome.background,
        bottomBar = { EducationBottomNav("home", onNavigate) }
    ) { inset ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inset),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth().statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "مرحباً، " + settings.teacherName.ifBlank { "يا معلم" },
                            color = SimpleHome.ink, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        onClick = { onNavigate("curricula") },
                        modifier = Modifier.size(48.dp),
                        color = Color.White, shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Glyph(EduGlyph.SEARCH, Modifier.size(24.dp), SimpleHome.ink)
                        }
                    }
                }
            }
            item {
                Text("ماذا تريد أن تفعل؟", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = SimpleHome.ink)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SimpleAction("إضافة كتاب", EduGlyph.PLUS, SimpleHome.sky,
                            Modifier.weight(1f)) { onNavigate("curricula") }
                        SimpleAction("إنشاء اختبار", EduGlyph.EXAM, SimpleHome.lilac,
                            Modifier.weight(1f)) { onNavigate("exams") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SimpleAction("الطلاب", EduGlyph.GROUP, SimpleHome.mint,
                            Modifier.weight(1f)) { onNavigate("tools") }
                        SimpleAction("الإنجازات", EduGlyph.FOLDER, SimpleHome.sand,
                            Modifier.weight(1f)) { onNavigate("portfolio") }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("ملفاتي", Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = SimpleHome.ink)
                    TextButton(onClick = { onNavigate("curricula") }) {
                        Text("عرض الكل", color = SimpleHome.ink)
                    }
                }
            }
            if (files.isEmpty()) {
                item {
                    Surface(
                        onClick = { onNavigate("curricula") },
                        shape = RoundedCornerShape(22.dp), color = Color.White
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Glyph(EduGlyph.PLUS, Modifier.size(27.dp), SimpleHome.ink)
                            Text("إضافة كتاب", color = SimpleHome.ink,
                                fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                items(files.take(3), key = { it.id }) { file ->
                    Surface(
                        onClick = { onNavigate("curricula") },
                        color = Color.White, shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(15.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Glyph(EduGlyph.BOOK, Modifier.size(30.dp), SimpleHome.ink)
                            Text(file.title, Modifier.weight(1f), maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = SimpleHome.ink, fontWeight = FontWeight.SemiBold)
                            Glyph(EduGlyph.ARROW, Modifier.size(18.dp), SimpleHome.muted)
                        }
                    }
                }
            }
            if (students.isNotEmpty()) {
                item {
                    Text("الطلاب: ${students.size}", color = SimpleHome.muted,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SimpleAction(
    text: String,
    icon: EduGlyph,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(138.dp),
        color = background,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(17.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Glyph(icon, Modifier.size(39.dp), SimpleHome.ink)
            Text(text, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold, color = SimpleHome.ink)
        }
    }
}
