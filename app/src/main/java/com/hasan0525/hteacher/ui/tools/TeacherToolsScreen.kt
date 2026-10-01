package com.hasan0525.hteacher.ui.tools

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.GradeRecordEntity
import com.hasan0525.hteacher.data.local.entity.StudentEntity
import com.hasan0525.hteacher.domain.student.AttendanceStatus
import com.hasan0525.hteacher.domain.student.TeacherToolsSection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TeacherToolsRoute(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as HTeacherApplication
    val viewModel: TeacherToolsViewModel = viewModel(
        factory = TeacherToolsViewModelFactory(application)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TeacherToolsScreen(
        state = state,
        onBack = onBack,
        onSelectSection = viewModel::selectSection,
        onPreviousDay = viewModel::previousDay,
        onNextDay = viewModel::nextDay,
        onSelectStudent = viewModel::selectStudent,
        onAddStudent = viewModel::addStudent,
        onDeleteStudent = viewModel::deleteStudent,
        onSetAttendance = viewModel::setAttendance,
        onAddGradeRecord = viewModel::addGradeRecord,
        onDeleteGradeRecord = viewModel::deleteGradeRecord,
        onExportReport = viewModel::exportReport,
        onMessageShown = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeacherToolsScreen(
    state: TeacherToolsUiState,
    onBack: () -> Unit,
    onSelectSection: (TeacherToolsSection) -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSelectStudent: (Long) -> Unit,
    onAddStudent: (String, String, Long?) -> Unit,
    onDeleteStudent: (Long) -> Unit,
    onSetAttendance: (Long, AttendanceStatus) -> Unit,
    onAddGradeRecord: (Long, String, String, String) -> Unit,
    onDeleteGradeRecord: (Long) -> Unit,
    onExportReport: (Uri) -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showStudentDialog by remember { mutableStateOf(false) }
    var showGradeDialog by remember { mutableStateOf(false) }

    val reportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) onExportReport(uri)
    }

    LaunchedEffect(state.message) {
        val current = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(current)
        onMessageShown()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "أدوات المعلم",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = TeacherToolsSection.entries.toList(),
                    key = { it.name }
                ) { section ->
                    FilterChip(
                        selected = state.section == section,
                        onClick = { onSelectSection(section) },
                        label = { Text(section.label) }
                    )
                }
            }

            when (state.section) {
                TeacherToolsSection.STUDENTS -> StudentsContent(
                    state = state,
                    onAdd = { showStudentDialog = true },
                    onDelete = onDeleteStudent
                )

                TeacherToolsSection.ATTENDANCE -> AttendanceContent(
                    state = state,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    onSetAttendance = onSetAttendance
                )

                TeacherToolsSection.GRADES -> GradesContent(
                    state = state,
                    onSelectStudent = onSelectStudent,
                    onAdd = { showGradeDialog = true },
                    onDelete = onDeleteGradeRecord
                )

                TeacherToolsSection.REPORTS -> ReportsContent(
                    state = state,
                    onExport = {
                        reportLauncher.launch("تقرير الطلاب.pdf")
                    }
                )
            }
        }
    }

    if (showStudentDialog) {
        AddStudentDialog(
            grades = state.grades,
            onDismiss = { showStudentDialog = false },
            onSave = { name, number, gradeId ->
                onAddStudent(name, number, gradeId)
                showStudentDialog = false
            }
        )
    }

    if (showGradeDialog) {
        val studentId = state.selectedStudentId

        if (studentId != null) {
            AddGradeDialog(
                onDismiss = { showGradeDialog = false },
                onSave = { title, score, maxScore ->
                    onAddGradeRecord(
                        studentId,
                        title,
                        score,
                        maxScore
                    )
                    showGradeDialog = false
                }
            )
        }
    }
}

@Composable
private fun StudentsContent(
    state: TeacherToolsUiState,
    onAdd: () -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAdd
            ) {
                Text("إضافة طالب")
            }
        }

        if (state.students.isEmpty()) {
            item {
                EmptyCard("لا يوجد طلاب")
            }
        } else {
            items(
                items = state.students,
                key = { it.id }
            ) { student ->
                StudentCard(
                    student = student,
                    grade = state.grades.firstOrNull {
                        it.id == student.gradeId
                    },
                    onDelete = { onDelete(student.id) }
                )
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: StudentEntity,
    grade: GradeEntity?,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = student.name,
                    fontWeight = FontWeight.Bold
                )

                val details = buildList {
                    if (grade != null) add(grade.name)
                    if (student.studentNumber.isNotBlank()) {
                        add("رقم: " + student.studentNumber)
                    }
                }

                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(onClick = onDelete) {
                Text("حذف")
            }
        }
    }
}

@Composable
private fun AttendanceContent(
    state: TeacherToolsUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSetAttendance: (Long, AttendanceStatus) -> Unit
) {
    val date = LocalDate.ofEpochDay(state.dateEpochDay)
    val formatter = DateTimeFormatter.ofPattern(
        "yyyy/MM/dd",
        Locale.US
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onPreviousDay
                ) {
                    Text("اليوم السابق")
                }

                Text(
                    text = date.format(formatter),
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onNextDay
                ) {
                    Text("اليوم التالي")
                }
            }
        }

        if (state.students.isEmpty()) {
            item {
                EmptyCard("أضف الطلاب")
            }
        } else {
            items(
                items = state.students,
                key = { it.id }
            ) { student ->
                val current = state.attendance.firstOrNull {
                    it.studentId == student.id &&
                        it.dateEpochDay == state.dateEpochDay
                }

                AttendanceCard(
                    student = student,
                    selected = current?.let {
                        AttendanceStatus.fromStorage(it.status)
                    },
                    onSelect = { status ->
                        onSetAttendance(student.id, status)
                    }
                )
            }
        }
    }
}

@Composable
private fun AttendanceCard(
    student: StudentEntity,
    selected: AttendanceStatus?,
    onSelect: (AttendanceStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = student.name,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = AttendanceStatus.entries.toList(),
                    key = { it.storageKey }
                ) { status ->
                    FilterChip(
                        selected = selected == status,
                        onClick = { onSelect(status) },
                        label = { Text(status.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GradesContent(
    state: TeacherToolsUiState,
    onSelectStudent: (Long) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Long) -> Unit
) {
    val selectedStudent = state.students.firstOrNull {
        it.id == state.selectedStudentId
    }
    val records = state.gradeRecords.filter {
        it.studentId == state.selectedStudentId
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            if (state.students.isEmpty()) {
                EmptyCard("أضف الطلاب")
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.students,
                        key = { it.id }
                    ) { student ->
                        FilterChip(
                            selected = state.selectedStudentId == student.id,
                            onClick = {
                                onSelectStudent(student.id)
                            },
                            label = {
                                Text(
                                    text = student.name,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }
        }

        if (selectedStudent != null) {
            item {
                val earned = records.sumOf { it.score }
                val max = records.sumOf { it.maxScore }
                val average = if (max > 0.0) {
                    earned / max * 100.0
                } else {
                    0.0
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = selectedStudent.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "المتوسط: " +
                                String.format(
                                    Locale.US,
                                    "%.1f%%",
                                    average
                                )
                        )
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAdd
                ) {
                    Text("إضافة درجة")
                }
            }

            if (records.isEmpty()) {
                item {
                    EmptyCard("لا توجد درجات")
                }
            } else {
                items(
                    items = records,
                    key = { it.id }
                ) { record ->
                    GradeRecordCard(
                        record = record,
                        onDelete = { onDelete(record.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GradeRecordCard(
    record: GradeRecordEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatScore(record.score) +
                        " / " + formatScore(record.maxScore),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onDelete) {
                Text("حذف")
            }
        }
    }
}

@Composable
private fun ReportsContent(
    state: TeacherToolsUiState,
    onExport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "تقرير الطلاب",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("عدد الطلاب: " + state.students.size)
                    Text("سجلات الحضور: " + state.attendance.size)
                    Text("سجلات الدرجات: " + state.gradeRecords.size)

                    if (state.isExporting) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isExporting,
                        onClick = onExport
                    ) {
                        Text("تصدير PDF")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddStudentDialog(
    grades: List<GradeEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var gradeId by remember {
        mutableStateOf(grades.firstOrNull()?.id)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة طالب") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الطالب") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("رقم الطالب - اختياري") }
                )

                if (grades.isNotEmpty()) {
                    Text(
                        text = "الصف",
                        fontWeight = FontWeight.Bold
                    )

                    grades.forEach { grade ->
                        FilterChip(
                            selected = gradeId == grade.id,
                            onClick = { gradeId = grade.id },
                            label = { Text(grade.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(name, number, gradeId)
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun AddGradeDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var maxScore by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة درجة") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم التقييم") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = score,
                    onValueChange = {
                        score = normalizeDecimal(it)
                    },
                    label = { Text("درجة الطالب") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = maxScore,
                    onValueChange = {
                        maxScore = normalizeDecimal(it)
                    },
                    label = { Text("الدرجة الكلية") }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() &&
                    score.isNotBlank() &&
                    maxScore.isNotBlank(),
                onClick = {
                    onSave(title, score, maxScore)
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun EmptyCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(18.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun normalizeDecimal(value: String): String {
    val normalized = value
        .replace('٫', '.')
        .replace('،', '.')
        .filter { it.isDigit() || it == '.' }

    val firstDot = normalized.indexOf('.')

    return if (firstDot < 0) {
        normalized.take(7)
    } else {
        normalized
            .filterIndexed { index, char ->
                char != '.' || index == firstDot
            }
            .take(7)
    }
}

private fun formatScore(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
