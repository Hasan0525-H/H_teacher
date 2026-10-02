package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hasan0525.hteacher.ui.theme.HTeacherTheme

// These independent layout previews use representative data, not fabricated live app statistics.
@Preview(name = "01 — Workbench | الرئيسية", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun HomeDesignPreview() {
    HTeacherTheme {
        Column(
            Modifier.fillMaxSize().background(Edu.Canvas).padding(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            WorkspaceMasthead("الأحد • يوم دراسي", "مرحبًا بالمعلم", "مركز عملك", EduGlyph.DASH)
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 19.dp)
                    .background(Edu.Navy, RoundedCornerShape(28.dp)).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                Text("لوحة عملك اليومية", color = Edu.Paper,
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    for ((number, label) in listOf("٤" to "مناهج", "٢٨" to "طالب", "١٢" to "إنجاز")) {
                        Column { Text(number, color = Edu.Paper,
                            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                            Text(label, color = Edu.Paper.copy(alpha = .75f)) }
                    }
                }
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Edu.Amber),
                    modifier = Modifier.fillMaxWidth()) { Text("إنشاء اختبار جديد", color = Edu.Navy) }
            }
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader("مسارات العمل")
                WorkspaceAction("المكتبة التعليمية", "كتبك ودروسك", EduGlyph.BOOK, Modifier.fillMaxWidth()) {}
                WorkspaceAction("إدارة الفصل", "الحضور والتقييم", EduGlyph.GROUP, Modifier.fillMaxWidth()) {}
            }
        }
    }
}

@Preview(name = "02 — Bookshelf | المناهج", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun LibraryDesignPreview() {
    HTeacherTheme {
        Column(Modifier.fillMaxSize().background(Edu.Canvas).padding(vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            WorkspaceMasthead("LIBRARY", "رفّ المناهج", "أغلفة وملفات قابلة للبحث", EduGlyph.BOOK)
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("ابحث في المكتبة") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChipPill("الرياضيات", true) {}
                FilterChipPill("العلوم", false) {}
                FilterChipPill("+", false) {}
            }
            Text("أغلفة الكتب", color = Edu.Navy, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 20.dp))
            Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                LibraryBookCover("رياضيات", "الصف الرابع", 0, Modifier.weight(1f)) {}
                LibraryBookCover("علوم", "الصف الثالث", 1, Modifier.weight(1f)) {}
            }
            PrimaryButton("إضافة ملف PDF", Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                icon = EduGlyph.PLUS) {}
        }
    }
}

@Preview(name = "03 — Exam paper | الاختبارات", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun ExamDesignPreview() {
    HTeacherTheme {
        Column(Modifier.fillMaxSize().background(Edu.Canvas).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            WorkspaceMasthead("ASSESSMENT STUDIO", "استوديو الاختبارات", "المحرر ومعاينة الطباعة", EduGlyph.EXAM)
            WorkflowSteps(2, listOf("المصدر", "التصميم", "المعاينة")) {}
            Column(Modifier.fillMaxWidth().background(Edu.Paper, RoundedCornerShape(8.dp)).padding(23.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp)) {
                Text("وزارة التعليم • نموذج اختبار", color = Edu.Muted)
                HorizontalDivider(color = Edu.Navy)
                Text("اختبار الرياضيات", color = Edu.Navy,
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("اسم الطالب: ................................", color = Edu.Navy)
                HorizontalDivider()
                for (q in listOf("عرّف خاصية الإبدال في الجمع.", "ما ناتج ٤ × ٦؟", "اختر الإجابة الصحيحة.")) {
                    Text(q, color = Edu.Navy)
                    Text("................................................................",
                        color = Edu.Muted, style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider(color = Edu.Line)
                }
            }
        }
    }
}

@Preview(name = "04 — Journal | الإنجاز", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun PortfolioDesignPreview() {
    HTeacherTheme {
        Column(Modifier.fillMaxSize().background(Edu.Canvas).padding(19.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            WorkspaceMasthead("PROFESSIONAL JOURNAL", "ملف الإنجاز", "مسيرتك المهنية", EduGlyph.FOLDER)
            Row(Modifier.fillMaxWidth().background(Edu.Paper,RoundedCornerShape(20.dp)).padding(20.dp)) {
                Text("١٢ إنجاز",Modifier.weight(1f),fontWeight = FontWeight.Bold,color = Edu.Navy)
                Text("٨ مرفقات",color = Edu.Teal)
            }
            SectionHeader("الخط الزمني")
            TimelineEntry("شهادة تدريب", "التطوير المهني") {
                Text("برنامج تحسين التعليم",color = Edu.Muted)
                SecondaryButton("المرفقات") {}
            }
            TimelineEntry("إنجاز طلابي", "الأنشطة") {
                Text("مبادرة القراءة",color = Edu.Muted)
            }
        }
    }
}

@Preview(name = "05 — Roster | أدوات المعلم", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun ClassroomDesignPreview() {
    HTeacherTheme {
        Column(Modifier.fillMaxSize().background(Edu.Canvas).padding(19.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            WorkspaceMasthead("CLASSROOM", "إدارة الفصل", "قائمة الطلاب والحضور", EduGlyph.GROUP)
            Column(Modifier.fillMaxWidth().background(Edu.Paper,RoundedCornerShape(22.dp)).padding(20.dp)) {
                Text("حضور اليوم",color=Edu.Muted)
                Text("٢٤ / ٢٨",color=Edu.Navy,style=MaterialTheme.typography.headlineLarge,
                    fontWeight=FontWeight.Black)
                LinearProgressIndicator(progress={24f/28f},modifier=Modifier.fillMaxWidth(),
                    color=Edu.Teal)
            }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                FilterChipPill("الطلاب",true) {}
                FilterChipPill("الحضور",false) {}
                FilterChipPill("الدرجات",false) {}
            }
            SectionHeader("كشف الفصل")
            for (name in listOf("أحمد محمد", "عبدالله سعيد", "فهد علي")) {
                Row(Modifier.fillMaxWidth().background(Edu.Paper,RoundedCornerShape(12.dp))
                    .padding(15.dp),verticalAlignment = Alignment.CenterVertically) {
                    Glyph(EduGlyph.GROUP,Modifier.size(20.dp),Edu.Teal)
                    Spacer(Modifier.width(12.dp))
                    Text(name,color = Edu.Navy,modifier=Modifier.weight(1f))
                    Text("عرض",color=Edu.Blue)
                }
            }
        }
    }
}

@Preview(name = "06 — Immersive reader | قارئ PDF", showBackground = true, widthDp = 390, heightDp = 790)
@Composable
fun PdfDesignPreview() {
    HTeacherTheme {
        Column(Modifier.fillMaxSize().background(Color(0xFF141F30)),
            verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth().background(Color(0xFF222F42)).padding(20.dp)) {
                Text("المكتبة",Modifier.weight(1f),color=Color.White)
                Text("قارئ PDF",color=Color.White)
            }
            Column(Modifier.weight(1f).padding(15.dp).background(Color.White)
                .padding(25.dp),verticalArrangement=Arrangement.spacedBy(22.dp)) {
                Text("عنوان الفصل",color=Edu.Navy,fontWeight=FontWeight.Bold)
                repeat(8){HorizontalDivider(color=Edu.Line,thickness=3.dp)}
            }
            Row(Modifier.fillMaxWidth().background(Color(0xFF222F42)).padding(20.dp),
                horizontalArrangement=Arrangement.SpaceBetween) {
                Text("السابق",color=Color.White)
                Text("٢ / ١٢",color=Edu.Amber)
                Text("التالي",color=Color.White)
            }
        }
    }
}
