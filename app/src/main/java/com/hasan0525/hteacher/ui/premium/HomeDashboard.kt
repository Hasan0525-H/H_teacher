package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun HomeDashboard(onNavigate: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as HTeacherApplication
    val files by app.container.teacherRepository.curricula.collectAsStateWithLifecycle(initialValue = emptyList())
    val students by app.container.teacherRepository.students.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by app.container.settingsRepository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())

    Scaffold(containerColor = Edu.Canvas, bottomBar = { EducationBottomNav("home", onNavigate) }) { inset ->
        LazyColumn(
            Modifier.fillMaxSize().padding(inset),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("المعلم H", color = Edu.Blue, style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold)
                        Text("مرحباً، ${settings.teacherName.ifBlank { "يا معلم" }}",
                            color = Edu.Navy, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Surface(onClick = { onNavigate("portfolio") }, modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(15.dp), color = Edu.Navy) {
                        Box(contentAlignment = Alignment.Center) { Glyph(EduGlyph.GROUP, Modifier.size(22.dp), Color.White) }
                    }
                }
            }
            item {
                Surface(color = Edu.Navy, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                        Box(Modifier.size(48.dp).padding(2.dp), contentAlignment = Alignment.Center) {
                            Glyph(EduGlyph.SPARK, Modifier.size(36.dp), Edu.Amber)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("ابدأ عملك بسرعة", color = Color.White, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium)
                            Text("كل أدواتك التعليمية في مكان واحد", color = Color.White.copy(.78f),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item { SectionHeader("الوصول السريع") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HomeAction("إضافة كتاب", EduGlyph.PLUS, Edu.BlueSoft, Modifier.weight(1f)) { onNavigate("curricula") }
                        HomeAction("إنشاء اختبار", EduGlyph.EXAM, Edu.AmberSoft, Modifier.weight(1f)) { onNavigate("exams") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HomeAction("الطلاب", EduGlyph.GROUP, Edu.Mint, Modifier.weight(1f)) { onNavigate("tools") }
                        HomeAction("ملف الإنجاز", EduGlyph.FOLDER, Color(0xFFF1ECFF), Modifier.weight(1f)) { onNavigate("portfolio") }
                    }
                }
            }
            item { SectionHeader("كتبك", action = if (files.isNotEmpty()) "عرض الكل" else null,
                onAction = { onNavigate("curricula") }) }
            if (files.isEmpty()) {
                item { Surface(onClick = { onNavigate("curricula") }, color = Edu.Paper, shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Edu.Line)) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Glyph(EduGlyph.BOOK, Modifier.size(26.dp), Edu.Blue)
                        Column(Modifier.weight(1f)) { Text("لم تضف كتاباً بعد", color = Edu.Navy, fontWeight = FontWeight.Bold); Text("أضف منهجاً للبدء", color = Edu.Muted, style = MaterialTheme.typography.bodySmall) }
                        Glyph(EduGlyph.ARROW, Modifier.size(18.dp), Edu.Muted)
                    }
                } }
            } else {
                items(files.take(3), key = { it.id }) { file ->
                    FeatureCard(file.title, "فتح من المكتبة", EduGlyph.BOOK, accent = Edu.Blue) { onNavigate("curricula") }
                }
            }
            if (students.isNotEmpty()) item {
                Text("${students.size} طالب مسجل", color = Edu.Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HomeAction(title: String, icon: EduGlyph, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier.height(112.dp), color = color, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Glyph(icon, Modifier.size(26.dp), Edu.Navy)
            Text(title, color = Edu.Navy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        }
    }
}
