package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.repository.AppSettings
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeDashboard(onNavigate: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as HTeacherApplication
    val repo = app.container.teacherRepository
    val files by repo.curricula.collectAsStateWithLifecycle(initialValue = emptyList())
    val students by repo.students.collectAsStateWithLifecycle(initialValue = emptyList())
    val portfolio by repo.portfolioItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val profile by app.container.settingsRepository.settings.collectAsStateWithLifecycle(
        initialValue = AppSettings()
    )
    val today = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))) }

    Scaffold(containerColor = Edu.Canvas, bottomBar = { EducationBottomNav("home", onNavigate) }) { insets ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(insets),
            contentPadding = PaddingValues(bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp)
        ) {
            item {
                WorkspaceMasthead(
                    kicker = today,
                    title = profile.teacherName.ifBlank { "صباح العمل" },
                    caption = "لوحة إدارة يومك الدراسي",
                    icon = EduGlyph.DASH,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            item {
                // The main screen is a daily workspace rather than a vertical stack of cards.
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        .background(Edu.Navy, RoundedCornerShape(30.dp)).padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("مركز العمل", color = Edu.Paper.copy(alpha = .78f),
                                style = MaterialTheme.typography.labelLarge)
                            Text("كل ما تحتاجه\nليوم دراسي منظم", color = Edu.Paper,
                                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        Glyph(EduGlyph.SPARK, Modifier.size(47.dp), Edu.Amber)
                    }
                    HorizontalDivider(color = Edu.Paper.copy(alpha = .18f))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(
                            "منهج" to files.size,
                            "طالب" to students.size,
                            "إنجاز" to portfolio.size
                        ).forEach { (label, value) ->
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(value.toString(), style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black, color = Edu.Paper)
                                Text(label, style = MaterialTheme.typography.bodySmall,
                                    color = Edu.Paper.copy(alpha = .8f))
                            }
                        }
                    }
                    Button(
                        onClick = { onNavigate("exams") },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Edu.Amber)
                    ) {
                        Glyph(EduGlyph.EXAM, Modifier.size(22.dp), Edu.Navy)
                        Spacer(Modifier.width(10.dp))
                        Text("إنشاء اختبار جديد", color = Edu.Navy, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Glyph(EduGlyph.ARROW, Modifier.size(19.dp), Edu.Navy)
                    }
                }
            }

            item {
                Column(
                    Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader("مسارات العمل", "افتح القسم الذي تريد العمل عليه")
                    WorkspaceAction(
                        title = "المكتبة التعليمية",
                        caption = "تصفح الكتب وأنشئ وحداتك ودروسك",
                        icon = EduGlyph.BOOK,
                        modifier = Modifier.fillMaxWidth()
                    ) { onNavigate("curricula") }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            WorkspaceAction(
                                title = "الفصل",
                                caption = "الحضور والدرجات",
                                icon = EduGlyph.GROUP,
                                modifier = Modifier.fillMaxWidth()
                            ) { onNavigate("tools") }
                        }
                        Column(Modifier.weight(1f)) {
                            WorkspaceAction(
                                title = "إنجازاتي",
                                caption = "الشهادات والشواهد",
                                icon = EduGlyph.FOLDER,
                                modifier = Modifier.fillMaxWidth()
                            ) { onNavigate("portfolio") }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("رفّ الكتب", color = Edu.Navy, fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge)
                            Text("آخر المناهج المحفوظة", color = Edu.Muted,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onNavigate("curricula") }) { Text("المكتبة ←") }
                    }
                    if (files.isEmpty()) {
                        Box(Modifier.padding(horizontal = 20.dp)) {
                            WorkspaceAction("أضف أول كتاب", "احتفظ بمناهجك داخل الجهاز",
                                EduGlyph.PLUS, Modifier.fillMaxWidth()) { onNavigate("curricula") }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(13.dp)
                        ) {
                            itemsIndexed(files.take(8), key = { _, item -> item.id }) { index, file ->
                                LibraryBookCover(
                                    title = file.title,
                                    subtitle = "ملف PDF",
                                    index = index,
                                    modifier = Modifier.width(166.dp)
                                ) { onNavigate("curricula") }
                            }
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("مساحتك المهنية", "سجل الشواهد والتطور المهني")
                    WorkspaceAction("ملف الإنجاز المهني", "${portfolio.size} إنجاز محفوظ",
                        EduGlyph.CHECK, Modifier.fillMaxWidth(), dark = true) {
                        onNavigate("portfolio")
                    }
                }
            }
        }
    }
}
