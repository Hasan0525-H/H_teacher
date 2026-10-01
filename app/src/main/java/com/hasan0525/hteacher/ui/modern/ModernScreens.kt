package com.hasan0525.hteacher.ui.modern

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import android.provider.OpenableColumns
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
private val Line = Color(0xFFE5EAE7)

enum class HIcon { HOME, BOOK, EXAM, FOLDER, PEOPLE, CHART, TOOLS, PLUS, ARROW, PDF, SETTINGS, SPARK }

@Composable
fun HIconView(icon: HIcon, modifier: Modifier = Modifier, tint: Color = Brand) {
    val glyph = when(icon) {
        HIcon.HOME -> "⌂"
        HIcon.BOOK -> "▤"
        HIcon.EXAM -> "▣"
        HIcon.FOLDER -> "□"
        HIcon.PEOPLE -> "♙"
        HIcon.CHART -> "⌁"
        HIcon.TOOLS -> "⚙"
        HIcon.PLUS -> "+"
        HIcon.ARROW -> "›"
        HIcon.PDF -> "▥"
        HIcon.SETTINGS -> "⊙"
        HIcon.SPARK -> "✦"
    }
    Text(glyph, color=tint, fontWeight=FontWeight.Bold, style=MaterialTheme.typography.titleLarge, modifier=modifier.size(26.dp), textAlign=androidx.compose.ui.text.style.TextAlign.Center)
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

@Composable private fun RowScope.HStat(label:String,value:Int,icon:String) {
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
fun ModernCurriculum(
    onBack: () -> Unit,
    onOpenPdf: (String, String) -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as HTeacherApplication
    val vm: CurriculumViewModel = viewModel(factory = CurriculumViewModelFactory(application))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddSubject by remember { mutableStateOf(false) }
    var showAddGrade by remember { mutableStateOf(false) }
    var pendingPdfUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingPdfTitle by rememberSaveable { mutableStateOf("") }

    // The active screen owns its own launcher; the legacy CurriculumRoute is not used by MainActivity.
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) {
            scope.launch { snackbar.showSnackbar("أُلغِي اختيار الملف") }
        } else {
            pendingPdfUri = uri.toString()
            pendingPdfTitle = runCatching {
                context.contentResolver.query(
                    uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor ->
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
                }
            }.getOrNull().orEmpty().ifBlank { "منهج.pdf" }
        }
    }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbar.showSnackbar(message)
            vm.clearMessage()
        }
    }

    Scaffold(
        containerColor = CanvasBg,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = { ModernNav("curricula", onNavigate) }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HTitle("مكتبة المناهج", "موادك وملفاتك في مساحة واحدة", onBack) }
            item {
                Text("المادة", color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.subjects, key = { it.id }) { subject ->
                        HChip(subject.name, state.selectedSubjectId == subject.id) { vm.selectSubject(subject.id) }
                    }
                    item { HChip("+ مادة", false) { showAddSubject = true } }
                }
            }
            item {
                Text("الصف", color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.grades, key = { it.id }) { grade ->
                        HChip(grade.name, state.selectedGradeId == grade.id) { vm.selectGrade(grade.id) }
                    }
                    item { HChip("+ صف", false) { showAddGrade = true } }
                }
            }
            item {
                // This control ALWAYS opens the picker; metadata is requested afterwards.
                HSurface(
                    Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                    onClick = { if (!state.isImporting) picker.launch(arrayOf("application/pdf")) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = BrandSoft) {
                            HIconView(HIcon.PDF, Modifier.padding(14.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("إضافة كتاب أو منهج", fontWeight = FontWeight.Bold, color = Ink)
                            Text(
                                if (state.isImporting) "جارٍ التحقق من الملف وحفظه..." else "اختر PDF من جهازك",
                                color = Muted
                            )
                        }
                        HIconView(HIcon.PLUS, tint = Brand)
                    }
                }
                if (state.isImporting) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        color = Brand
                    )
                }
            }
            item {
                Text(
                    "مكتبتك",
                    color = Ink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            if (state.curricula.isEmpty()) {
                item {
                    HSurface(Modifier.padding(horizontal = 18.dp).fillMaxWidth()) {
                        Text("المكتبة فارغة", fontWeight = FontWeight.Bold, color = Ink)
                        Text("أضف ملف PDF للبدء", color = Muted, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
            items(state.curricula, key = { it.id }) { curriculum ->
                HSurface(
                    Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                    onClick = {
                        curriculum.localFileUri?.takeIf(String::isNotBlank)?.let {
                            onOpenPdf(it, curriculum.title)
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(16.dp), color = BrandSoft) {
                            HIconView(HIcon.BOOK, Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                curriculum.title, fontWeight = FontWeight.Bold, color = Ink,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text("متاح دون إنترنت", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        HIconView(HIcon.ARROW, tint = Muted)
                    }
                }
            }
        }
    }

    pendingPdfUri?.let { uriText ->
        HImportPdfDialog(
            fileName = pendingPdfTitle,
            subjects = state.subjects.map { it.id to it.name },
            grades = state.grades.map { it.id to it.name },
            initialSubjectId = state.selectedSubjectId,
            initialGradeId = state.selectedGradeId,
            isImporting = state.isImporting,
            onDismiss = { pendingPdfUri = null },
            onImport = { subjectId, gradeId, newSubject, newGrade ->
                vm.importPdfWithMetadata(
                    uri = Uri.parse(uriText),
                    requestedSubjectId = subjectId,
                    requestedGradeId = gradeId,
                    newSubjectName = newSubject,
                    newGradeName = newGrade
                )
                pendingPdfUri = null
            }
        )
    }
    if (showAddSubject) {
        HTextDialog("مادة جديدة", "اسم المادة",
            onDismiss = { showAddSubject = false },
            onSave = { vm.addSubject(it); showAddSubject = false })
    }
    if (showAddGrade) {
        HTextDialog("صف جديد", "اسم الصف",
            onDismiss = { showAddGrade = false },
            onSave = { vm.addGrade(it); showAddGrade = false })
    }
}

@Composable
private fun HImportPdfDialog(
    fileName: String,
    subjects: List<Pair<Long, String>>,
    grades: List<Pair<Long, String>>,
    initialSubjectId: Long?,
    initialGradeId: Long?,
    isImporting: Boolean,
    onDismiss: () -> Unit,
    onImport: (Long?, Long?, String, String) -> Unit
) {
    var subjectId by remember(fileName) { mutableStateOf(initialSubjectId) }
    var gradeId by remember(fileName) { mutableStateOf(initialGradeId) }
    var newSubject by remember(fileName) { mutableStateOf("") }
    var newGrade by remember(fileName) { mutableStateOf("") }
    val effectiveSubjectId = subjectId?.takeIf { id -> subjects.any { it.first == id } }
        ?: subjects.firstOrNull()?.first
    val effectiveGradeId = gradeId?.takeIf { id -> grades.any { it.first == id } }
        ?: grades.firstOrNull()?.first

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("حفظ المنهج") },
        text = {
            Column(
                Modifier.heightIn(max = 470.dp).verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(fileName, style = MaterialTheme.typography.titleMedium)
                Text("اختر مادة أو اكتب مادة جديدة")
                subjects.forEach { (id, name) ->
                    HChip(name, newSubject.isBlank() && effectiveSubjectId == id) {
                        subjectId = id
                        newSubject = ""
                    }
                }
                HField("اسم مادة جديدة", newSubject, { newSubject = it })
                Text("اختر صفًا أو اكتب صفًا جديدًا")
                grades.forEach { (id, name) ->
                    HChip(name, newGrade.isBlank() && effectiveGradeId == id) {
                        gradeId = id
                        newGrade = ""
                    }
                }
                HField("اسم صف جديد", newGrade, { newGrade = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isImporting &&
                    (newSubject.isNotBlank() || effectiveSubjectId != null) &&
                    (newGrade.isNotBlank() || effectiveGradeId != null),
                onClick = {
                    onImport(
                        if (newSubject.isNotBlank()) null else effectiveSubjectId,
                        if (newGrade.isNotBlank()) null else effectiveGradeId,
                        newSubject.trim(),
                        newGrade.trim()
                    )
                }
            ) { Text("حفظ المنهج") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun ModernExam(onBack:()->Unit,onNavigate:(String)->Unit={}) {
    val app=(LocalContext.current.applicationContext as HTeacherApplication)
    val vm:ExamGeneratorViewModel=viewModel(factory=ExamGeneratorViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){u:Uri?->u?.let { vm.exportExam(it, false) }}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("exams",onNavigate)}){pad->
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
                    Button(modifier=Modifier.weight(1f),onClick=vm::generateExam,shape=RoundedCornerShape(18.dp)){Text("إنشاء الاختبار")}
                    OutlinedButton(modifier=Modifier.weight(1f),onClick=vm::generateAiQuestions,shape=RoundedCornerShape(18.dp),enabled=!state.isAiGenerating){Text(if(state.isAiGenerating)"جارٍ..." else "توليد ذكي")}
                }
            }
            state.generatedExam?.let{exam->
                item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(exam.title,fontWeight=FontWeight.Bold,color=Ink,style=MaterialTheme.typography.titleLarge);Text("عدد الأسئلة: "+exam.questions.size,color=Muted);Text("المجموع: "+exam.totalMarks,color=Muted);Spacer(Modifier.height(12.dp));Button(modifier=Modifier.fillMaxWidth(),onClick={launcher.launch(exam.title+".pdf")},shape=RoundedCornerShape(18.dp)){Text("تصدير PDF")}}}
            }
        }
    }
}

@Composable
private fun HField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier){OutlinedTextField(value=value,onValueChange=onChange,label={Text(label)},modifier=modifier,shape=RoundedCornerShape(16.dp),singleLine=true)}

@Composable
fun ModernPortfolio(onBack:()->Unit,onNavigate:(String)->Unit={}) {
    val app=(LocalContext.current.applicationContext as HTeacherApplication)
    val vm:PortfolioViewModel=viewModel(factory=PortfolioViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    var add by remember{mutableStateOf(false)}
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){u:Uri?->u?.let(vm::exportPortfolio)}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("portfolio",onNavigate)}){pad->
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
            item{Button(modifier=Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={add=true},shape=RoundedCornerShape(18.dp)){Text("إضافة إنجاز")}}
            items(state.items,key={it.item.id}){ui->HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Text(ui.item.title,fontWeight=FontWeight.Bold,color=Ink);Text(PortfolioCategory.fromStorage(ui.item.category).label,color=Brand,style=MaterialTheme.typography.bodySmall);if(ui.item.description.isNotBlank())Text(ui.item.description,color=Muted,modifier=Modifier.padding(top=5.dp));if(ui.attachments.isNotEmpty())Text(ui.attachments.size.toString()+" مرفق",color=Muted,modifier=Modifier.padding(top=8.dp));TextButton(onClick={vm.deleteItem(ui.item.id)}){Text("حذف")}}}
            item{OutlinedButton(modifier=Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={launcher.launch("teacher-portfolio.pdf")},enabled=!state.isExporting,shape=RoundedCornerShape(18.dp)){Text(if(state.isExporting)"جارٍ التصدير..." else "تصدير ملف الإنجاز PDF")}}
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
fun ModernTools(onBack:()->Unit,onNavigate:(String)->Unit={}) {
    val app=(LocalContext.current.applicationContext as HTeacherApplication)
    val vm:TeacherToolsViewModel=viewModel(factory=TeacherToolsViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    var addStudent by remember{mutableStateOf(false)}
    Scaffold(containerColor=CanvasBg,bottomBar={ModernNav("tools",onNavigate)}){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(bottom=30.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            item{HTitle("أدوات المعلم","إدارة الطلاب والحضور والدرجات والتقارير",onBack)}
            item{HSurface(Modifier.padding(horizontal=18.dp).fillMaxWidth()){Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){HStat("الطلاب",state.students.size,"PEOPLE");HStat("الحضور",state.attendance.size,"CHART");HStat("الدرجات",state.gradeRecords.size,"EXAM")}}}
            item{LazyRow(Modifier.padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){items(TeacherToolsSection.entries){s->HChip(s.label,state.section==s){vm.selectSection(s)}}}}
            when(state.section){
                TeacherToolsSection.STUDENTS->{
                    item{Button(modifier=Modifier.padding(horizontal=18.dp).fillMaxWidth(),onClick={addStudent=true},shape=RoundedCornerShape(18.dp)){Text("إضافة طالب")}}
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

