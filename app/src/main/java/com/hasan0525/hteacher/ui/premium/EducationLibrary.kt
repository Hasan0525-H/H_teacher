package com.hasan0525.hteacher.ui.premium

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.CurriculumUnitEntity
import com.hasan0525.hteacher.ui.curriculum.CurriculumViewModel
import com.hasan0525.hteacher.ui.curriculum.CurriculumViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationLibrary(onBack:()->Unit,onNavigate:(String)->Unit,onOpenPdf:(String,String)->Unit) {
    val context=LocalContext.current
    val app=context.applicationContext as HTeacherApplication
    val vm:CurriculumViewModel=viewModel(factory=CurriculumViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar=remember{SnackbarHostState()}
    val scope=rememberCoroutineScope()
    var search by rememberSaveable { mutableStateOf("") }
    var pendingUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingTitle by rememberSaveable { mutableStateOf("") }
    var addSubject by remember{mutableStateOf(false)}
    var addGrade by remember{mutableStateOf(false)}
    var openCourse by rememberSaveable{mutableStateOf<Long?>(null)}
    var addUnit by remember{mutableStateOf<Long?>(null)}
    var addLesson by remember{mutableStateOf<Long?>(null)}
    var renameUnit by remember{mutableStateOf<CurriculumUnitEntity?>(null)}
    var deleteUnit by remember{mutableStateOf<CurriculumUnitEntity?>(null)}
    var renameLesson by remember{mutableStateOf<com.hasan0525.hteacher.data.local.entity.LessonEntity?>(null)}
    var deleteLesson by remember{mutableStateOf<com.hasan0525.hteacher.data.local.entity.LessonEntity?>(null)}
    var deleteCourse by remember{mutableStateOf<CurriculumEntity?>(null)}
    var renameCourse by remember{mutableStateOf<CurriculumEntity?>(null)}
    var manageSubject by remember{mutableStateOf<Long?>(null)}
    var manageGrade by remember{mutableStateOf<Long?>(null)}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){ uri:Uri? ->
        if(uri==null) scope.launch{snackbar.showSnackbar("أُلغِي اختيار الملف")} else {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            pendingUri=uri.toString()
            pendingTitle=runCatching {
                context.contentResolver.query(uri,arrayOf(OpenableColumns.DISPLAY_NAME),null,null,null)?.use{ cursor ->
                    val col=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if(col>=0&&cursor.moveToFirst())cursor.getString(col) else null
                }
            }.getOrNull().orEmpty().ifBlank{"ملف PDF"}
        }
    }
    LaunchedEffect(state.message) {
        state.message?.let{snackbar.showSnackbar(it);vm.clearMessage()}
    }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val displayed = state.curricula.filter { it.title.contains(search, ignoreCase = true) }

    Scaffold(
        containerColor = Edu.Canvas,
        topBar = { AppTopBar("المكتبة", onBack = onBack) },
        bottomBar = { EducationBottomNav("curricula", onNavigate) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (!state.isImporting) picker.launch(arrayOf("application/pdf")) },
                containerColor = Edu.Navy,
                contentColor = Edu.Paper,
                shape = RoundedCornerShape(21.dp),
                icon = { Glyph(EduGlyph.PLUS, Modifier.size(23.dp), Edu.Paper) },
                text = { Text(if (state.isImporting) "جارٍ الحفظ" else "إضافة PDF", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("بحث") },
                        leadingIcon = { Glyph(EduGlyph.SEARCH, Modifier.size(21.dp), Edu.Muted) },
                        shape = RoundedCornerShape(19.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Edu.Paper, focusedContainerColor = Edu.Paper,
                            focusedBorderColor = Edu.Teal, unfocusedBorderColor = Edu.Line
                        )
                    )
                    if (state.isImporting) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Edu.Teal)
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${displayed.size} كتاب", Modifier.weight(1f),
                        color = Edu.Navy, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showFilters = !showFilters }) {
                        Text(if (showFilters) "إخفاء الفلاتر" else "تصفية", color = Edu.Teal)
                    }
                }
            }
            if (showFilters) {
                item {
                    Column(Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("المواد", Modifier.weight(1f), color = Edu.Navy)
                            TextButton(onClick = { addSubject = true }) { Text("+ مادة") }
                            state.subjects.firstOrNull { it.id == state.selectedSubjectId }?.let { selected ->
                                TextButton(onClick = { manageSubject = selected.id }) { Text("تعديل") }
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.subjects, key = { it.id }) { subject ->
                                FilterChipPill(subject.name, subject.id == state.selectedSubjectId) {
                                    vm.selectSubject(subject.id)
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("الصفوف", Modifier.weight(1f), color = Edu.Navy)
                            TextButton(onClick = { addGrade = true }) { Text("+ صف") }
                            state.grades.firstOrNull { it.id == state.selectedGradeId }?.let { selected ->
                                TextButton(onClick = { manageGrade = selected.id }) { Text("تعديل") }
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.grades, key = { it.id }) { grade ->
                                FilterChipPill(grade.name, grade.id == state.selectedGradeId) {
                                    vm.selectGrade(grade.id)
                                }
                            }
                        }
                    }
                }
            }
            if (displayed.isEmpty()) {
                item {
                    Column(Modifier.padding(horizontal = 20.dp).fillMaxWidth().background(Edu.Paper,
                        RoundedCornerShape(25.dp))) {
                        EmptyState(
                            "لا توجد كتب",
                            "اضغط إضافة PDF",
                            EduGlyph.BOOK,
                            "اختيار PDF"
                        ) { picker.launch(arrayOf("application/pdf")) }
                    }
                }
            } else {
                items(displayed.chunked(2)) { pair ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        pair.forEach { course ->
                            LibraryBookCover(
                                title = course.title,
                                subtitle = "",
                                index = displayed.indexOfFirst { it.id == course.id },
                                modifier = Modifier.weight(1f)
                            ) { openCourse = course.id }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Course details are a separate surface, rather than nesting another stack in every library item.
    openCourse?.let { id ->
        state.curricula.firstOrNull { it.id == id }?.let { course ->
            ModalBottomSheet(
                onDismissRequest = { openCourse = null },
                containerColor = Edu.Canvas,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    Modifier.fillMaxWidth().navigationBarsPadding()
                        .heightIn(max = 680.dp).verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(course.title, color = Edu.Navy,
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    PrimaryButton(
                        "فتح قارئ الكتاب",
                        Modifier.fillMaxWidth(),
                        enabled = !course.localFileUri.isNullOrBlank(),
                        icon = EduGlyph.PDF
                    ) { course.localFileUri?.let { openCourse = null; onOpenPdf(it, course.title) } }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SecondaryButton("تعديل الاسم", Modifier.weight(1f)) {
                            renameCourse = course; openCourse = null
                        }
                        SecondaryButton("حذف الكتاب", Modifier.weight(1f)) {
                            deleteCourse = course; openCourse = null
                        }
                    }
                    HorizontalDivider(color = Edu.Line)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("الوحدات والدروس", color = Edu.Navy,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { addUnit = course.id; openCourse = null }) { Text("+ وحدة") }
                    }
                    val units = state.units.filter { it.curriculumId == course.id }
                    if (units.isEmpty()) {
                        EmptyState("لا توجد وحدات", "", EduGlyph.FOLDER)
                    }
                    units.forEachIndexed { index, unit ->
                        Column(
                            Modifier.fillMaxWidth().background(Edu.Paper, RoundedCornerShape(20.dp))
                                .padding(15.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text("الوحدة ${index + 1}", color = Edu.Teal,
                                style = MaterialTheme.typography.labelMedium)
                            Text(unit.title, color = Edu.Navy,
                                fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Row {
                                TextButton(onClick = { addLesson = unit.id; openCourse = null }) { Text("+ درس") }
                                TextButton(onClick = { renameUnit = unit; openCourse = null }) { Text("تعديل") }
                                TextButton(onClick = { deleteUnit = unit; openCourse = null }) {
                                    Text("حذف", color = Edu.Error)
                                }
                            }
                            state.lessons.filter { it.unitId == unit.id }.forEach { lesson ->
                                HorizontalDivider(color = Edu.Line)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Glyph(EduGlyph.DOC, Modifier.size(19.dp), Edu.Teal)
                                    Spacer(Modifier.width(8.dp))
                                    Text(lesson.title, Modifier.weight(1f), color = Edu.Navy)
                                    TextButton(onClick = { renameLesson = lesson; openCourse = null }) {
                                        Text("تعديل")
                                    }
                                    TextButton(onClick = { deleteLesson = lesson; openCourse = null }) {
                                        Text("حذف", color = Edu.Error)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }

    if(pendingUri!=null) PdfMetadataDialog(
        name=pendingTitle,subjects=state.subjects.map{it.id to it.name},grades=state.grades.map{it.id to it.name},
        subjectId=state.selectedSubjectId,gradeId=state.selectedGradeId,
        onDismiss={pendingUri=null},onSave={sid,gid,newS,newG->
            vm.importPdfWithMetadata(Uri.parse(pendingUri),sid,gid,newS,newG)
            pendingUri=null
        }
    )
    if(addSubject) EducationTextDialog("إضافة مادة","اسم المادة",onDismiss={addSubject=false}) {
        vm.addSubject(it);addSubject=false
    }
    if(addGrade) EducationTextDialog("إضافة صف","اسم الصف",onDismiss={addGrade=false}) {
        vm.addGrade(it);addGrade=false
    }
    manageSubject?.let{id->
        val entry=state.subjects.firstOrNull{it.id==id}
        if(entry!=null) EducationManageDialog("إدارة المادة",entry.name,{manageSubject=null},
            onRename={vm.renameSubject(id,it);manageSubject=null},
            onDelete={vm.deleteSubject(id);manageSubject=null})
    }
    manageGrade?.let{id->
        val entry=state.grades.firstOrNull{it.id==id}
        if(entry!=null) EducationManageDialog("إدارة الصف",entry.name,{manageGrade=null},
            onRename={vm.renameGrade(id,it);manageGrade=null},
            onDelete={vm.deleteGrade(id);manageGrade=null})
    }
    addUnit?.let{id->EducationTextDialog("وحدة جديدة","اسم الوحدة",{addUnit=null}){
        vm.addUnit(id,it);addUnit=null
    }}
    addLesson?.let{id->EducationLessonDialog(onDismiss={addLesson=null}){title,start,end,notes->
        vm.addLesson(id,title,start,end,notes);addLesson=null
    }}
    renameUnit?.let{u->EducationTextDialog("تعديل الوحدة","العنوان",{renameUnit=null},u.title){
        vm.renameUnit(u.id,it);renameUnit=null
    }}
    deleteUnit?.let{u->EducationConfirm("حذف الوحدة؟","ستُحذف الدروس المرتبطة بها.",{deleteUnit=null}){
        vm.deleteUnit(u.id);deleteUnit=null
    }}
    renameLesson?.let{lesson->EducationTextDialog("تعديل الدرس","عنوان الدرس",{renameLesson=null},lesson.title){
        vm.renameLesson(lesson.id,it);renameLesson=null
    }}
    deleteLesson?.let{lesson->EducationConfirm("حذف الدرس؟",lesson.title,{deleteLesson=null}){
        vm.deleteLesson(lesson.id);deleteLesson=null
    }}
    renameCourse?.let{course->EducationTextDialog("تعديل اسم الكتاب","الاسم",{renameCourse=null},course.title){
        vm.renameCurriculum(course.id,it);renameCourse=null
    }}
    deleteCourse?.let{course->EducationConfirm("حذف الكتاب؟","سيُحذف ملف PDF المرتبط به.",{deleteCourse=null}){
        vm.deleteCurriculum(course.id);deleteCourse=null
    }}
}

@Composable
fun EducationTextDialog(title:String,label:String,onDismiss:()->Unit,initial:String="",onSave:(String)->Unit) {
    var text by remember(title,initial){mutableStateOf(initial)}
    AlertDialog(onDismissRequest=onDismiss,shape=RoundedCornerShape(22.dp),title={Text(title,color=Edu.Navy)},
        text={FormField(label,text,{text=it},Modifier.fillMaxWidth())},
        confirmButton={TextButton(enabled=text.isNotBlank(),onClick={onSave(text.trim())}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
fun EducationConfirm(title:String,message:String,onDismiss:()->Unit,onConfirm:()->Unit) {
    AlertDialog(onDismissRequest=onDismiss,title={Text(title)},text={Text(message)},
        confirmButton={TextButton(onClick=onConfirm){Text("تأكيد",color=Edu.Error)}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
private fun EducationManageDialog(title:String,name:String,onDismiss:()->Unit,
                                 onRename:(String)->Unit,onDelete:()->Unit) {
    var text by remember(name){mutableStateOf(name)}
    var confirmDelete by remember{mutableStateOf(false)}
    if(confirmDelete){EducationConfirm("تأكيد الحذف",name,{confirmDelete=false},onDelete)}else{
        AlertDialog(onDismissRequest=onDismiss,title={Text(title)},text={
            FormField("الاسم",text,{text=it},Modifier.fillMaxWidth())
        },confirmButton={TextButton(enabled=text.isNotBlank(),onClick={onRename(text.trim())}){Text("حفظ")}},
            dismissButton={
                Row {
                    TextButton(onClick={confirmDelete=true}){Text("حذف",color=Edu.Error)}
                    TextButton(onClick=onDismiss){Text("إلغاء")}
                }
            })
    }
}

@Composable
private fun PdfMetadataDialog(
    name:String,subjects:List<Pair<Long,String>>,grades:List<Pair<Long,String>>,
    subjectId:Long?,gradeId:Long?,onDismiss:()->Unit,
    onSave:(Long?,Long?,String,String)->Unit
) {
    var selectedSubject by remember(name){mutableStateOf(subjectId)}
    var selectedGrade by remember(name){mutableStateOf(gradeId)}
    var newSubject by remember(name){mutableStateOf("")}
    var newGrade by remember(name){mutableStateOf("")}
    val subject=selectedSubject?.takeIf{id->subjects.any{it.first==id}}?:subjects.firstOrNull()?.first
    val grade=selectedGrade?.takeIf{id->grades.any{it.first==id}}?:grades.firstOrNull()?.first
    AlertDialog(onDismissRequest=onDismiss,title={Text("بيانات الملف")},text={
        Column(Modifier.heightIn(max=440.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Text(name,color=Edu.Blue,fontWeight=FontWeight.Bold)
            Text("المادة",fontWeight=FontWeight.Bold)
            subjects.forEach{(id,label)->FilterChipPill(label,newSubject.isBlank()&&subject==id){
                selectedSubject=id;newSubject=""
            }}
            FormField("اسم مادة جديدة",newSubject,{newSubject=it},Modifier.fillMaxWidth())
            Text("الصف",fontWeight=FontWeight.Bold)
            grades.forEach{(id,label)->FilterChipPill(label,newGrade.isBlank()&&grade==id){
                selectedGrade=id;newGrade=""
            }}
            FormField("اسم صف جديد",newGrade,{newGrade=it},Modifier.fillMaxWidth())
        }
    },confirmButton={
        TextButton(enabled=(subject!=null||newSubject.isNotBlank())&&(grade!=null||newGrade.isNotBlank()),
            onClick={onSave(if(newSubject.isNotBlank())null else subject,
                if(newGrade.isNotBlank())null else grade,newSubject.trim(),newGrade.trim())}) {
            Text("حفظ المنهج")
        }
    },dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
private fun EducationLessonDialog(onDismiss:()->Unit,onSave:(String,String,String,String)->Unit) {
    var title by remember{mutableStateOf("")}
    var first by remember{mutableStateOf("")}
    var last by remember{mutableStateOf("")}
    var note by remember{mutableStateOf("")}
    AlertDialog(onDismissRequest=onDismiss,title={Text("درس جديد")},text={
        Column(Modifier.heightIn(max=430.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(9.dp)){
            FormField("عنوان الدرس",title,{title=it},Modifier.fillMaxWidth())
            FormField("من صفحة",first,{first=it.filter(Char::isDigit)},Modifier.fillMaxWidth())
            FormField("إلى صفحة",last,{last=it.filter(Char::isDigit)},Modifier.fillMaxWidth())
            FormField("نص الدرس",note,{note=it},Modifier.fillMaxWidth(),singleLine=false)
        }
    },confirmButton={TextButton(enabled=title.isNotBlank(),onClick={onSave(title,first,last,note)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}
