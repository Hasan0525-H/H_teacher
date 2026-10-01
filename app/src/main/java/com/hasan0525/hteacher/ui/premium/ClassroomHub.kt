package com.hasan0525.hteacher.ui.premium

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.domain.student.AttendanceStatus
import com.hasan0525.hteacher.domain.student.TeacherToolsSection
import com.hasan0525.hteacher.ui.tools.TeacherToolsViewModel
import com.hasan0525.hteacher.ui.tools.TeacherToolsViewModelFactory
import java.time.LocalDate

@Composable
fun ClassroomHub(onBack:()->Unit,onNavigate:(String)->Unit){
    val app=LocalContext.current.applicationContext as HTeacherApplication
    val vm:TeacherToolsViewModel=viewModel(factory=TeacherToolsViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar=remember{SnackbarHostState()}
    var addStudent by remember{mutableStateOf(false)}
    var selectedForGrade by remember{mutableStateOf<Long?>(null)}
    var deleteStudent by remember{mutableStateOf<Long?>(null)}
    var deleteRecord by remember{mutableStateOf<Long?>(null)}
    var search by remember{mutableStateOf("")}
    val exportPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){u:Uri?->
        u?.let(vm::exportReport)
    }
    LaunchedEffect(state.message){state.message?.let{snackbar.showSnackbar(it);vm.clearMessage()}}
    val present=state.attendance.count{it.dateEpochDay==state.dateEpochDay && it.status==AttendanceStatus.PRESENT.storageKey}
    Scaffold(containerColor=Edu.Canvas,
        topBar={AppTopBar("إدارة الفصل","الطلاب والتقييم والحضور",onBack)},
        bottomBar={EducationBottomNav("tools",onNavigate)},snackbarHost={SnackbarHost(snackbar)}
    ){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(horizontal=19.dp,vertical=18.dp),
            verticalArrangement=Arrangement.spacedBy(15.dp)) {
            item {
                Column(Modifier.fillMaxWidth().background(Edu.Navy,RoundedCornerShape(24.dp)).padding(20.dp),
                    verticalArrangement=Arrangement.spacedBy(13.dp)) {
                    Text("نظرة على الفصل",color=Edu.Paper,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        StatCard("الطلاب",state.students.size.toString(),EduGlyph.GROUP,Modifier.weight(1f))
                        StatCard("حضور اليوم",present.toString(),EduGlyph.DATE,Modifier.weight(1f),Edu.Teal)
                        StatCard("التقييمات",state.gradeRecords.size.toString(),EduGlyph.CHART,Modifier.weight(1f))
                    }
                }
            }
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    ToolModule(EduGlyph.GROUP,"الطلاب",TeacherToolsSection.STUDENTS==state.section,Modifier.weight(1f)){
                        vm.selectSection(TeacherToolsSection.STUDENTS)
                    }
                    ToolModule(EduGlyph.DATE,"الحضور",TeacherToolsSection.ATTENDANCE==state.section,Modifier.weight(1f)){
                        vm.selectSection(TeacherToolsSection.ATTENDANCE)
                    }
                }
            }
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){
                    ToolModule(EduGlyph.CHART,"الدرجات",TeacherToolsSection.GRADES==state.section,Modifier.weight(1f)){
                        vm.selectSection(TeacherToolsSection.GRADES)
                    }
                    ToolModule(EduGlyph.DOC,"التقارير",TeacherToolsSection.REPORTS==state.section,Modifier.weight(1f)){
                        vm.selectSection(TeacherToolsSection.REPORTS)
                    }
                }
            }
            when(state.section){
                TeacherToolsSection.STUDENTS -> {
                    item { SectionHeader("قائمة الطلاب",action="+ إضافة",onAction={addStudent=true}) }
                    item { FormField("بحث عن طالب",search,{search=it},Modifier.fillMaxWidth()) }
                    val filtered=state.students.filter{it.name.contains(search,true) || it.studentNumber.contains(search,true)}
                    if(filtered.isEmpty())item{
                        EmptyState("لا يوجد طلاب","أضف الطلاب لتسجيل الحضور والدرجات",
                            EduGlyph.GROUP,"إضافة طالب"){addStudent=true}
                    }else items(filtered,key={it.id}){student->
                        Row(Modifier.fillMaxWidth().background(Edu.Paper,RoundedCornerShape(17.dp)).padding(12.dp),
                            verticalAlignment=Alignment.CenterVertically) {
                            Box(Modifier.size(43.dp).background(Edu.BlueSoft,RoundedCornerShape(13.dp)),
                                contentAlignment=Alignment.Center){Glyph(EduGlyph.GROUP)}
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(student.name,color=Edu.Navy,fontWeight=FontWeight.Bold)
                                Text(student.studentNumber.ifBlank{"بدون رقم"},color=Edu.Muted,
                                    style=MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick={deleteStudent=student.id}){Text("حذف",color=Edu.Error)}
                        }
                    }
                }
                TeacherToolsSection.ATTENDANCE -> {
                    item {
                        AppCard {
                            SectionHeader("سجل الحضور","اختر التاريخ وسجل حالة كل طالب")
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                SecondaryButton("اليوم السابق",Modifier.weight(1f)){vm.previousDay()}
                                Text(LocalDate.ofEpochDay(state.dateEpochDay).toString(),
                                    Modifier.weight(1f),color=Edu.Navy)
                                SecondaryButton("التالي",Modifier.weight(1f)){vm.nextDay()}
                            }
                        }
                    }
                    if(state.students.isEmpty())item{EmptyState("لا يوجد طلاب","أضف الطلاب أولًا",EduGlyph.GROUP)}
                    else items(state.students,key={it.id}){student->
                        AppCard {
                            Text(student.name,color=Edu.Navy,fontWeight=FontWeight.Bold)
                            val selected=state.attendance.firstOrNull{
                                it.studentId==student.id && it.dateEpochDay==state.dateEpochDay
                            }?.status
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                                items(AttendanceStatus.entries){status->
                                    FilterChipPill(status.label,selected==status.storageKey){vm.setAttendance(student.id,status)}
                                }
                            }
                        }
                    }
                }
                TeacherToolsSection.GRADES -> {
                    item{SectionHeader("سجل الدرجات","اختر طالبًا ثم أضف تقييمًا")}
                    if(state.students.isEmpty())item{EmptyState("لا يوجد طلاب","أضف طلابك أولًا",EduGlyph.GROUP)}
                    else{
                        item {
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                items(state.students,key={it.id}){student->
                                    FilterChipPill(student.name,state.selectedStudentId==student.id){vm.selectStudent(student.id)}
                                }
                            }
                        }
                        val studentId=state.selectedStudentId
                        item {
                            PrimaryButton("إضافة تقييم",Modifier.fillMaxWidth(),
                                enabled=studentId!=null,icon=EduGlyph.PLUS){selectedForGrade=studentId}
                        }
                        val records=state.gradeRecords.filter{it.studentId==studentId}
                        if(records.isEmpty())item{
                            EmptyState("لا توجد تقييمات","أضف أول تقييم للطالب",EduGlyph.CHART)
                        }else items(records,key={it.id}){record->
                            AppCard {
                                Row(verticalAlignment=Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(record.title,color=Edu.Navy,fontWeight=FontWeight.Bold)
                                        Text("${record.score} / ${record.maxScore}",color=Edu.Teal)
                                    }
                                    TextButton(onClick={deleteRecord=record.id}){Text("حذف",color=Edu.Error)}
                                }
                            }
                        }
                    }
                }
                TeacherToolsSection.REPORTS -> {
                    item {
                        AppCard {
                            SectionHeader("تقرير الفصل","ملف واحد جاهز للطباعة")
                            Text("يتضمن الطلاب والحضور والدرجات الحالية.",
                                color=Edu.Muted,style=MaterialTheme.typography.bodyMedium)
                            PrimaryButton(if(state.isExporting)"جارٍ التصدير..." else "تصدير تقرير PDF",
                                Modifier.fillMaxWidth(),enabled=!state.isExporting,icon=EduGlyph.PDF){
                                exportPicker.launch("تقرير-الطلاب.pdf")
                            }
                            if(state.isExporting) LoadingView("إعداد التقرير")
                        }
                    }
                }
            }
        }
    }
    if(addStudent) StudentEntryDialog(state.grades,{addStudent=false}){name,number,grade->
        vm.addStudent(name,number,grade);addStudent=false
    }
    selectedForGrade?.let{id->GradeEntryDialog({selectedForGrade=null}){title,score,total->
        vm.addGradeRecord(id,title,score,total);selectedForGrade=null
    }}
    deleteStudent?.let{id->EducationConfirm("حذف الطالب؟","سيُحذف سجل الحضور والتقييمات المرتبطة به.",
        {deleteStudent=null}){vm.deleteStudent(id);deleteStudent=null}}
    deleteRecord?.let{id->EducationConfirm("حذف التقييم؟","لن يمكن التراجع.",
        {deleteRecord=null}){vm.deleteGradeRecord(id);deleteRecord=null}}
}

@Composable
private fun ToolModule(glyph:EduGlyph,title:String,selected:Boolean,modifier:Modifier=Modifier,onClick:()->Unit) {
    Surface(onClick=onClick,modifier=modifier.height(82.dp),shape=RoundedCornerShape(17.dp),
        color=if(selected)Edu.BlueSoft else Edu.Paper) {
        Row(Modifier.padding(13.dp),verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Glyph(glyph,tint=if(selected)Edu.Blue else Edu.Muted)
            Text(title,fontWeight=FontWeight.Bold,color=if(selected)Edu.Blue else Edu.Navy,
                style=MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StudentEntryDialog(grades:List<GradeEntity>,onDismiss:()->Unit,
    onSave:(String,String,Long?)->Unit) {
    var name by remember{mutableStateOf("")}
    var number by remember{mutableStateOf("")}
    var gradeId by remember{mutableStateOf<Long?>(grades.firstOrNull()?.id)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("طالب جديد")},text={
        Column(Modifier.heightIn(max=410.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(10.dp)) {
            FormField("اسم الطالب",name,{name=it},Modifier.fillMaxWidth())
            FormField("رقم الطالب",number,{number=it},Modifier.fillMaxWidth())
            Text("الصف",fontWeight=FontWeight.Bold,color=Edu.Navy)
            grades.forEach{grade->FilterChipPill(grade.name,gradeId==grade.id){gradeId=grade.id}}
        }
    },confirmButton={TextButton(enabled=name.isNotBlank(),onClick={onSave(name,number,gradeId)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
private fun GradeEntryDialog(onDismiss:()->Unit,onSave:(String,String,String)->Unit){
    var title by remember{mutableStateOf("")}
    var score by remember{mutableStateOf("")}
    var total by remember{mutableStateOf("")}
    AlertDialog(onDismissRequest=onDismiss,title={Text("تقييم جديد")},text={
        Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
            FormField("اسم التقييم",title,{title=it},Modifier.fillMaxWidth())
            FormField("الدرجة",score,{score=it},Modifier.fillMaxWidth())
            FormField("من",total,{total=it},Modifier.fillMaxWidth())
        }
    },confirmButton={TextButton(enabled=title.isNotBlank()&&score.isNotBlank()&&total.isNotBlank(),
        onClick={onSave(title,score,total)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}
