package com.hasan0525.hteacher.ui.modern

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.QuestionType
import com.hasan0525.hteacher.domain.portfolio.PortfolioCategory
import com.hasan0525.hteacher.domain.student.TeacherToolsSection
import com.hasan0525.hteacher.ui.curriculum.CurriculumViewModel
import com.hasan0525.hteacher.ui.curriculum.CurriculumViewModelFactory
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModel
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModelFactory
import com.hasan0525.hteacher.ui.home.HomeViewModel
import com.hasan0525.hteacher.ui.portfolio.PortfolioViewModel
import com.hasan0525.hteacher.ui.portfolio.PortfolioViewModelFactory
import com.hasan0525.hteacher.ui.tools.TeacherToolsViewModel
import com.hasan0525.hteacher.ui.tools.TeacherToolsViewModelFactory
import java.util.Locale

private val Ink = Color(0xFF17201D)
private val Muted = Color(0xFF697873)
private val CanvasBg = Color(0xFFF7F8F6)
private val Surface = Color.White
private val Brand = Color(0xFF176B5A)
private val BrandSoft = Color(0xFFE4F1EC)
private val Accent = Color(0xFFE6B65A)
private val Line = Color(0xFFE5EAE7)

enum class HIcon { HOME, BOOK, EXAM, FOLDER, PEOPLE, CHART, TOOLS, PLUS, ARROW, PDF, SETTINGS, SPARK }

@Composable
fun HIconView(icon: HIcon, modifier: Modifier = Modifier, tint: Color = Brand) {
    Canvas(modifier.size(26.dp)) {
        val s = size.minDimension
        val w = s * .72f
        val left = (s-w)/2
        val top = (s-w)/2
        val p = Stroke(width=s*.09f, cap=StrokeCap.Round)
        when(icon) {
            HIcon.HOME -> { drawLine(Offset(left,top+s*.42f),Offset(s/2,top),p); drawLine(Offset(s/2,top),Offset(left+w,top+s*.42f),p); drawRoundRect(left+s*.12f,top+s*.38f,w*.76f,w*.58f, s*.08f,s*.08f,tint,style=Stroke(width=s*.09f)); drawLine( s/2,top+s*.96f,s/2,top+s*.62f,p) }
            HIcon.BOOK -> { drawRoundRect(left,top,w*.48f,w,s*.04f,s*.04f,tint,style=p); drawRoundRect(s/2,top,w*.48f,w,s*.04f,s*.04f,tint,style=p); drawLine(s/2,top+s*.08f,s/2,top+s*.92f,p) }
            HIcon.EXAM -> { drawRoundRect(left+s*.12f,top+s*.08f,w*.76f,w*.9f,s*.08f,s*.08f,tint,style=p); drawLine(left+s*.3f,top+s*.32f,left+s*.72f,top+s*.32f,p); drawLine(left+s*.3f,top+s*.52f,left+s*.72f,top+s*.52f,p); drawLine(left+s*.3f,top+s*.72f,left+s*.58f,top+s*.72f,p) }
            HIcon.FOLDER -> { drawRoundRect(left,top+s*.2f,w,w*.72f,s*.08f,s*.08f,tint,style=p); drawLine(left+s*.08f,top+s*.2f,left+s*.3f,top+s*.08f,p); drawLine(left+s*.3f,top+s*.08f,left+s*.52f,top+s*.2f,p) }
            HIcon.PEOPLE -> { drawCircle(s*.38f,s*.34f,s*.13f,tint,style=p); drawCircle(s*.7f,s*.4f,s*.1f,tint,style=p); drawRoundRect(s*.16f,s*.54f,s*.46f,s*.35f,s*.12f,s*.12f,tint,style=p); drawArc(s*.57f,s*.52f,s*.86f,s*.86f,190f,160f,false,p) }
            HIcon.CHART -> { drawLine(left,top+w,left,top,p); drawLine(left,top+w,s*.9f,top+w,p); drawLine(left+s*.1f,top+w*.75f,left+s*.32f,top+w*.55f,p); drawLine(left+s*.32f,top+w*.55f,left+s*.5f,top+w*.66f,p); drawLine(left+s*.5f,top+w*.66f,left+s*.78f,top+w*.25f,p) }
            HIcon.TOOLS -> { drawCircle(s*.34f,s*.36f,s*.18f,tint,style=p); drawLine(s*.47f,s*.49f,s*.84f,s*.86f,p); drawCircle(s*.72f,s*.72f,s*.15f,tint,style=p) }
            HIcon.PLUS -> { drawLine(s*.2f,s/2,s*.8f,s/2,p); drawLine(s/2,s*.2f,s/2,s*.8f,p) }
            HIcon.ARROW -> { drawLine(s*.2f,s/2,s*.8f,s/2,p); drawLine(s*.55f,s*.25f,s*.8f,s/2,p); drawLine(s*.55f,s*.75f,s*.8f,s/2,p) }
            HIcon.PDF -> { drawRoundRect(left+s*.12f,top,w*.72f,w,s*.06f,s*.06f,tint,style=p); drawLine(left+s*.27f,top+s*.38f,left+s*.68f,top+s*.38f,p); drawLine(left+s*.27f,top+s*.58f,left+s*.68f,top+s*.58f,p) }
            HIcon.SETTINGS -> { drawCircle(s/2,s/2,s*.24f,tint,style=p); for(i in 0 until 8){ val a=i*45f; val rad=Math.toRadians(a.toDouble()); val x1=s/2+ s*.32f*kotlin.math.cos(rad).toFloat(); val y1=s/2+s*.32f*kotlin.math.sin(rad).toFloat(); val x2=s/2+s*.43f*kotlin.math.cos(rad).toFloat(); val y2=s/2+s*.43f*kotlin.math.sin(rad).toFloat(); drawLine(x1,y1,x2,y2,p) } }
            HIcon.SPARK -> { drawLine(s*.5f,s*.08f,s*.5f,s*.92f,p); drawLine(s*.08f,s*.5f,s*.92f,s*.5f,p); drawLine(s*.22f,s*.22f,s*.78f,s*.78f,p); drawLine(s*.78f,s*.22f,s*.22f,s*.78f,p) }
        }
    }
}

@Composable
private fun HSurface(modifier: Modifier=Modifier, selected:Boolean=false, onClick:(()->Unit)?=null, content:@Composable ColumnScope.()->Unit) {
    val m=modifier.clip(RoundedCornerShape(26.dp)).then(if(onClick!=null) Modifier.clickable{onClick()} else Modifier)
    Column(m.background(if(selected) BrandSoft else Surface).border(1.dp, if(selected) Brand.copy(.18f) else Line, RoundedCornerShape(26.dp)).padding(18.dp), content=content)
}

@Composable private fun HTitle(title:String, subtitle:String?=null, onBack:(()->Unit)?=null) {
    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=20.dp,vertical=14.dp), verticalAlignment=Alignment.CenterVertically) {
        if(onBack!=null) { HIconView(HIcon.ARROW, Modifier.size(28.dp), Ink); Spacer(Modifier.width(12.dp)) }
        Column(Modifier.weight(1f)) { Text(title,color=Ink,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.headlineSmall); subtitle?.let{Text(it,color=Muted,style=MaterialTheme.typography.bodyMedium)} }
    }
}

@Composable private fun HChip(text:String, selected:Boolean, onClick:()->Unit) {
    Surface(onClick=onClick, shape=RoundedCornerShape(14.dp), color=if(selected) Brand else Surface, border=if(selected)null else androidx.compose.foundation.BorderStroke(1.dp,Line)) {
        Text(text, color=if(selected) Color.White else Ink, fontWeight=if(selected) FontWeight.Bold else FontWeight.Medium, modifier=Modifier.padding(horizontal=15.dp,vertical=10.dp))
    }
}

@Composable
fun ModernHome(onOpen:(String)->Unit) {
    val vm:HomeViewModel=viewModel()
    val state=vm.uiState
    val cards=listOf(
        Triple("curricula","المناهج",HIcon.BOOK),
        Triple("exams","الاختبارات",HIcon.EXAM),
        Triple("portfolio","ملف الإنجاز",HIcon.FOLDER),
        Triple("tools","أدوات المعلم",HIcon.TOOLS)
    )
    Scaffold(containerColor=CanvasBg, bottomBar={ModernNav("home",onOpen)}) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            item { HTitle("المعلم H","مساحة عملك اليومية") }
            item {
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("مساحتك جاهزة",color=Ink,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.headlineSmall)
                            Text("أنجز مهامك التعليمية من مكان واحد",color=Muted,modifier=Modifier.padding(top=6.dp))
                        }
                        Surface(shape=CircleShape,color=BrandSoft){ HIconView(HIcon.SPARK,Modifier.padding(14.dp),Brand) }
                    }
                }
            }
            item { Text("الوصول السريع",fontWeight=FontWeight.Bold,color=Ink,modifier=Modifier.padding(horizontal=20.dp)) }
            item {
                Row(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    cards.take(2).forEach{ (id,t,ic)-> HSurface(Modifier.weight(1f),onClick={onOpen(id)}){Surface(shape=CircleShape,color=BrandSoft){HIconView(ic,Modifier.padding(10.dp))};Spacer(Modifier.height(22.dp));Text(t,color=Ink,fontWeight=FontWeight.Bold)} }
                }
            }
            item {
                Row(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    cards.drop(2).forEach{ (id,t,ic)-> HSurface(Modifier.weight(1f),onClick={onOpen(id)}){Surface(shape=CircleShape,color=BrandSoft){HIconView(ic,Modifier.padding(10.dp))};Spacer(Modifier.height(22.dp));Text(t,color=Ink,fontWeight=FontWeight.Bold)} }
                }
            }
            item {
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()) {
                    Text("ملخص",fontWeight=FontWeight.Bold,color=Ink)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        HStat("المناهج",state.tools.count{it.id.name=="CURRICULA"},"BOOK")
                        HStat("الأدوات",state.tools.size,"TOOLS")
                        HStat("جاهز",state.tools.count{it.status.name=="READY"},"SPARK")
                    }
                }
            }
        }
    }
}

@Composable private fun HStat(label:String,value:Int,icon:String) {
    Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(CanvasBg).padding(14.dp)) {
        Text(value.toString(),style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,color=Brand)
        Text(label,color=Muted,style=MaterialTheme.typography.bodySmall)
    }
}

@Composable private fun ModernNav(selected:String,onOpen:(String)->Unit) {
    NavigationBar(containerColor=Surface,tonalElevation=0.dp) {
        listOf("home" to "الرئيسية","curricula" to "المناهج","exams" to "اختبار","portfolio" to "الإنجاز","tools" to "الأدوات").forEach { (id,label) ->
            val icon=when(id){"home"->HIcon.HOME;"curricula"->HIcon.BOOK;"exams"->HIcon.EXAM;"portfolio"->HIcon.FOLDER;else->HIcon.TOOLS}
            NavigationBarItem(selected=selected==id,onClick={onOpen(id)},icon={HIconView(icon,tint=if(selected==id)Brand else Muted)},label={Text(label)})
        }
    }
}

@Composable
fun ModernCurriculum(onBack:()->Unit,onOpenPdf:(String,String)->Unit) {
    val app=(LocalContext.current.applicationContext as HTeacherApplication)
    val vm:CurriculumViewModel=viewModel(factory=CurriculumViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showAddSubject by remember{mutableStateOf(false)}
    var showAddGrade by remember{mutableStateOf(false)}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){u:Uri?->u?.let(vm::importPdf)}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("curricula"){if(it=="home")onBack()} }) {pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=30.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
            item{HTitle("مكتبة المناهج","موادك وملفاتك في مساحة واحدة",onBack)}
            item{
                Row(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    HChip(if(state.subjects.isEmpty())"إضافة مادة" else state.subjects.firstOrNull{it.id==state.selectedSubjectId}?.name?:"المادة",true){showAddSubject=true}
                    HChip(if(state.grades.isEmpty())"إضافة صف" else state.grades.firstOrNull{it.id==state.selectedGradeId}?.name?:"الصف",false){showAddGrade=true}
                }
            }
            item{
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={if(state.selectedSubjectId!=null&&state.selectedGradeId!=null)picker.launch(arrayOf("application/pdf"))}){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Surface(shape=CircleShape,color=BrandSoft){HIconView(HIcon.PDF,Modifier.padding(14.dp))}
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)){Text("إضافة كتاب أو منهج",fontWeight=FontWeight.Bold,color=Ink);Text(if(state.isImporting)"جارٍ الحفظ..." else "PDF محفوظ داخل الجهاز",color=Muted)}
                        HIconView(HIcon.PLUS, tint=Brand)
                    }
                }
            }
            item{Text("مكتبتك",fontWeight=FontWeight.Bold,color=Ink,modifier=Modifier.padding(horizontal=20.dp))}
            if(state.curricula.isEmpty()) item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text("المكتبة فارغة",fontWeight=FontWeight.Bold,color=Ink);Text("أضف أول ملف PDF للبدء",color=Muted,modifier=Modifier.padding(top=6.dp))}}
            items(state.curricula,key={it.id}){c->
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={onOpenPdf(c.localFileUri,c.title)}){
                    Row(verticalAlignment=Alignment.CenterVertically){Surface(shape=RoundedCornerShape(16.dp),color=BrandSoft){HIconView(HIcon.BOOK,Modifier.padding(12.dp))};Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(c.title,fontWeight=FontWeight.Bold,color=Ink,maxLines=1,overflow=TextOverflow.Ellipsis);Text("مادة ومرحلة محفوظة أوفلاين",color=Muted,style=MaterialTheme.typography.bodySmall)};HIconView(HIcon.ARROW,tint=Muted)}
                }
            }
        }
    }
    if(showAddSubject) HTextDialog("مادة جديدة","اسم المادة"){showAddSubject=false}{vm.addSubject(it)}
    if(showAddGrade) HTextDialog("صف جديد","اسم الصف"){showAddGrade=false}{vm.addGrade(it)}
}

@Composable
fun ModernExam(onBack:()->Unit) {
    val app=LocalApplication.current
    val vm:ExamGeneratorViewModel=viewModel(factory=ExamGeneratorViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){u:Uri?->u?.let { vm.exportExam(it, false) }}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("exams"){if(it=="home")onBack()}}){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=30.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
            item{HTitle("استوديو الاختبارات","صمّم اختبارك ثم صدّره PDF",onBack)}
            item{
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){
                    Text("المصدر",fontWeight=FontWeight.Bold,color=Ink)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(state.subjects){s->HChip(s.name,s.id==state.selectedSubjectId){vm.selectSubject(s.id)}}}
                    Spacer(Modifier.height(10.dp))
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(state.curricula){c->HChip(c.title,c.id==state.selectedCurriculumId){vm.selectCurriculum(c.id)}}}
                }
            }
            item{
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){
                    Text("مواصفات الاختبار",fontWeight=FontWeight.Bold,color=Ink)
                    Spacer(Modifier.height(12.dp))
                    HField("العنوان",state.title,vm::updateTitle)
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){HField("الأسئلة",state.questionCount,vm::updateQuestionCount,Modifier.weight(1f));HField("الدرجات",state.totalMarks,vm::updateTotalMarks,Modifier.weight(1f))}
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(Difficulty.entries){d->HChip(d.label,d==state.selectedDifficulty){vm.selectDifficulty(d)}}}
                }
            }
            item{
                Row(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Button(Modifier.weight(1f),onClick=vm::generateExam,shape=RoundedCornerShape(18.dp)){Text("إنشاء الاختبار")}
                    OutlinedButton(Modifier.weight(1f),onClick=vm::generateAiQuestions,shape=RoundedCornerShape(18.dp),enabled=!state.isAiGenerating){Text(if(state.isAiGenerating)"جارٍ..." else "توليد ذكي")}
                }
            }
            state.generatedExam?.let{exam->
                item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(exam.title,fontWeight=FontWeight.Bold,color=Ink,style=MaterialTheme.typography.titleLarge);Text("عدد الأسئلة: "+exam.questions.size,color=Muted);Text("المجموع: "+exam.totalMarks,color=Muted);Spacer(Modifier.height(12.dp));Button(Modifier.fillMaxWidth(),onClick={launcher.launch(exam.title+".pdf")},shape=RoundedCornerShape(18.dp)){Text("تصدير PDF")}}}
            }
        }
    }
}

@Composable
private fun HField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier){OutlinedTextField(value=value,onValueChange=onChange,label={Text(label)},modifier=modifier,shape=RoundedCornerShape(16.dp),singleLine=true)}

@Composable
fun ModernPortfolio(onBack:()->Unit) {
    val app=LocalApplication.current
    val vm:PortfolioViewModel=viewModel(factory=PortfolioViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    var add by remember{mutableStateOf(false)}
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){u:Uri?->u?.let(vm::exportPortfolio)}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("portfolio"){if(it=="home")onBack()}}){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=30.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
            item{HTitle("ملف الإنجاز","حوّل أعمالك إلى سجل مهني مرتب",onBack)}
            item{
                HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){
                    Text(state.profile.teacherName.ifBlank{"ملف المعلم"},fontWeight=FontWeight.Bold,color=Ink,style=MaterialTheme.typography.headlineSmall)
                    Text(state.profile.jobTitle.ifBlank{"بياناتك المهنية"},color=Muted)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){HStat("العناصر",state.allItems.size,"FOLDER");HStat("المرفقات",state.allItems.sumOf{it.attachments.size},"PDF")}
                }
            }
            item{LazyRow(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){item{HChip("الكل",state.selectedCategory==null){vm.selectCategory(null)}};items(PortfolioCategory.entries){c->HChip(c.label,state.selectedCategory==c){vm.selectCategory(c)}}}}
            item{Button(Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={add=true},shape=RoundedCornerShape(18.dp)){Text("إضافة إنجاز")}}
            items(state.items,key={it.item.id}){ui->HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(ui.item.title,fontWeight=FontWeight.Bold,color=Ink);Text(PortfolioCategory.fromStorage(ui.item.category).label,color=Brand,style=MaterialTheme.typography.bodySmall);if(ui.item.description.isNotBlank())Text(ui.item.description,color=Muted,modifier=Modifier.padding(top=5.dp));if(ui.attachments.isNotEmpty())Text(ui.attachments.size.toString()+" مرفق",color=Muted,modifier=Modifier.padding(top=8.dp));TextButton(onClick={vm.deleteItem(ui.item.id)}){Text("حذف")}}}
            item{OutlinedButton(Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={launcher.launch("teacher-portfolio.pdf")},enabled=!state.isExporting,shape=RoundedCornerShape(18.dp)){Text(if(state.isExporting)"جارٍ التصدير..." else "تصدير ملف الإنجاز PDF")}}
        }
    }
    if(add) HPortfolioDialog(onDismiss={add=false}){c,t,d->add=false;vm.addItem(c,t,d)}
}

@Composable
private fun HPortfolioDialog(onDismiss:()->Unit,onSave:(PortfolioCategory,String,String)->Unit){
    var title by remember{mutableStateOf("")};var desc by remember{mutableStateOf("")};var cat by remember{mutableStateOf(PortfolioCategory.COURSES)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("إضافة إنجاز")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){HField("العنوان",title,{title=it});HField("الوصف",desc,{desc=it});LazyColumn(Modifier.heightIn(max=180.dp)){items(PortfolioCategory.entries){c->HChip(c.label,c==cat){cat=c}}}}},confirmButton={TextButton(enabled=title.isNotBlank(),onClick={onSave(cat,title,desc)}){Text("حفظ")}},dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
fun ModernTools(onBack:()->Unit) {
    val app=LocalApplication.current
    val vm:TeacherToolsViewModel=viewModel(factory=TeacherToolsViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    var addStudent by remember{mutableStateOf(false)}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("tools"){if(it=="home")onBack()}}){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=30.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            item{HTitle("أدوات المعلم","إدارة الطلاب والحضور والدرجات والتقارير",onBack)}
            item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){HStat("الطلاب",state.students.size,"PEOPLE");HStat("الحضور",state.attendance.size,"CHART");HStat("الدرجات",state.gradeRecords.size,"EXAM")}}}
            item{LazyRow(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){items(TeacherToolsSection.entries){s->HChip(s.label,state.section==s){vm.selectSection(s)}}}}
            when(state.section){
                TeacherToolsSection.STUDENTS->{
                    item{Button(Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={addStudent=true},shape=RoundedCornerShape(18.dp)){Text("إضافة طالب")}}
                    items(state.students,key={it.id}){student->HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(student.name,fontWeight=FontWeight.Bold,color=Ink);Text(student.studentNumber,color=Muted)};TextButton(onClick={vm.deleteStudent(student.id)}){Text("حذف")}}}}
                }
                TeacherToolsSection.ATTENDANCE->{items(state.students,key={it.id}){s->HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(s.name,fontWeight=FontWeight.Bold,color=Ink);Text("سجل حضور اليوم",color=Muted);}}}
                TeacherToolsSection.GRADES->{items(state.students,key={it.id}){s->HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(s.name,fontWeight=FontWeight.Bold,color=Ink);val r=state.gradeRecords.filter{it.studentId==s.id};Text("التقييمات: "+r.size,color=Muted)}}}
                TeacherToolsSection.REPORTS->{item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text("تقرير شامل",fontWeight=FontWeight.Bold,color=Ink);Text("الطلاب: "+state.students.size,color=Muted);Text("الحضور: "+state.attendance.size,color=Muted);Text("الدرجات: "+state.gradeRecords.size,color=Muted)}}}
            }
        }
    }
    if(addStudent) HStudentDialog(state.grades,{addStudent=false}){n,num,g->addStudent=false;vm.addStudent(n,num,g)}
}

@Composable private fun HStudentDialog(grades:List<com.hasan0525.hteacher.data.local.entity.GradeEntity>,onDismiss:()->Unit,onSave:(String,String,Long?)->Unit){
    var n by remember{mutableStateOf("")};var num by remember{mutableStateOf("")};var g by remember{mutableStateOf<Long?>(grades.firstOrNull()?.id)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("إضافة طالب")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){HField("اسم الطالب",n,{n=it});HField("الرقم",num,{num=it});LazyRow(horizontalArrangement=Arrangement.spacedBy(6.dp)){items(grades){x->HChip(x.name,x.id==g){g=x.id}}}}},confirmButton={TextButton(enabled=n.isNotBlank(),onClick={onSave(n,num,g)}){Text("حفظ")}},dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable private fun HTextDialog(title:String,label:String,onDismiss:()->Unit,onSave:(String)->Unit){
    var value by remember{mutableStateOf("")}
    AlertDialog(onDismissRequest=onDismiss,title={Text(title)},text={HField(label,value,{value=it})},confirmButton={TextButton(enabled=value.isNotBlank(),onClick={onSave(value)}){Text("حفظ")}},dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

