package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.hasan0525.hteacher.HTeacherApplication

@Composable
fun HomeDashboard(onNavigate:(String)->Unit) {
    val app=LocalContext.current.applicationContext as HTeacherApplication
    val repo=app.container.teacherRepository
    val files by repo.curricula.collectAsStateWithLifecycle(initialValue=emptyList())
    val students by repo.students.collectAsStateWithLifecycle(initialValue=emptyList())
    val portfolio by repo.portfolioItems.collectAsStateWithLifecycle(initialValue=emptyList())
    val profile by app.container.settingsRepository.settings.collectAsStateWithLifecycle(
        initialValue=com.hasan0525.hteacher.data.repository.AppSettings()
    )
    Scaffold(containerColor=Edu.Canvas,bottomBar={EducationBottomNav("home",onNavigate)}) { pad ->
        LazyColumn(
            Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(bottom=32.dp),
            verticalArrangement=Arrangement.spacedBy(22.dp)
        ) {
            item {
                Column(
                    Modifier.fillMaxWidth().background(Edu.Navy,RoundedCornerShape(bottomStart=30.dp,bottomEnd=30.dp))
                        .padding(start=23.dp,end=23.dp,top=28.dp,bottom=26.dp),
                    verticalArrangement=Arrangement.spacedBy(21.dp)
                ) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("مساحة المعلم",color=Edu.Paper.copy(alpha=.7f),style=MaterialTheme.typography.bodyMedium)
                            Text(profile.teacherName.ifBlank{"مرحبًا بك"},style=MaterialTheme.typography.headlineMedium,
                                fontWeight=FontWeight.Bold,color=Edu.Paper)
                        }
                        Surface(shape=RoundedCornerShape(17.dp),color=Edu.Blue) {
                            Glyph(EduGlyph.SPARK,Modifier.padding(12.dp),Edu.Paper)
                        }
                    }
                    Text("مركز إدارة يومك الدراسي",color=Edu.Paper,style=MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        listOf(Triple("المناهج",files.size, EduGlyph.BOOK),
                            Triple("الطلاب",students.size,EduGlyph.GROUP),
                            Triple("الإنجازات",portfolio.size,EduGlyph.FOLDER)).forEach { (label,value,icon)->
                            Column(Modifier.weight(1f).background(Edu.Paper.copy(alpha=.10f),RoundedCornerShape(14.dp))
                                .padding(13.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                                Glyph(icon,tint=Edu.Amber)
                                Text("$value",fontWeight=FontWeight.Bold,color=Edu.Paper,
                                    style=MaterialTheme.typography.titleLarge)
                                Text(label,color=Edu.Paper.copy(alpha=.85f),style=MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                    SectionHeader("أنجز الآن","المهام الأكثر استخدامًا")
                    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            ActionShortcut("المكتبة","أضف منهجك",EduGlyph.BOOK,Edu.Blue,Modifier.fillMaxWidth()) {
                                onNavigate("curricula")
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            ActionShortcut("اختبار جديد","أنشئ وصدّر",EduGlyph.EXAM,Edu.Teal,Modifier.fillMaxWidth()) {
                                onNavigate("exams")
                            }
                        }
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    SectionHeader("آخر الملفات",action="عرض المكتبة",onAction={onNavigate("curricula")})
                    if(files.isEmpty()) {
                        AppCard(Modifier.fillMaxWidth()) {
                            EmptyState("مكتبتك جاهزة","ابدأ بإضافة أول منهج",EduGlyph.BOOK,"إضافة منهج") {
                                onNavigate("curricula")
                            }
                        }
                    } else {
                        files.take(3).forEach { file ->
                            FeatureCard(file.title,"ملف محفوظ للاستخدام دون إنترنت",EduGlyph.DOC,
                                modifier=Modifier.fillMaxWidth(),accent=Edu.Teal) {
                                onNavigate("curricula")
                            }
                        }
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    SectionHeader("إدارة العمل")
                    FeatureCard("ملف الإنجاز","شواهدك وتقاريرك المهنية",EduGlyph.FOLDER,
                        Modifier.fillMaxWidth(),accent=Edu.Teal){onNavigate("portfolio")}
                    FeatureCard("إدارة الفصل","الطلاب والحضور والدرجات",EduGlyph.GROUP,
                        Modifier.fillMaxWidth(),accent=Edu.Blue){onNavigate("tools")}
                }
            }
        }
    }
}

@Composable
private fun ActionShortcut(
    title:String,subtitle:String,icon:EduGlyph,tint:androidx.compose.ui.graphics.Color,
    modifier:Modifier=Modifier,onClick:()->Unit
) {
    Surface(onClick=onClick,modifier=modifier.height(149.dp),color=Edu.Paper,
        shape=RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.SpaceBetween) {
            Surface(color=tint.copy(alpha=.10f),shape=RoundedCornerShape(13.dp)) {
                Glyph(icon,Modifier.padding(9.dp).size(27.dp),tint)
            }
            Column {
                Text(title,fontWeight=FontWeight.Bold,color=Edu.Navy,
                    style=MaterialTheme.typography.titleMedium)
                Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}
