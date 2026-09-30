package com.hasan0525.hteacher.ui.curriculum

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.files.CurriculumFileStore
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.CurriculumUnitEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.LessonEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity
import com.hasan0525.hteacher.data.repository.OfflineTeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CurriculumUiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val grades: List<GradeEntity> = emptyList(),
    val curricula: List<CurriculumEntity> = emptyList(),
    val units: List<CurriculumUnitEntity> = emptyList(),
    val lessons: List<LessonEntity> = emptyList(),
    val selectedSubjectId: Long? = null,
    val selectedGradeId: Long? = null,
    val isImporting: Boolean = false,
    val message: String? = null
)

private data class CurriculumCoreState(
    val subjects: List<SubjectEntity>,
    val grades: List<GradeEntity>,
    val curricula: List<CurriculumEntity>,
    val selectedSubjectId: Long?,
    val selectedGradeId: Long?
)

class CurriculumViewModel(
    private val repository: OfflineTeacherRepository,
    private val fileStore: CurriculumFileStore
) : ViewModel() {
    private val selectedSubjectId = MutableStateFlow<Long?>(null)
    private val selectedGradeId = MutableStateFlow<Long?>(null)
    private val isImporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val coreState = combine(
        repository.subjects,
        repository.grades,
        repository.curricula,
        selectedSubjectId,
        selectedGradeId
    ) { subjects, grades, curricula, subjectId, gradeId ->
        val effectiveSubjectId = subjectId
            ?.takeIf { id -> subjects.any { it.id == id } }
            ?: subjects.firstOrNull()?.id

        val effectiveGradeId = gradeId
            ?.takeIf { id -> grades.any { it.id == id } }
            ?: grades.firstOrNull()?.id

        CurriculumCoreState(
            subjects = subjects,
            grades = grades,
            curricula = curricula.filter { curriculum ->
                curriculum.subjectId == effectiveSubjectId &&
                    curriculum.gradeId == effectiveGradeId
            },
            selectedSubjectId = effectiveSubjectId,
            selectedGradeId = effectiveGradeId
        )
    }

    val uiState = combine(
        coreState,
        repository.curriculumUnits,
        repository.lessons,
        isImporting,
        message
    ) { core, units, lessons, importing, currentMessage ->
        CurriculumUiState(
            subjects = core.subjects,
            grades = core.grades,
            curricula = core.curricula,
            units = units,
            lessons = lessons,
            selectedSubjectId = core.selectedSubjectId,
            selectedGradeId = core.selectedGradeId,
            isImporting = importing,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CurriculumUiState()
    )

    fun selectSubject(id: Long) {
        selectedSubjectId.value = id
    }

    fun selectGrade(id: Long) {
        selectedGradeId.value = id
    }

    fun addSubject(name: String) = launchOperation(
        successMessage = "تمت إضافة المادة"
    ) {
        require(name.isNotBlank()) { "اكتب اسم المادة" }
        val id = repository.addSubject(name)
        selectedSubjectId.value = id
    }

    fun renameSubject(id: Long, name: String) = launchOperation(
        successMessage = "تم تعديل المادة"
    ) {
        require(name.isNotBlank()) { "اكتب اسم المادة" }
        val subject = uiState.value.subjects
            .firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.updateSubject(subject.copy(name = name.trim()))
    }

    fun deleteSubject(id: Long) = launchOperation(
        successMessage = "تم حذف المادة",
        failurePrefix = "تعذر حذف المادة. احذف المناهج المرتبطة بها أولًا"
    ) {
        val subject = uiState.value.subjects
            .firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.deleteSubject(subject)
    }

    fun addGrade(name: String) = launchOperation(
        successMessage = "تمت إضافة الصف"
    ) {
        require(name.isNotBlank()) { "اكتب اسم الصف" }
        val nextOrder = (uiState.value.grades.maxOfOrNull { it.sortOrder } ?: -1) + 1
        val id = repository.addGrade(name, nextOrder)
        selectedGradeId.value = id
    }

    fun renameGrade(id: Long, name: String) = launchOperation(
        successMessage = "تم تعديل الصف"
    ) {
        require(name.isNotBlank()) { "اكتب اسم الصف" }
        val grade = uiState.value.grades
            .firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.updateGrade(grade.copy(name = name.trim()))
    }

    fun deleteGrade(id: Long) = launchOperation(
        successMessage = "تم حذف الصف",
        failurePrefix = "تعذر حذف الصف. احذف المناهج المرتبطة به أولًا"
    ) {
        val grade = uiState.value.grades
            .firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.deleteGrade(grade)
    }

    fun importPdf(uri: Uri) {
        val state = uiState.value
        val subjectId = state.selectedSubjectId
        val gradeId = state.selectedGradeId

        if (subjectId == null || gradeId == null) {
            message.value = "أضف مادة وصفًا واخترهما أولًا"
            return
        }

        viewModelScope.launch {
            isImporting.value = true
            var copiedPath: String? = null

            try {
                val storedPdf = fileStore.importPdf(uri)
                copiedPath = storedPdf.absolutePath

                repository.addCurriculum(
                    subjectId = subjectId,
                    gradeId = gradeId,
                    title = storedPdf.title,
                    localFileUri = storedPdf.absolutePath
                )

                message.value = "تم حفظ المنهج على الجهاز"
            } catch (error: Throwable) {
                fileStore.delete(copiedPath)
                message.value = error.message ?: "تعذر استيراد ملف PDF"
            } finally {
                isImporting.value = false
            }
        }
    }

    fun renameCurriculum(id: Long, title: String) = launchOperation(
        successMessage = "تم تعديل اسم المنهج"
    ) {
        require(title.isNotBlank()) { "اكتب اسم المنهج" }
        val curriculum = uiState.value.curricula
            .firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.updateCurriculum(
            curriculum.copy(title = title.trim())
        )
    }

    fun deleteCurriculum(id: Long) = launchOperation(
        successMessage = "تم حذف المنهج"
    ) {
        val curriculum = uiState.value.curricula
            .firstOrNull { it.id == id }
            ?: return@launchOperation

        repository.deleteCurriculum(curriculum)
        fileStore.delete(curriculum.localFileUri)
    }

    fun addUnit(curriculumId: Long, title: String) = launchOperation(
        successMessage = "تمت إضافة الوحدة"
    ) {
        require(title.isNotBlank()) { "اكتب اسم الوحدة" }
        val nextOrder = uiState.value.units
            .filter { it.curriculumId == curriculumId }
            .maxOfOrNull { it.sortOrder }
            ?.plus(1) ?: 0
        repository.addCurriculumUnit(
            curriculumId = curriculumId,
            title = title,
            sortOrder = nextOrder
        )
    }

    fun renameUnit(id: Long, title: String) = launchOperation(
        successMessage = "تم تعديل الوحدة"
    ) {
        require(title.isNotBlank()) { "اكتب اسم الوحدة" }
        val unit = uiState.value.units.firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.updateCurriculumUnit(
            unit.copy(title = title.trim())
        )
    }

    fun deleteUnit(id: Long) = launchOperation(
        successMessage = "تم حذف الوحدة"
    ) {
        val unit = uiState.value.units.firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.deleteCurriculumUnit(unit)
    }

    fun addLesson(
        unitId: Long,
        title: String,
        pageStart: String,
        pageEnd: String,
        textContent: String
    ) = launchOperation(
        successMessage = "تمت إضافة الدرس"
    ) {
        require(title.isNotBlank()) { "اكتب اسم الدرس" }
        val start = pageStart.toIntOrNull()
        val end = pageEnd.toIntOrNull()
        require(start == null || start > 0) { "بداية الصفحات غير صحيحة" }
        require(end == null || end > 0) { "نهاية الصفحات غير صحيحة" }
        require(
            start == null || end == null || end >= start
        ) { "نهاية الصفحات يجب أن تكون بعد البداية" }

        val nextOrder = uiState.value.lessons
            .filter { it.unitId == unitId }
            .maxOfOrNull { it.sortOrder }
            ?.plus(1) ?: 0

        repository.addLesson(
            unitId = unitId,
            title = title,
            pageStart = start,
            pageEnd = end,
            textContent = textContent,
            sortOrder = nextOrder
        )
    }

    fun renameLesson(id: Long, title: String) = launchOperation(
        successMessage = "تم تعديل الدرس"
    ) {
        require(title.isNotBlank()) { "اكتب اسم الدرس" }
        val lesson = uiState.value.lessons.firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.updateLesson(
            lesson.copy(title = title.trim())
        )
    }

    fun deleteLesson(id: Long) = launchOperation(
        successMessage = "تم حذف الدرس"
    ) {
        val lesson = uiState.value.lessons.firstOrNull { it.id == id }
            ?: return@launchOperation
        repository.deleteLesson(lesson)
    }

    fun clearMessage() {
        message.value = null
    }

    private fun launchOperation(
        successMessage: String,
        failurePrefix: String? = null,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
                message.value = successMessage
            } catch (error: Throwable) {
                message.value = failurePrefix
                    ?: error.message
                    ?: "حدث خطأ غير متوقع"
            }
        }
    }
}

class CurriculumViewModelFactory(
    private val application: HTeacherApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurriculumViewModel::class.java)) {
            return CurriculumViewModel(
                repository = application.container.teacherRepository,
                fileStore = CurriculumFileStore(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
