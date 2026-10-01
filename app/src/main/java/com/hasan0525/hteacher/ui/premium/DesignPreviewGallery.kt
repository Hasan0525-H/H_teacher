package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.hasan0525.hteacher.ui.theme.HTeacherTheme

// Independent design-system previews are safe in Android Studio without app data.
@Preview(name="01 | Dashboard",showBackground=true,widthDp=390)
@Composable fun HomeDesignPreview(){
    HTeacherTheme {
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
            AppTopBar("المعلم H","يومك الدراسي")
            AppCard {SectionHeader("ملخص الفصل");Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                StatCard("طلاب","٢٨",EduGlyph.GROUP,Modifier.weight(1f))
                StatCard("كتب","٤",EduGlyph.BOOK,Modifier.weight(1f))
            }}
            FeatureCard("إنشاء اختبار","إعداد الأسئلة وتصدير النموذج",EduGlyph.EXAM,Modifier.fillMaxWidth()){}
        }
    }
}
@Preview(name="02 | Library",showBackground=true,widthDp=390)
@Composable fun LibraryDesignPreview(){
    HTeacherTheme {
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
            AppTopBar("مكتبة المناهج","مصادر تعليمية")
            SectionHeader("ملفاتك",action="+ إضافة",onAction={})
            FeatureCard("رياضيات • الصف الرابع","كتاب PDF قابل للقراءة دون اتصال",
                EduGlyph.BOOK,Modifier.fillMaxWidth()){}
            EmptyState("المكتبة","أضف كتابًا لإنشاء وحدات جديدة",EduGlyph.DOC)
        }
    }
}
@Preview(name="03 | Exam studio",showBackground=true,widthDp=390)
@Composable fun ExamDesignPreview(){
    HTeacherTheme{
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            AppTopBar("استوديو الاختبارات","بناء اختبار")
            SectionHeader("اختر الإعداد")
            AppCard {
                FormField("اسم الاختبار","اختبار الفصل الأول",{},Modifier.fillMaxWidth())
                SectionHeader("المرحلة","٢ من ٣")
                PrimaryButton("إنشاء الاختبار",Modifier.fillMaxWidth(),icon=EduGlyph.EXAM){}
            }
        }
    }
}
@Preview(name="04 | Portfolio",showBackground=true,widthDp=390)
@Composable fun PortfolioDesignPreview(){
    HTeacherTheme{
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            AppTopBar("ملف الإنجاز","شواهدك المهنية")
            StatCard("إنجازات","١٢",EduGlyph.FOLDER,Modifier.fillMaxWidth(),Edu.Teal)
            SectionHeader("أحدث الإنجازات")
            FeatureCard("شهادة تدريب","التطوير المهني",EduGlyph.CHECK,Modifier.fillMaxWidth(),Edu.Teal){}
        }
    }
}
@Preview(name="05 | Classroom",showBackground=true,widthDp=390)
@Composable fun ClassroomDesignPreview(){
    HTeacherTheme{
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            AppTopBar("إدارة الفصل","الطلاب والحضور")
            Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                StatCard("طلاب","٢٨",EduGlyph.GROUP,Modifier.weight(1f))
                StatCard("الحضور","٢٤",EduGlyph.DATE,Modifier.weight(1f),Edu.Teal)
            }
            FeatureCard("درجات الطلاب","أضف التقييمات",EduGlyph.CHART,Modifier.fillMaxWidth()){}
        }
    }
}
@Preview(name="06 | Reader",showBackground=true,widthDp=390)
@Composable fun PdfDesignPreview(){
    HTeacherTheme{
        Column(Modifier.background(Edu.Canvas).padding(14.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            AppTopBar("كتاب الطالب","قارئ PDF")
            EmptyState("صفحة الكتاب","منطقة القراءة",EduGlyph.PDF)
            Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                SecondaryButton("السابق",Modifier.weight(1f)){}
                PrimaryButton("التالي",Modifier.weight(1f)){}
            }
        }
    }
}
