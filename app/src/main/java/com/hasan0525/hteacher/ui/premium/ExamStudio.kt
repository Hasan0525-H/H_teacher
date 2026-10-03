package com.hasan0525.hteacher.ui.premium

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.QuestionType
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModel
import com.hasan0525.hteacher.ui.exam.ExamGeneratorViewModelFactory

@Composable
fun ExamStudio(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val app = LocalContext.current.applicationContext as HTeacherApplication
    val vm: ExamGeneratorViewModel = viewModel(factory = ExamGeneratorViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var step by rememberSaveable { mutableIntStateOf(0) }
    val questionsPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        uri?.let { vm.exportExam(it, false) }
    }
    val answersPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        uri?.let { vm.exportExam(it, true) }
    }

    LaunchedEffect(state.generatedExam, state.isAiGenerating) {
        if (state.generatedExam != null && !state.isAiGenerating) step = 2
    }
    LaunchedEffect(state.message) {
        state.message?.let { snackbar.showSnackbar(it); vm.clearMessage() }
    }

    Scaffold(
        containerColor = Edu.Canvas,
        topBar = { AppTopBar("الاختبارات", onBack = onBack) },
        bottomBar = { EducationBottomNav("exams", onNavigate) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                WorkflowSteps(current = step, titles = listOf("المصدر", "الإعداد", "المعاينة")) { selected ->
                    if (selected < 2 || state.generatedExam != null) step = selected
                }
            }
            when (step) {
                0 -> sourceStep(state, vm, onNavigate) { step = 1 }
                1 -> setupStep(state, vm) { if (!state.isAiGenerating) vm.generateAiQuestionsFromSelectedSource() }
                else -> previewStep(state, questionsPicker, answersPicker) { step = 1 }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.sourceStep(
    state: com.hasan0525.hteacher.ui.exam.ExamGeneratorUiState,
    vm: ExamGeneratorViewModel,
    onNavigate: (String) -> Unit,
    onNext: () -> Unit
) {
    item { SectionHeader("اختر مصدر الأسئلة", "اختر المادة والمنهج الذي سيُبنى عليه الاختبار") }
    item {
        AppCard {
            Text("المادة", color = Edu.Navy, fontWeight = FontWeight.Bold)
            if (state.subjects.isEmpty()) {
                EmptyState("أضف مادة أولاً", "", EduGlyph.BOOK, "فتح المكتبة") { onNavigate("curricula") }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.subjects, key = { it.id }) { subject ->
                        FilterChipPill(subject.name, state.selectedSubjectId == subject.id) { vm.selectSubject(subject.id) }
                    }
                }
            }
            HorizontalDivider(color = Edu.Line)
            Text("المنهج", color = Edu.Navy, fontWeight = FontWeight.Bold)
            if (state.curricula.isEmpty()) {
                Text("أضف كتابًا من المكتبة أولًا", color = Edu.Muted)
            } else LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.curricula, key = { it.id }) { book ->
                    FilterChipPill(book.title, state.selectedCurriculumId == book.id) { vm.selectCurriculum(book.id) }
                }
            }
        }
    }
    item {
        PrimaryButton("التالي", Modifier.fillMaxWidth(), enabled = state.selectedCurriculumId != null, icon = EduGlyph.ARROW, onClick = onNext)
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.setupStep(
    state: com.hasan0525.hteacher.ui.exam.ExamGeneratorUiState,
    vm: ExamGeneratorViewModel,
    onGenerate: () -> Unit
) {
    item { SectionHeader("إعداد الاختبار", "حدد العدد والنوع والدرجة ثم اضغط توليد الأسئلة") }
    item {
        AppCard {
            FormField("اسم الاختبار", state.title, vm::updateTitle, Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField("عدد الأسئلة", state.questionCount, { vm.updateQuestionCount(it) }, Modifier.weight(1f))
                FormField("الدرجة الكلية", state.totalMarks, { vm.updateTotalMarks(it) }, Modifier.weight(1f))
            }
            Text("نوع الأسئلة", color = Edu.Navy, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                QuestionType.entries.forEach { kind ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = kind in state.selectedTypes,
                            onCheckedChange = { vm.toggleType(kind) },
                            colors = CheckboxDefaults.colors(checkedColor = Edu.Blue)
                        )
                        Text(kind.label, color = Edu.Navy)
                    }
                }
            }
            Text("المستوى", color = Edu.Navy, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChipPill("متنوع", state.selectedDifficulty == null) { vm.selectDifficulty(null) } }
                items(Difficulty.entries) { level ->
                    FilterChipPill(level.label, state.selectedDifficulty == level) { vm.selectDifficulty(level) }
                }
            }
        }
    }
    item {
        if (state.isAiGenerating) {
            AppCard {
                Text("جارٍ توليد الأسئلة من المنهج", color = Edu.Navy, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { state.generationProgress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = Edu.Teal,
                    trackColor = Edu.BlueSoft
                )
                Text("${state.generationProgress}%", color = Edu.Teal, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        } else {
            PrimaryButton(
                "توليد الأسئلة",
                Modifier.fillMaxWidth(),
                enabled = state.selectedCurriculumId != null && state.aiConfigured,
                icon = EduGlyph.SPARK,
                onClick = onGenerate
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.previewStep(
    state: com.hasan0525.hteacher.ui.exam.ExamGeneratorUiState,
    questionsPicker: androidx.activity.result.ActivityResultLauncher<String>,
    answersPicker: androidx.activity.result.ActivityResultLauncher<String>,
    onEdit: () -> Unit
) {
    val exam = state.generatedExam
    item { SectionHeader("معاينة الاختبار", "راجع الأسئلة ثم صدّر ملفات PDF") }
    if (exam == null) {
        item { EmptyState("لم يُنشأ الاختبار", "ارجع للإعداد وابدأ التوليد", EduGlyph.EXAM, "العودة للإعداد", onEdit) }
    } else {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("ورقة الأسئلة", Modifier.weight(1f), enabled = !state.isExporting, icon = EduGlyph.PDF) {
                    questionsPicker.launch(exam.title + "-الاختبار.pdf")
                }
                SecondaryButton("الإجابات", Modifier.weight(1f), enabled = !state.isExporting) {
                    answersPicker.launch(exam.title + "-الإجابة.pdf")
                }
            }
        }
        item {
            Column(
                Modifier.fillMaxWidth().background(Edu.Paper, RoundedCornerShape(12.dp)).padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text("ورقة اختبار", color = Edu.Muted, style = MaterialTheme.typography.labelSmall)
                HorizontalDivider(color = Edu.Navy)
                Text(exam.title, color = Edu.Navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("المادة: ${exam.subjectName}    الدرجة: ${exam.totalMarks}", color = Edu.Muted)
                HorizontalDivider(color = Edu.Line)
                exam.questions.forEach { question ->
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("${question.number}. ", color = Edu.Blue, fontWeight = FontWeight.Bold)
                            Text(question.questionText, Modifier.weight(1f), color = Edu.Navy, fontWeight = FontWeight.SemiBold)
                            Text("(${question.mark})", color = Edu.Muted)
                        }
                        HorizontalDivider(color = Edu.Line)
                    }
                }
            }
        }
        item { SecondaryButton("تعديل الإعدادات", Modifier.fillMaxWidth(), onClick = onEdit) }
    }
}
