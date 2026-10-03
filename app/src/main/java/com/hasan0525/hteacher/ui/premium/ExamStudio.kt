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
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.QuestionType
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModel
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModelFactory

@Composable
fun ExamStudio(onBack:()->Unit,onNavigate:(String)->Unit) {
    val app=LocalContext.current.applicationContext as HTeacherApplication
    val vm:ExamGeneratorViewModel=viewModel(factory=ExamGeneratorViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar=remember{SnackbarHostState()}
    var advanced by remember { mutableStateOf(false) }
    var step by remember{mutableIntStateOf(0)}
    var bank by remember{mutableStateOf(false)}
    var addQuestion by remember{mutableStateOf(false)}
    var deleteQuestion by remember{mutableStateOf<Long?>(null)}
    val questionsPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){uri:Uri?->
        uri?.let{vm.exportExam(it,false)}
    }
    val answersPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){uri:Uri?->
        uri?.let{vm.exportExam(it,true)}
    }
    LaunchedEffect(state.message){state.message?.let{snackbar.showSnackbar(it);vm.clearMessage()}}
    Scaffold(containerColor=Edu.Canvas,
        topBar={AppTopBar("الاختبارات", onBack = onBack)},
        bottomBar={EducationBottomNav("exams",onNavigate)},
        snackbarHost={SnackbarHost(snackbar)}
    ){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(horizontal=19.dp,vertical=18.dp),
            verticalArrangement=Arrangement.spacedBy(17.dp)){
            item {
                WorkflowSteps(
                    current = step,
                    titles = listOf("المصدر", "التصميم", "المعاينة"),
                    onSelect = { selected ->
                        if (selected < 2 || state.generatedExam != null) step = selected
                    }
                )
            }
            when(step) {
                0 -> {
                    item { SectionHeader("اختر المادة") }
                    item {
                        AppCard {
                            Text("المادة",fontWeight=FontWeight.Bold,color=Edu.Navy)
                            if(state.subjects.isEmpty()) {
                                EmptyState("أضف مادة أولاً","",
                                    EduGlyph.BOOK,"انتقل للمكتبة"){onNavigate("curricula")}
                            } else LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                items(state.subjects,key={it.id}) { subject ->
                                    FilterChipPill(subject.name,state.selectedSubjectId==subject.id){vm.selectSubject(subject.id)}
                                }
                            }
                            HorizontalDivider(color=Edu.Line)
                            Text("نطاق الأسئلة",fontWeight=FontWeight.Bold,color=Edu.Navy)
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                item { FilterChipPill("كل المناهج",state.selectedCurriculumId==null){vm.selectCurriculum(null)} }
                                items(state.curricula,key={it.id}){book->
                                    FilterChipPill(book.title,state.selectedCurriculumId==book.id){vm.selectCurriculum(book.id)}
                                }
                            }
                        }
                    }
                    item {
                        PrimaryButton("التالي",Modifier.fillMaxWidth(),
                            enabled=state.selectedSubjectId!=null,icon=EduGlyph.ARROW){step=1}
                    }
                }
                1 -> {
                    item { SectionHeader("إعداد الاختبار") }
                    item {
                        AppCard {
                            FormField("اسم الاختبار",state.title,vm::updateTitle,Modifier.fillMaxWidth())
                            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                                FormField("عدد الأسئلة",state.questionCount,{vm.updateQuestionCount(it.filter(Char::isDigit))},Modifier.weight(1f))
                                FormField("الدرجة الكلية",state.totalMarks,{vm.updateTotalMarks(it.filter(Char::isDigit))},Modifier.weight(1f))
                            }
                            TextButton(onClick = { advanced = !advanced }) {
                                Text(if (advanced) "إخفاء الخيارات" else "خيارات إضافية", color = Edu.Teal)
                            }
                            if (advanced) {
                            Text("أنواع الأسئلة",fontWeight=FontWeight.Bold,color=Edu.Navy)
                            QuestionType.entries.forEach{kind->
                                Row(verticalAlignment=Alignment.CenterVertically) {
                                    Checkbox(checked=kind in state.selectedTypes,onCheckedChange={vm.toggleType(kind)},
                                        colors=CheckboxDefaults.colors(checkedColor=Edu.Blue))
                                    Text(kind.label,color=Edu.Navy)
                                }
                            }
                            HorizontalDivider(color=Edu.Line)
                            Text("المستوى",fontWeight=FontWeight.Bold,color=Edu.Navy)
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                item { FilterChipPill("متنوع",state.selectedDifficulty==null){vm.selectDifficulty(null)} }
                                items(Difficulty.entries){kind->
                                    FilterChipPill(kind.label,state.selectedDifficulty==kind){vm.selectDifficulty(kind)}
                                }
                            }
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("الأسئلة المتاحة: ${state.matchingQuestionCount}",
                                Modifier.weight(1f), color = Edu.Navy,
                                fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = { bank = true }) { Text("بنك الأسئلة") }
                            TextButton(onClick = { addQuestion = true }) { Text("+ سؤال") }
                        }
                        if (state.matchingQuestionCount == 0) {
                            Text(
                                if (state.questions.isEmpty()) "أضف سؤالًا للبدء."
                                else "لا توجد أسئلة بهذا النوع أو المستوى. عدّل الخيارات.",
                                color = Edu.Muted
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            SecondaryButton("السابق",Modifier.weight(1f)){step=0}
                            PrimaryButton("إنشاء الاختبار",Modifier.weight(1.3f),icon=EduGlyph.CHECK,
                                enabled=!state.isAiGenerating && state.matchingQuestionCount > 0){
                                if (vm.generateExam()) step = 2
                            }
                        }
                    }
                    if (advanced || state.matchingQuestionCount == 0) {
                    item {
                        AppCard {
                            SectionHeader("إنشاء أسئلة")
                            if(state.isAiGenerating) LoadingView("جارٍ إنشاء الأسئلة")
                            PrimaryButton("توليد الأسئلة بالذكاء الاصطناعي",
                                Modifier.fillMaxWidth(),icon=EduGlyph.SPARK,
                                enabled=state.aiConfigured&&state.indexedLessonCount>0&&state.selectedCurriculumId!=null&&!state.isAiGenerating) {
                                vm.generateAiQuestions()
                            }
                            if(!state.aiConfigured) Text("الذكاء الاصطناعي غير متاح حالياً", color=Edu.Muted)
                            else if(state.selectedCurriculumId == null) Text("اختر منهجاً لإنشاء الأسئلة",color=Edu.Muted)
                            else if(state.indexedLessonCount==0) Text("أضف نص الدروس إلى المنهج أولاً",color=Edu.Muted)
                        }
                    }
                    }
                }
                else -> {
                    item { SectionHeader("الاختبار") }
                    val exam=state.generatedExam
                    if(exam==null) {
                        item {
                            EmptyState("لم يُنشأ الاختبار","أضف أسئلة أو عدّل الخيارات",
                                EduGlyph.EXAM,"إضافة أسئلة"){step=1;addQuestion=true}
                        }
                    } else {
                        item {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp)){
                                    PrimaryButton("ورقة الاختبار",Modifier.weight(1f),
                                        enabled=!state.isExporting,icon=EduGlyph.PDF){
                                        questionsPicker.launch(exam.title+"-الاختبار.pdf")
                                    }
                                    SecondaryButton("نموذج الإجابة",Modifier.weight(1f),
                                        enabled=!state.isExporting){
                                        answersPicker.launch(exam.title+"-الإجابة.pdf")
                                    }
                                }
                                // Real paper preview instead of unrelated question cards.
                                Column(
                                    Modifier.fillMaxWidth().background(Edu.Paper,RoundedCornerShape(9.dp))
                                        .padding(horizontal=22.dp,vertical=26.dp),
                                    verticalArrangement=Arrangement.spacedBy(16.dp)
                                ) {
                                    Text("وزارة التعليم  •  ورقة اختبار",color=Edu.Muted,
                                        style=MaterialTheme.typography.labelSmall)
                                    HorizontalDivider(color=Edu.Navy)
                                    Text(exam.title,color=Edu.Navy,
                                        style=MaterialTheme.typography.titleLarge,
                                        fontWeight=FontWeight.ExtraBold)
                                    Row(verticalAlignment=Alignment.CenterVertically) {
                                        Text("الاسم: ................................",Modifier.weight(1f),
                                            color=Edu.Navy,style=MaterialTheme.typography.bodyMedium)
                                        Text("الدرجة: ${exam.totalMarks}",color=Edu.Navy,
                                            style=MaterialTheme.typography.bodyMedium)
                                    }
                                    HorizontalDivider(color=Edu.Line)
                                    exam.questions.forEach { q ->
                                        Column(verticalArrangement=Arrangement.spacedBy(7.dp)) {
                                            Row(verticalAlignment=Alignment.Top) {
                                                Text("${q.number}. ",color=Edu.Blue,fontWeight=FontWeight.Bold)
                                                Text(q.questionText,Modifier.weight(1f),
                                                    color=Edu.Navy,fontWeight=FontWeight.SemiBold)
                                                Text("(${q.mark})",color=Edu.Muted)
                                            }
                                            Text(q.type.label,color=Edu.Muted,
                                                style=MaterialTheme.typography.labelSmall)
                                            HorizontalDivider(color=Edu.Line)
                                        }
                                    }
                                    Text("",color=Edu.Muted,
                                        style=MaterialTheme.typography.labelMedium,
                                        modifier=Modifier.align(Alignment.CenterHorizontally))
                                }
                            }
                        }
                    }
                    item { SecondaryButton("تعديل الاختبار",Modifier.fillMaxWidth()){step=1} }
                }
            }
        }
    }
    if(bank) AlertDialog(onDismissRequest={bank=false},title={Text("بنك الأسئلة")},text={
        Column(Modifier.heightIn(max=440.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(8.dp)){
            Text("عدد الأسئلة: ${state.questions.size}",color=Edu.Muted)
            state.questions.forEach{q->
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text(q.questionText,Modifier.weight(1f),color=Edu.Navy,maxLines=3)
                    TextButton(onClick={deleteQuestion=q.id}){Text("حذف",color=Edu.Error)}
                }
                HorizontalDivider(color=Edu.Line)
            }
        }
    },confirmButton={TextButton(onClick={addQuestion=true;bank=false}){Text("+ سؤال جديد")}},
        dismissButton={TextButton(onClick={bank=false}){Text("إغلاق")}})
    if(addQuestion) EducationQuestionDialog(onDismiss={addQuestion=false}){text,answer,type,difficulty->
        vm.addQuestion(text,answer,type,difficulty);addQuestion=false
    }
    deleteQuestion?.let{id->EducationConfirm("حذف السؤال؟","لن يمكن استرجاعه.",{deleteQuestion=null}){
        vm.deleteQuestion(id);deleteQuestion=null
    }}
}

@Composable
private fun EducationQuestionDialog(onDismiss:()->Unit,onSave:(String,String,QuestionType,Difficulty)->Unit) {
    var text by remember{mutableStateOf("")}
    var answer by remember{mutableStateOf("")}
    var type by remember{mutableStateOf(QuestionType.MULTIPLE_CHOICE)}
    var difficulty by remember{mutableStateOf(Difficulty.MEDIUM)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("سؤال جديد")},text={
        Column(Modifier.heightIn(max=445.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(10.dp)){
            FormField("السؤال",text,{text=it},Modifier.fillMaxWidth(),singleLine=false)
            FormField("الإجابة",answer,{answer=it},Modifier.fillMaxWidth(),singleLine=false)
            Text("النوع",color=Edu.Navy,fontWeight=FontWeight.Bold)
            QuestionType.entries.forEach{kind->
                FilterChipPill(kind.label,type==kind){type=kind}
            }
            Text("الصعوبة",color=Edu.Navy,fontWeight=FontWeight.Bold)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                Difficulty.entries.forEach{kind->
                    FilterChipPill(kind.label,difficulty==kind){difficulty=kind}
                }
            }
        }
    },confirmButton={TextButton(enabled=text.isNotBlank(),onClick={onSave(text,answer,type,difficulty)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}
