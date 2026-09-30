package com.hasan0525.hteacher.ui.portfolio

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.files.PortfolioFileStore
import com.hasan0525.hteacher.data.local.entity.PortfolioAttachmentEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioItemEntity
import com.hasan0525.hteacher.data.pdf.PdfPortfolioExporter
import com.hasan0525.hteacher.data.repository.AppSettings
import com.hasan0525.hteacher.data.repository.AppSettingsRepository
import com.hasan0525.hteacher.data.repository.OfflineTeacherRepository
import com.hasan0525.hteacher.domain.portfolio.PortfolioCategory
import com.hasan0525.hteacher.domain.portfolio.PortfolioExportItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PortfolioItemUi(
    val item: PortfolioItemEntity,
    val attachments: List<PortfolioAttachmentEntity>
)

data class PortfolioUiState(
    val profile: AppSettings = AppSettings(),
    val selectedCategory: PortfolioCategory? = null,
    val items: List<PortfolioItemUi> = emptyList(),
    val allItems: List<PortfolioItemUi> = emptyList(),
    val isExporting: Boolean = false,
    val message: String? = null
)

private data class PortfolioDataState(
    val profile: AppSettings,
    val items: List<PortfolioItemUi>
)

class PortfolioViewModel(
    private val repository: OfflineTeacherRepository,
    private val settingsRepository: AppSettingsRepository,
    private val fileStore: PortfolioFileStore,
    private val exporter: PdfPortfolioExporter
) : ViewModel() {
    private val selectedCategory = MutableStateFlow<PortfolioCategory?>(null)
    private val isExporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val dataState = combine(
        repository.portfolioItems,
        repository.portfolioAttachments,
        settingsRepository.settings
    ) { items, attachments, profile ->
        PortfolioDataState(
            profile = profile,
            items = items.map { item ->
                PortfolioItemUi(
                    item = item,
                    attachments = attachments.filter {
                        it.portfolioItemId == item.id
                    }
                )
            }
        )
    }

    val uiState = combine(
        dataState,
        selectedCategory,
        isExporting,
        message
    ) { data, category, exporting, currentMessage ->
        PortfolioUiState(
            profile = data.profile,
            selectedCategory = category,
            items = data.items.filter { ui ->
                category == null ||
                    PortfolioCategory.fromStorage(ui.item.category) == category
            },
            allItems = data.items,
            isExporting = exporting,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PortfolioUiState()
    )

    fun selectCategory(category: PortfolioCategory?) {
        selectedCategory.value = category
    }

    fun saveProfile(
        teacherName: String,
        schoolName: String,
        specialization: String,
        jobTitle: String
    ) {
        viewModelScope.launch {
            try {
                settingsRepository.updateTeacherProfile(
                    teacherName = teacherName,
                    schoolName = schoolName,
                    specialization = specialization,
                    jobTitle = jobTitle
                )
                message.value = "تم حفظ بيانات المعلم"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حفظ البيانات"
            }
        }
    }

    fun addItem(
        category: PortfolioCategory,
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "اكتب عنوان العنصر" }

                repository.addPortfolioItem(
                    PortfolioItemEntity(
                        category = category.storageKey,
                        title = title.trim(),
                        description = description.trim()
                    )
                )

                selectedCategory.value = category
                message.value = "تمت إضافة العنصر"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر إضافة العنصر"
            }
        }
    }

    fun updateItem(
        id: Long,
        category: PortfolioCategory,
        title: String,
        description: String
    ) {
        val current = uiState.value.allItems
            .firstOrNull { it.item.id == id }
            ?.item
            ?: return

        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "اكتب عنوان العنصر" }
                repository.updatePortfolioItem(
                    current.copy(
                        category = category.storageKey,
                        title = title.trim(),
                        description = description.trim()
                    )
                )
                message.value = "تم تعديل العنصر"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر تعديل العنصر"
            }
        }
    }

    fun deleteItem(id: Long) {
        val current = uiState.value.allItems
            .firstOrNull { it.item.id == id }
            ?: return

        viewModelScope.launch {
            try {
                repository.deletePortfolioItem(current.item)
                current.attachments.forEach {
                    fileStore.delete(it.localPath)
                }
                message.value = "تم حذف العنصر"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حذف العنصر"
            }
        }
    }

    fun addAttachment(
        itemId: Long,
        uri: Uri
    ) {
        viewModelScope.launch {
            var copiedPath: String? = null

            try {
                val stored = fileStore.importFile(uri)
                copiedPath = stored.absolutePath

                repository.addPortfolioAttachment(
                    PortfolioAttachmentEntity(
                        portfolioItemId = itemId,
                        fileName = stored.fileName,
                        mimeType = stored.mimeType,
                        localPath = stored.absolutePath
                    )
                )

                message.value = "تم حفظ المرفق"
            } catch (error: Throwable) {
                fileStore.delete(copiedPath)
                message.value = error.message ?: "تعذر حفظ المرفق"
            }
        }
    }

    fun deleteAttachment(id: Long) {
        val attachment = uiState.value.allItems
            .flatMap { it.attachments }
            .firstOrNull { it.id == id }
            ?: return

        viewModelScope.launch {
            try {
                repository.deletePortfolioAttachment(attachment)
                fileStore.delete(attachment.localPath)
                message.value = "تم حذف المرفق"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حذف المرفق"
            }
        }
    }

    fun exportPortfolio(uri: Uri) {
        val state = uiState.value

        viewModelScope.launch {
            isExporting.value = true

            try {
                exporter.export(
                    uri = uri,
                    profile = state.profile,
                    items = state.allItems.map { ui ->
                        PortfolioExportItem(
                            category = PortfolioCategory.fromStorage(
                                ui.item.category
                            ),
                            title = ui.item.title,
                            description = ui.item.description,
                            attachmentNames = ui.attachments.map {
                                it.fileName
                            }
                        )
                    }
                )

                message.value = "تم حفظ ملف الإنجاز PDF"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر تصدير ملف الإنجاز"
            } finally {
                isExporting.value = false
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }
}

class PortfolioViewModelFactory(
    private val application: HTeacherApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(
                repository = application.container.teacherRepository,
                settingsRepository = application.container.settingsRepository,
                fileStore = PortfolioFileStore(application),
                exporter = PdfPortfolioExporter(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
