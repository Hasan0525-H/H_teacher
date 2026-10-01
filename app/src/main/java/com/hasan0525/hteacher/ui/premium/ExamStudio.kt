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
        topBar={AppTopBar("استوديو الاختبارات","من الإعداد إلى الطباعة",onBack)},
        bottomBar={EducationBottomNav("exams",onNavigate)},
        snackbarHost={SnackbarHost(snackbar)}
    ){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(horizontal=19.dp,vertical=18.dp),
            verticalArrangement=Arrangement.spacedBy(17.dp)){
            item {
                Column(Modifier.fillMaxWidth().background(Edu.Navy,RoundedCornerShape(24.dp)).padding(20.dp),
                    verticalArrangement=Arrangement.spacedBy(13.dp)){
                    Text("صمّم اختبارًا في دقائق",color=Edu.Paper,style=MaterialTheme.typography.titleLarge,
                        fontWeight=FontWeight.Bold)
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        listOf("١. المصدر","٢. الإعداد","٣. النتيجة").forEachIndexed{index,text->
                            Surface(onClick={step=index},shape=RoundedCornerShape(14.dp),
                                color=if(step==index) Edu.Amber else Edu.Paper.copy(alpha=.13f)){
                                Text(text,Modifier.padding(horizontal=12.dp,vertical=9.dp),
                                    color=if(step==index)Edu.Navy else Edu.Paper,
                                    style=MaterialTheme.typography.bodySmall,fontWeight=FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            when(step) {
                0 -> {
                    item { SectionHeader("اختر المحتوى","ابدأ بالمادة والمنهج المطلوبين") }
                    item {
                        AppCard {
                            Text("المادة",fontWeight=FontWeight.Bold,color=Edu.Navy)
                            if(state.subjects.isEmpty()) {
                                EmptyState("لا توجد مواد","أضف مادة من مكتبة المناهج",
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
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            StatCard("أسئلة محفوظة",state.questions.size.toString(),EduGlyph.EXAM,Modifier.weight(1f))
                            StatCard("دروس مفهرسة",state.indexedLessonCount.toString(),EduGlyph.BOOK,Modifier.weight(1f),
                                Edu.Teal)
                        }
                    }
                    item {
                        PrimaryButton("التالي: مواصفات الاختبار",Modifier.fillMaxWidth(),
                            enabled=state.selectedSubjectId!=null,icon=EduGlyph.ARROW){step=1}
                    }
                }
                1 -> {
                    item { SectionHeader("المواصفات","تحكم في نوع الاختبار وصعوبته") }
                    item {
                        AppCard {
                            FormField("اسم الاختبار",state.title,vm::updateTitle,Modifier.fillMaxWidth())
                            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                                FormField("عدد الأسئلة",state.questionCount,{vm.updateQuestionCount(it.filter(Char::isDigit))},Modifier.weight(1f))
                                FormField("الدرجة الكلية",state.totalMarks,{vm.updateTotalMarks(it.filter(Char::isDigit))},Modifier.weight(1f))
                            }
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
                    item {
                        FeatureCard("بنك الأسئلة","إضافة وتحرير الأسئلة المحفوظة",EduGlyph.FOLDER,
                            Modifier.fillMaxWidth(),Edu.Teal){bank=true}
                    }
                    item {
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            SecondaryButton("السابق",Modifier.weight(1f)){step=0}
                            PrimaryButton("إنشاء الاختبار",Modifier.weight(1.3f),icon=EduGlyph.CHECK,
                                enabled=!state.isAiGenerating){vm.generateExam();step=2}
                        }
                    }
                    item {
                        AppCard {
                            SectionHeader("إنشاء ذكي","باستخدام الدروس المفهرسة فقط")
                            if(state.isAiGenerating) LoadingView("جارٍ إنشاء الأسئلة")
                            PrimaryButton("توليد الأسئلة بالذكاء الاصطناعي",
                                Modifier.fillMaxWidth(),icon=EduGlyph.SPARK,
                                enabled=state.aiConfigured&&state.indexedLessonCount>0&&state.selectedCurriculumId!=null&&!state.isAiGenerating) {
                                vm.generateAiQuestions()
                            }
                            if(!state.aiConfigured)Text("الخدمة السحابية غير مفعّلة",color=Edu.Muted)
                            else if(state.indexedLessonCount==0)Text("أضف محتوى الدروس في المكتبة",color=Edu.Muted)
                        }
                    }
                }
                else -> {
                    item { SectionHeader("نتيجة الاختبار","معاينة وتصدير نسخة الطباعة") }
                    val exam=state.generatedExam
                    if(exam==null) {
                        item {
                            EmptyState("لم يُنشأ الاختبار بعد","راجع الشروط والأسئلة ثم أنشئ الاختبار",
                                EduGlyph.EXAM,"العودة للإعداد"){step=1}
                        }
                    } else {
                        item {
                            Column(Modifier.fillMaxWidth().background(Edu.Navy,RoundedCornerShape(22.dp))
                                .padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                                Text(exam.title,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,
                                    color=Edu.Paper)
                                Row {
                                    Text("${exam.questions.size} سؤال",color=Edu.Paper,modifier=Modifier.weight(1f))
                                    Text("${exam.totalMarks} درجة",color=Edu.Amber)
                                }
                                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                    PrimaryButton("الاختبار PDF",Modifier.weight(1f),
                                        enabled=!state.isExporting,icon=EduGlyph.PDF){
                                        questionsPicker.launch(exam.title+"-الاختبار.pdf")
                                    }
                                    SecondaryButton("نموذج الإجابة",Modifier.weight(1f),enabled=!state.isExporting){
                                        answersPicker.launch(exam.title+"-الإجابة.pdf")
                                    }
                                }
                            }
                        }
                        items(exam.questions,key={it.number}){q->
                            AppCard {
                                Text("سؤال ${q.number} • ${q.type.label}",color=Edu.Blue,
                                    style=MaterialTheme.typography.labelMedium,fontWeight=FontWeight.Bold)
                                Text(q.questionText,color=Edu.Navy,fontWeight=FontWeight.Bold)
                                Text("${q.mark} درجة",color=Edu.Muted)
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
