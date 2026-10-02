package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.repository.AppSettings
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** A new RTL-first dashboard. All counts and file titles originate from live repositories. */
private object HomeStyle {
    val Ink = Color(0xFF123B50)
    val Background = Color(0xFFF7F9FD)
    val Subtle = Color(0xFF6F8393)
    val Azure = Color(0xFFE5F3FF)
    val Lavender = Color(0xFFEFECFF)
    val Mint = Color(0xFFE3F8F1)
    val Cream = Color(0xFFFFF2DA)
}

@Composable
fun HomeDashboard(onNavigate: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as HTeacherApplication
    val files by app.container.teacherRepository.curricula.collectAsStateWithLifecycle(initialValue = emptyList())
    val students by app.container.teacherRepository.students.collectAsStateWithLifecycle(initialValue = emptyList())
    val achievements by app.container.teacherRepository.portfolioItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by app.container.settingsRepository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
    val date = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale("ar"))) }
    val greeting = remember { if (LocalTime.now().hour < 12) "صباح الخير 👋" else "مساء الخير 👋" }

    Scaffold(
        containerColor = HomeStyle.Background,
        bottomBar = { EducationBottomNav("home", onNavigate) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            item {
                HomeGreeting(
                    greeting = greeting,
                    name = settings.teacherName.ifBlank { "المعلم" },
                    onSearch = { onNavigate("curricula") },
                    onNotifications = { onNavigate("tools") }
                )
            }
            item {
                HomeHero(date = date) { onNavigate("tools") }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    HomeSectionTitle("إجراءات سريعة")
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        HomeAction("إنشاء اختبار", EduGlyph.EXAM, HomeStyle.Azure, Modifier.weight(1f)) { onNavigate("exams") }
                        HomeAction("إضافة محتوى", EduGlyph.PLUS, HomeStyle.Lavender, Modifier.weight(1f)) { onNavigate("curricula") }
                        HomeAction("الطلاب", EduGlyph.GROUP, HomeStyle.Mint, Modifier.weight(1f)) { onNavigate("tools") }
                        HomeAction("ملفاتي", EduGlyph.FOLDER, HomeStyle.Cream, Modifier.weight(1f)) { onNavigate("curricula") }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeSectionTitle("جدول اليوم", "فتح أدوات المعلم") { onNavigate("tools") }
                    // A timetable datasource is not yet available in the current repository.
                    // Never present illustrative lessons as real teacher appointments.
                    Surface(
                        color = Color.White, shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Box(
                                Modifier.width(4.dp).height(68.dp).background(
                                    Brush.verticalGradient(listOf(Color(0xFF58BFA7), Color(0xFF7EB8FA))),
                                    RoundedCornerShape(4.dp)
                                )
                            )
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("جدولك اليوم", color = HomeStyle.Ink, fontWeight = FontWeight.Bold)
                                Text("لا يوجد جدول حصص مرتبط حتى الآن", color = HomeStyle.Subtle,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Glyph(EduGlyph.DATE, Modifier.size(28.dp), HomeStyle.Ink)
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeSectionTitle("إحصائيات سريعة")
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        HomeMetric("الاختبارات", "—", EduGlyph.EXAM, HomeStyle.Azure, Modifier.weight(1f))
                        HomeMetric("المحتوى", files.size.toString(), EduGlyph.DOC, HomeStyle.Lavender, Modifier.weight(1f))
                        HomeMetric("الطلاب", students.size.toString(), EduGlyph.GROUP, HomeStyle.Mint, Modifier.weight(1f))
                        HomeMetric("الإنجازات", achievements.size.toString(), EduGlyph.CHECK, HomeStyle.Cream, Modifier.weight(1f))
                    }
                    Text("— = لم يُربط مصدر الإحصائية بعد", style = MaterialTheme.typography.labelSmall,
                        color = HomeStyle.Subtle)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    HomeSectionTitle("آخر ما أضيف", "عرض الكل") { onNavigate("curricula") }
                    if (files.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigate("curricula") },
                            color = Color.White, shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Glyph(EduGlyph.PLUS, Modifier.size(28.dp), HomeStyle.Ink)
                                Column {
                                    Text("أضف أول ملف تعليمي", color = HomeStyle.Ink, fontWeight = FontWeight.Bold)
                                    Text("ابدأ من المكتبة", style = MaterialTheme.typography.bodySmall, color = HomeStyle.Subtle)
                                }
                            }
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(files.take(8), key = { _, file -> file.id }) { index, file ->
                                HomeRecentFile(file.title, index) { onNavigate("curricula") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeGreeting(greeting: String, name: String, onSearch: () -> Unit, onNotifications: () -> Unit) {
    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Box(Modifier.size(58.dp).clip(CircleShape).background(HomeStyle.Lavender),
            contentAlignment = Alignment.Center) {
            Glyph(EduGlyph.GROUP, Modifier.size(29.dp), HomeStyle.Ink)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(greeting, color = HomeStyle.Ink, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge)
            Text(name, color = HomeStyle.Ink, style = MaterialTheme.typography.titleMedium)
            Text("كل يوم فرصة جديدة لصناعة أثر جميل", color = HomeStyle.Subtle,
                style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
        HomeCircleAction(EduGlyph.SEARCH, onSearch)
        HomeCircleAction(EduGlyph.MORE, onNotifications)
    }
}

@Composable
private fun HomeCircleAction(glyph: EduGlyph, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.size(44.dp),
        shape = CircleShape, color = Color.White, shadowElevation = 3.dp) {
        Box(contentAlignment = Alignment.Center) { Glyph(glyph, Modifier.size(23.dp), HomeStyle.Ink) }
    }
}

@Composable
private fun HomeHero(date: String, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(30.dp), modifier = Modifier.fillMaxWidth(),
        color = HomeStyle.Azure, shadowElevation = 5.dp) {
        Box(
            Modifier.fillMaxWidth().heightIn(min = 205.dp).background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFE7F2FC), Color(0xFFFFF7E9), Color(0xFFD6ECED))
                )
            )
        ) {
            Column(Modifier.fillMaxWidth(.75f).padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(date, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold, color = HomeStyle.Ink)
                Text("يوم مليء\nبالفرص التعليمية", color = HomeStyle.Ink,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold)
                Text("لننظم يومنا ونحقق المزيد", color = HomeStyle.Subtle,
                    style = MaterialTheme.typography.bodySmall)
                Surface(onClick = onClick, color = HomeStyle.Ink, shape = CircleShape,
                    modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Glyph(EduGlyph.ARROW, Modifier.size(23.dp), Color.White)
                    }
                }
            }
            // Visual illustration placeholder: avoids bundling an unlicensed stock photo.
            Column(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Glyph(EduGlyph.BOOK, Modifier.size(58.dp), Color(0xFF7DABA4))
                Spacer(Modifier.height(10.dp))
                Glyph(EduGlyph.SPARK, Modifier.size(25.dp), Color(0xFFDBB56D))
            }
        }
    }
}

@Composable
private fun HomeSectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), color = HomeStyle.Ink,
            fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action, color = Color(0xFF3487AE), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun HomeAction(title: String, icon: EduGlyph, shade: Color,
    modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier.height(106.dp),
        color = shade, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(horizontal = 3.dp, vertical = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween) {
            Glyph(icon, Modifier.size(36.dp), HomeStyle.Ink)
            Text(title, style = MaterialTheme.typography.labelSmall,
                color = HomeStyle.Ink, fontWeight = FontWeight.Bold,
                maxLines = 2)
        }
    }
}

@Composable
private fun HomeMetric(title: String, value: String, icon: EduGlyph, shade: Color,
    modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Color.White, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(horizontal = 7.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Box(Modifier.size(35.dp).background(shade, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) {
                Glyph(icon, Modifier.size(21.dp), HomeStyle.Ink)
            }
            Text(value, style = MaterialTheme.typography.titleLarge,
                color = HomeStyle.Ink, fontWeight = FontWeight.ExtraBold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = HomeStyle.Subtle,
                maxLines = 1)
        }
    }
}

@Composable
private fun HomeRecentFile(title: String, index: Int, onClick: () -> Unit) {
    val tones = listOf(HomeStyle.Azure, HomeStyle.Cream, HomeStyle.Mint, HomeStyle.Lavender)
    Surface(onClick = onClick, modifier = Modifier.width(162.dp), color = Color.White,
        shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
        Column {
            Box(Modifier.fillMaxWidth().height(105.dp).background(tones[index % tones.size]),
                contentAlignment = Alignment.Center) {
                Glyph(EduGlyph.BOOK, Modifier.size(44.dp), HomeStyle.Ink)
            }
            Column(Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(title, color = HomeStyle.Ink, fontWeight = FontWeight.Bold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium)
                Text("منهج محفوظ", color = HomeStyle.Subtle,
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
