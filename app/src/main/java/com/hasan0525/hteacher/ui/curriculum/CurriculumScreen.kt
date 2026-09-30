package com.hasan0525.hteacher.ui.curriculum

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity

@Composable
fun CurriculumRoute(
    onBack: () -> Unit,
    onOpenPdf: (String, String) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as HTeacherApplication
    val viewModel: CurriculumViewModel = viewModel(
        factory = CurriculumViewModelFactory(application)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CurriculumScreen(
        state = state,
        onBack = onBack,
        onOpenPdf = onOpenPdf,
        onSelectSubject = viewModel::selectSubject,
        onSelectGrade = viewModel::selectGrade,
        onAddSubject = viewModel::addSubject,
        onRenameSubject = viewModel::renameSubject,
        onDeleteSubject = viewModel::deleteSubject,
        onAddGrade = viewModel::addGrade,
        onRenameGrade = viewModel::renameGrade,
        onDeleteGrade = viewModel::deleteGrade,
        onImportPdf = viewModel::importPdf,
        onRenameCurriculum = viewModel::renameCurriculum,
        onDeleteCurriculum = viewModel::deleteCurriculum,
        onMessageShown = viewModel::clearMessage
    )
}

@Composable
private fun CurriculumScreen(
    state: CurriculumUiState,
    onBack: () -> Unit,
    onOpenPdf: (String, String) -> Unit,
    onSelectSubject: (Long) -> Unit,
    onSelectGrade: (Long) -> Unit,
    onAddSubject: (String) -> Unit,
    onRenameSubject: (Long, String) -> Unit,
    onDeleteSubject: (Long) -> Unit,
    onAddGrade: (String) -> Unit,
    onRenameGrade: (Long, String) -> Unit,
    onDeleteGrade: (Long) -> Unit,
    onImportPdf: (Uri) -> Unit,
    onRenameCurriculum: (Long, String) -> Unit,
    onDeleteCurriculum: (Long) -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showSubjectManager by remember { mutableStateOf(false) }
    var showGradeManager by remember { mutableStateOf(false) }
    var editingCurriculum by remember { mutableStateOf<CurriculumEntity?>(null) }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) onImportPdf(uri)
    }

    LaunchedEffect(state.message) {
        val currentMessage = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(currentMessage)
        onMessageShown()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "المناهج",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SelectorSection(
                    title = "المادة",
                    emptyText = "أضف مادة للبدء",
                    labels = state.subjects.map { it.id to it.name },
                    selectedId = state.selectedSubjectId,
                    onSelect = onSelectSubject,
                    onManage = { showSubjectManager = true }
                )
            }

            item {
                SelectorSection(
                    title = "الصف",
                    emptyText = "أضف صفًا للبدء",
                    labels = state.grades.map { it.id to it.name },
                    selectedId = state.selectedGradeId,
                    onSelect = onSelectGrade,
                    onManage = { showGradeManager = true }
                )
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.selectedSubjectId != null &&
                        state.selectedGradeId != null &&
                        !state.isImporting,
                    onClick = {
                        pdfPicker.launch(arrayOf("application/pdf"))
                    }
                ) {
                    Text(
                        if (state.isImporting) {
                            "جارٍ حفظ المنهج..."
                        } else {
                            "إضافة منهج PDF"
                        }
                    )
                }

                if (state.isImporting) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "يتم حفظ نسخة داخل التطبيق لتعمل دون إنترنت.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Text(
                    text = "المناهج المحفوظة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.curricula.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "لا يوجد منهج محفوظ للمادة والصف المحددين.",
                            modifier = Modifier.padding(18.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = state.curricula,
                    key = { it.id }
                ) { curriculum ->
                    CurriculumCard(
                        curriculum = curriculum,
                        onOpen = {
                            val path = curriculum.localFileUri
                            if (!path.isNullOrBlank()) {
                                onOpenPdf(path, curriculum.title)
                            }
                        },
                        onRename = {
                            editingCurriculum = curriculum
                        },
                        onDelete = {
                            onDeleteCurriculum(curriculum.id)
                        }
                    )
                }
            }
        }
    }

    if (showSubjectManager) {
        EntityManagerDialog(
            title = "إدارة المواد",
            itemLabel = "المادة",
            items = state.subjects.map { it.id to it.name },
            onDismiss = { showSubjectManager = false },
            onAdd = onAddSubject,
            onRename = onRenameSubject,
            onDelete = onDeleteSubject
        )
    }

    if (showGradeManager) {
        EntityManagerDialog(
            title = "إدارة الصفوف",
            itemLabel = "الصف",
            items = state.grades.map { it.id to it.name },
            onDismiss = { showGradeManager = false },
            onAdd = onAddGrade,
            onRename = onRenameGrade,
            onDelete = onDeleteGrade
        )
    }

    editingCurriculum?.let { curriculum ->
        NameEditorDialog(
            title = "تعديل اسم المنهج",
            initialValue = curriculum.title,
            onDismiss = { editingCurriculum = null },
            onConfirm = { value ->
                onRenameCurriculum(curriculum.id, value)
                editingCurriculum = null
            }
        )
    }
}

@Composable
private fun SelectorSection(
    title: String,
    emptyText: String,
    labels: List<Pair<Long, String>>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onManage: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onManage) {
                Text("إدارة")
            }
        }

        if (labels.isEmpty()) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(labels, key = { it.first }) { item ->
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
private fun CurriculumCard(
    curriculum: CurriculumEntity,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = curriculum.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onOpen,
                    enabled = !curriculum.localFileUri.isNullOrBlank()
                ) {
                    Text("فتح")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onRename
                ) {
                    Text("تعديل")
                }

                TextButton(onClick = onDelete) {
                    Text("حذف")
                }
            }
        }
    }
}

@Composable
private fun EntityManagerDialog(
    title: String,
    itemLabel: String,
    items: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onRename: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    var newValue by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<Pair<Long, String>?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newValue,
                    onValueChange = { newValue = it },
                    singleLine = true,
                    label = { Text("اسم " + itemLabel) }
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = newValue.isNotBlank(),
                    onClick = {
                        onAdd(newValue)
                        newValue = ""
                    }
                ) {
                    Text("إضافة")
                }

                if (items.isEmpty()) {
                    Text(
                        text = "لا توجد بيانات بعد.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    items.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.second,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                TextButton(
                                    onClick = { editingItem = item }
                                ) {
                                    Text("تعديل")
                                }
                                TextButton(
                                    onClick = { onDelete(item.first) }
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

    editingItem?.let { item ->
        NameEditorDialog(
            title = "تعديل " + itemLabel,
            initialValue = item.second,
            onDismiss = { editingItem = null },
            onConfirm = { value ->
                onRename(item.first, value)
                editingItem = null
            }
        )
    }
}

@Composable
private fun NameEditorDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember(initialValue) {
        mutableStateOf(initialValue)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = { value = it },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = value.isNotBlank(),
                onClick = { onConfirm(value) }
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
