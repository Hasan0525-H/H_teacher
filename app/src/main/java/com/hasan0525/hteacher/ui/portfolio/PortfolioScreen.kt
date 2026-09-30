package com.hasan0525.hteacher.ui.portfolio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.hasan0525.hteacher.data.repository.AppSettings
import com.hasan0525.hteacher.domain.portfolio.PortfolioCategory

@Composable
fun PortfolioRoute(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as HTeacherApplication
    val viewModel: PortfolioViewModel = viewModel(
        factory = PortfolioViewModelFactory(application)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PortfolioScreen(
        state = state,
        onBack = onBack,
        onSelectCategory = viewModel::selectCategory,
        onSaveProfile = viewModel::saveProfile,
        onAddItem = viewModel::addItem,
        onUpdateItem = viewModel::updateItem,
        onDeleteItem = viewModel::deleteItem,
        onAddAttachment = viewModel::addAttachment,
        onDeleteAttachment = viewModel::deleteAttachment,
        onExport = viewModel::exportPortfolio,
        onMessageShown = viewModel::clearMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioScreen(
    state: PortfolioUiState,
    onBack: () -> Unit,
    onSelectCategory: (PortfolioCategory?) -> Unit,
    onSaveProfile: (String, String, String, String) -> Unit,
    onAddItem: (PortfolioCategory, String, String) -> Unit,
    onUpdateItem: (Long, PortfolioCategory, String, String) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddAttachment: (Long, Uri) -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onExport: (Uri) -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showProfileEditor by remember { mutableStateOf(false) }
    var showNewItem by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<PortfolioItemUi?>(null) }
    var pendingAttachmentItemId by remember { mutableLongStateOf(0L) }

    val attachmentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && pendingAttachmentItemId > 0L) {
            onAddAttachment(pendingAttachmentItemId, uri)
        }
        pendingAttachmentItemId = 0L
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) onExport(uri)
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
                        text = "ملف الإنجاز",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("رجوع")
                    }
                },
                actions = {
                    TextButton(
                        enabled = !state.isExporting,
                        onClick = {
                            exportLauncher.launch("ملف الإنجاز.pdf")
                        }
                    ) {
                        Text("PDF")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ProfileCard(
                    profile = state.profile,
                    onEdit = { showProfileEditor = true }
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCategory == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("الكل") }
                        )
                    }

                    items(
                        items = PortfolioCategory.entries.toList(),
                        key = { it.storageKey }
                    ) { category ->
                        FilterChip(
                            selected = state.selectedCategory == category,
                            onClick = {
                                onSelectCategory(category)
                            },
                            label = { Text(category.label) }
                        )
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showNewItem = true }
                ) {
                    Text("إضافة عنصر لملف الإنجاز")
                }

                if (state.isExporting) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (state.items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "لا توجد عناصر في هذا التصنيف.",
                            modifier = Modifier.padding(18.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = state.items,
                    key = { it.item.id }
                ) { ui ->
                    PortfolioItemCard(
                        ui = ui,
                        onEdit = { editingItem = ui },
                        onDelete = { onDeleteItem(ui.item.id) },
                        onAddAttachment = {
                            pendingAttachmentItemId = ui.item.id
                            attachmentPicker.launch(arrayOf("*/*"))
                        },
                        onDeleteAttachment = onDeleteAttachment
                    )
                }
            }
        }
    }

    if (showProfileEditor) {
        ProfileEditorDialog(
            profile = state.profile,
            onDismiss = { showProfileEditor = false },
            onSave = { name, school, specialization, title ->
                onSaveProfile(name, school, specialization, title)
                showProfileEditor = false
            }
        )
    }

    if (showNewItem) {
        PortfolioItemEditorDialog(
            initialCategory = state.selectedCategory
                ?: PortfolioCategory.PROFESSIONAL_EVIDENCE,
            initialTitle = "",
            initialDescription = "",
            dialogTitle = "إضافة عنصر",
            onDismiss = { showNewItem = false },
            onSave = { category, title, description ->
                onAddItem(category, title, description)
                showNewItem = false
            }
        )
    }

    editingItem?.let { ui ->
        PortfolioItemEditorDialog(
            initialCategory = PortfolioCategory.fromStorage(
                ui.item.category
            ),
            initialTitle = ui.item.title,
            initialDescription = ui.item.description,
            dialogTitle = "تعديل العنصر",
            onDismiss = { editingItem = null },
            onSave = { category, title, description ->
                onUpdateItem(
                    ui.item.id,
                    category,
                    title,
                    description
                )
                editingItem = null
            }
        )
    }
}

@Composable
private fun ProfileCard(
    profile: AppSettings,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profile.teacherName.ifBlank {
                        "بيانات المعلم"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                TextButton(onClick = onEdit) {
                    Text("تعديل")
                }
            }

            listOf(
                profile.jobTitle,
                profile.specialization,
                profile.schoolName
            )
                .filter { it.isNotBlank() }
                .forEach { value ->
                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

            if (
                profile.teacherName.isBlank() &&
                profile.schoolName.isBlank() &&
                profile.specialization.isBlank() &&
                profile.jobTitle.isBlank()
            ) {
                Text(
                    text = "أضف بياناتك المهنية لتظهر في ملف الإنجاز.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun PortfolioItemCard(
    ui: PortfolioItemUi,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddAttachment: () -> Unit,
    onDeleteAttachment: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = PortfolioCategory
                    .fromStorage(ui.item.category)
                    .label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = ui.item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (ui.item.description.isNotBlank()) {
                Text(
                    text = ui.item.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (ui.attachments.isNotEmpty()) {
                HorizontalDivider()

                ui.attachments.forEach { attachment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = attachment.fileName,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        TextButton(
                            onClick = {
                                onDeleteAttachment(attachment.id)
                            }
                        ) {
                            Text("حذف")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onAddAttachment
                ) {
                    Text("إضافة مرفق")
                }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onEdit
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
private fun ProfileEditorDialog(
    profile: AppSettings,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember(profile) {
        mutableStateOf(profile.teacherName)
    }
    var school by remember(profile) {
        mutableStateOf(profile.schoolName)
    }
    var specialization by remember(profile) {
        mutableStateOf(profile.specialization)
    }
    var jobTitle by remember(profile) {
        mutableStateOf(profile.jobTitle)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("البيانات المهنية") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("اسم المعلم/ة") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    singleLine = true,
                    label = { Text("المسمى الوظيفي") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = specialization,
                    onValueChange = { specialization = it },
                    singleLine = true,
                    label = { Text("التخصص") }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = school,
                    onValueChange = { school = it },
                    singleLine = true,
                    label = { Text("المدرسة") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        name,
                        school,
                        specialization,
                        jobTitle
                    )
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
private fun PortfolioItemEditorDialog(
    initialCategory: PortfolioCategory,
    initialTitle: String,
    initialDescription: String,
    dialogTitle: String,
    onDismiss: () -> Unit,
    onSave: (PortfolioCategory, String, String) -> Unit
) {
    var category by remember(initialCategory) {
        mutableStateOf(initialCategory)
    }
    var title by remember(initialTitle) {
        mutableStateOf(initialTitle)
    }
    var description by remember(initialDescription) {
        mutableStateOf(initialDescription)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("العنوان") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف") },
                    minLines = 3
                )

                Text(
                    text = "التصنيف",
                    fontWeight = FontWeight.Bold
                )

                PortfolioCategory.entries.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item.label) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(category, title, description)
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
