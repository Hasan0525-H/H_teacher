package com.hasan0525.hteacher.ui.exam

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.GeneratedExam
import com.hasan0525.hteacher.domain.exam.QuestionType

@Composable
fun ExamGeneratorRoute(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as HTeacherApplication
    val viewModel: ExamGeneratorViewModel = viewModel(
        factory = ExamGeneratorViewModelFactory(application)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExamGeneratorScreen(
        state = state,
        onBack = onBack,
        onSelectSubject = viewModel::selectSubject,
        onSelectCurriculum = viewModel::selectCurriculum,
        onToggleType = viewModel::toggleType,
        onSelectDifficulty = viewModel::selectDifficulty,
        onQuestionCountChange = viewModel::updateQuestionCount,
        onTotalMarksChange = viewModel::updateTotalMarks,
        onTitleChange = viewModel::updateTitle,
        onAddQuestion = viewModel::addQuestion,
        onDeleteQuestion = viewModel::deleteQuestion,
        onGenerate = viewModel::generateExam,
        onGenerateAi = viewModel::generateAiQuestions,
        onExport = viewModel::exportExam,
        onMessageShown = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamGeneratorScreen(
    state: ExamGeneratorUiState,
    onBack: () -> Unit,
    onSelectSubject: (Long) -> Unit,
    onSelectCurriculum: (Long?) -> Unit,
    onToggleType: (QuestionType) -> Unit,
    onSelectDifficulty: (Difficulty?) -> Unit,
    onQuestionCountChange: (String) -> Unit,
    onTotalMarksChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onAddQuestion: (String, String, QuestionType, Difficulty) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onGenerate: () -> Unit,
    onGenerateAi: () -> Unit,
    onExport: (Uri, Boolean) -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showQuestionBank by remember { mutableStateOf(false) }
    val questionsPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri -> if (uri != null) onExport(uri, false) }
    val answersPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri -> if (uri != null) onExport(uri, true) }

    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); onMessageShown() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("الاختبارات", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "رجوع") } }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("أنشئ اختبارك", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("حدد المطلوب ثم أنشئ النموذج", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Outlined.Assignment, null, Modifier.padding(12.dp).size(25.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("المصدر", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        SelectionRow("المادة", "أضف مادة", state.subjects.map { it.id to it.name }, state.selectedSubjectId, onSelectSubject)
                        Text("المنهج", fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(state.selectedCurriculumId == null, { onSelectCurriculum(null) }, label = { Text("كل المناهج") })
                            }
                            items(state.curricula, key = { it.id }) { curriculum ->
                                FilterChip(state.selectedCurriculumId == curriculum.id, { onSelectCurriculum(curriculum.id) }, label = { Text(curriculum.title, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                            }
                        }
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("مواصفات الاختبار", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.Tune, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        OutlinedTextField(Modifier.fillMaxWidth(), state.title, onTitleChange, singleLine = true, label = { Text("عنوان الاختبار") })
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(Modifier.weight(1f), state.questionCount, onQuestionCountChange, singleLine = true, label = { Text("الأسئلة") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(Modifier.weight(1f), state.totalMarks, onTotalMarksChange, singleLine = true, label = { Text("الدرجات") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        Text("نوع السؤال", fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(QuestionType.entries.toList(), key = { it.storageKey }) { type ->
                                FilterChip(type in state.selectedTypes, { onToggleType(type) }, label = { Text(type.label) })
                            }
                        }
                        Text("المستوى", fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { FilterChip(state.selectedDifficulty == null, { onSelectDifficulty(null) }, label = { Text("متنوع") }) }
                            items(Difficulty.entries.toList(), key = { it.storageKey }) { difficulty ->
                                FilterChip(state.selectedDifficulty == difficulty, { onSelectDifficulty(difficulty) }, label = { Text(difficulty.label) })
                            }
                        }
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("إنشاء", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(Modifier.weight(1f), onClick = onGenerate, enabled = state.selectedSubjectId != null) { Text("إنشاء") }
                            OutlinedButton(Modifier.weight(1f), onClick = { showQuestionBank = true }) { Text("بنك الأسئلة") }
                        }
                        Button(Modifier.fillMaxWidth(), enabled = state.aiConfigured && state.selectedCurriculumId != null && state.indexedLessonCount > 0 && !state.isAiGenerating, onClick = onGenerateAi) {
                            Text(if (state.isAiGenerating) "جارٍ التوليد..." else "توليد بالذكاء الاصطناعي")
                        }
                        if (state.isAiGenerating) LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text(
                            when {
                                !state.aiConfigured -> "الذكاء الاصطناعي غير مفعّل"
                                state.selectedCurriculumId == null -> "اختر منهجًا للتوليد الذكي"
                                state.indexedLessonCount == 0 -> "فهرس المنهج أولًا"
                                else -> "الدروس المفهرسة: ${state.indexedLessonCount}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            state.generatedExam?.let { exam ->
                item {
                    GeneratedExamCard(exam, state.isExporting, { questionsPdfLauncher.launch(safeFileName(exam.title + " - الأسئلة.pdf")) }, { answersPdfLauncher.launch(safeFileName(exam.title + " - الإجابة.pdf")) })
                }
                items(exam.questions, key = { it.number }) { question ->
                    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("السؤال ${question.number}", fontWeight = FontWeight.Bold)
                            Text(question.questionText)
                            Text("${question.type.label} · ${formatMark(question.mark)} درجة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
    if (showQuestionBank) QuestionBankDialog(state.questions, { showQuestionBank = false }, onAddQuestion, onDeleteQuestion)
}

@Composable
private fun SelectionRow(
    title: String,
    emptyText: String,
    items: List<Pair<Long, String>>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (items.isEmpty()) {
            Text(
                text = emptyText,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.first }) { item ->
                    FilterChip(
                        selected = selectedId == item.first,
                        onClick = { onSelect(item.first) },
                        label = { Text(item.second) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneratedExamCard(
    exam: GeneratedExam,
    isExporting: Boolean,
    onExportQuestions: () -> Unit,
    onExportAnswers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = exam.subjectName +
                    (exam.curriculumTitle?.let { " - " + it } ?: ""),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "عدد الأسئلة: " + exam.questions.size +
                    "    الدرجة: " + exam.totalMarks,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (isExporting) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !isExporting,
                    onClick = onExportQuestions
                ) {
                    Text("PDF الأسئلة")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !isExporting,
                    onClick = onExportAnswers
                ) {
                    Text("PDF الإجابة")
                }
            }
        }
    }
}

@Composable
private fun QuestionBankDialog(
    questions: List<QuestionEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, String, QuestionType, Difficulty) -> Unit,
    onDelete: (Long) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(QuestionType.MULTIPLE_CHOICE) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("بنك الأسئلة") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("نص السؤال") },
                    minLines = 2
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = answerText,
                    onValueChange = { answerText = it },
                    label = { Text("الإجابة") },
                    minLines = 2
                )

                Text(
                    text = "النوع",
                    fontWeight = FontWeight.Bold
                )

                QuestionType.entries.forEach { item ->
                    FilterChip(
                        selected = type == item,
                        onClick = { type = item },
                        label = { Text(item.label) }
                    )
                }

                Text(
                    text = "الصعوبة",
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Difficulty.entries.forEach { item ->
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = difficulty == item,
                            onClick = { difficulty = item },
                            label = { Text(item.label) }
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = questionText.isNotBlank(),
                    onClick = {
                        onAdd(
                            questionText,
                            answerText,
                            type,
                            difficulty
                        )
                        questionText = ""
                        answerText = ""
                    }
                ) {
                    Text("إضافة السؤال")
                }

                HorizontalDivider()

                if (questions.isEmpty()) {
                    Text(
                        text = "لا توجد أسئلة",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    questions.forEach { question ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = QuestionType
                                        .fromStorage(question.questionType)
                                        .label +
                                        " - " +
                                        Difficulty
                                            .fromStorage(question.difficulty)
                                            .label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = question.questionText,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )

                                TextButton(
                                    onClick = {
                                        onDelete(question.id)
                                    }
                                ) {
                                    Text("حذف")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("تم")
            }
        }
    )
}

private fun safeFileName(value: String): String =
    value.replace(Regex("[\\/:*?\"<>|]"), "-")

private fun formatMark(mark: Double): String =
    if (mark % 1.0 == 0.0) {
        mark.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", mark)
    }
