package com.hasan0525.hteacher.ui.tools

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.AttendanceEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.GradeRecordEntity
import com.hasan0525.hteacher.data.local.entity.StudentEntity
import com.hasan0525.hteacher.data.pdf.PdfStudentReportExporter
import com.hasan0525.hteacher.data.repository.OfflineTeacherRepository
import com.hasan0525.hteacher.domain.student.AttendanceStatus
import com.hasan0525.hteacher.domain.student.TeacherToolsSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TeacherToolsUiState(
    val grades: List<GradeEntity> = emptyList(),
    val students: List<StudentEntity> = emptyList(),
    val attendance: List<AttendanceEntity> = emptyList(),
    val gradeRecords: List<GradeRecordEntity> = emptyList(),
    val section: TeacherToolsSection = TeacherToolsSection.STUDENTS,
    val dateEpochDay: Long = LocalDate.now().toEpochDay(),
    val selectedStudentId: Long? = null,
    val isExporting: Boolean = false,
    val message: String? = null
)

private data class TeacherToolsData(
    val grades: List<GradeEntity>,
    val students: List<StudentEntity>,
    val attendance: List<AttendanceEntity>,
    val gradeRecords: List<GradeRecordEntity>
)

private data class TeacherToolsControls(
    val section: TeacherToolsSection,
    val dateEpochDay: Long,
    val selectedStudentId: Long?
)

class TeacherToolsViewModel(
    private val repository: OfflineTeacherRepository,
    private val exporter: PdfStudentReportExporter
) : ViewModel() {
    private val section = MutableStateFlow(TeacherToolsSection.STUDENTS)
    private val dateEpochDay = MutableStateFlow(LocalDate.now().toEpochDay())
    private val selectedStudentId = MutableStateFlow<Long?>(null)
    private val isExporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val data = combine(
        repository.grades,
        repository.students,
        repository.attendance,
        repository.gradeRecords
    ) { grades, students, attendance, gradeRecords ->
        TeacherToolsData(
            grades = grades,
            students = students,
            attendance = attendance,
            gradeRecords = gradeRecords
        )
    }

    private val controls = combine(
        section,
        dateEpochDay,
        selectedStudentId
    ) { currentSection, currentDate, studentId ->
        TeacherToolsControls(
            section = currentSection,
            dateEpochDay = currentDate,
            selectedStudentId = studentId
        )
    }

    val uiState = combine(
        data,
        controls,
        isExporting,
        message
    ) { currentData, currentControls, exporting, currentMessage ->
        val effectiveStudentId = currentControls.selectedStudentId
            ?.takeIf { id ->
                currentData.students.any { it.id == id }
            }
            ?: currentData.students.firstOrNull()?.id

        TeacherToolsUiState(
            grades = currentData.grades,
            students = currentData.students,
            attendance = currentData.attendance,
            gradeRecords = currentData.gradeRecords,
            section = currentControls.section,
            dateEpochDay = currentControls.dateEpochDay,
            selectedStudentId = effectiveStudentId,
            isExporting = exporting,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TeacherToolsUiState()
    )

    fun selectSection(value: TeacherToolsSection) {
        section.value = value
    }

    fun previousDay() {
        dateEpochDay.value -= 1L
    }

    fun nextDay() {
        dateEpochDay.value += 1L
    }

    fun selectStudent(id: Long) {
        selectedStudentId.value = id
    }

    fun addStudent(
        name: String,
        studentNumber: String,
        gradeId: Long?
    ) {
        viewModelScope.launch {
            try {
                require(name.isNotBlank()) { "اكتب اسم الطالب" }

                val id = repository.addStudent(
                    StudentEntity(
                        name = name.trim(),
                        gradeId = gradeId,
                        studentNumber = studentNumber.trim()
                    )
                )

                selectedStudentId.value = id
                message.value = "تمت إضافة الطالب"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر إضافة الطالب"
            }
        }
    }

    fun deleteStudent(id: Long) {
        val student = uiState.value.students
            .firstOrNull { it.id == id }
            ?: return

        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                message.value = "تم حذف الطالب"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حذف الطالب"
            }
        }
    }

    fun setAttendance(
        studentId: Long,
        status: AttendanceStatus
    ) {
        val state = uiState.value
        val existing = state.attendance.firstOrNull {
            it.studentId == studentId &&
                it.dateEpochDay == state.dateEpochDay
        }

        viewModelScope.launch {
            try {
                repository.setAttendance(
                    AttendanceEntity(
                        id = existing?.id ?: 0,
                        studentId = studentId,
                        dateEpochDay = state.dateEpochDay,
                        status = status.storageKey,
                        note = existing?.note.orEmpty(),
                        createdAt = existing?.createdAt
                            ?: System.currentTimeMillis()
                    )
                )
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حفظ الحضور"
            }
        }
    }

    fun addGradeRecord(
        studentId: Long,
        title: String,
        scoreText: String,
        maxScoreText: String
    ) {
        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "اكتب اسم التقييم" }

                val score = scoreText.toDoubleOrNull()
                    ?: error("الدرجة غير صحيحة")
                val maxScore = maxScoreText.toDoubleOrNull()
                    ?: error("الدرجة الكلية غير صحيحة")

                require(maxScore > 0.0) {
                    "الدرجة الكلية يجب أن تكون أكبر من صفر"
                }
                require(score >= 0.0 && score <= maxScore) {
                    "درجة الطالب يجب أن تكون بين صفر والدرجة الكلية"
                }

                repository.addGradeRecord(
                    GradeRecordEntity(
                        studentId = studentId,
                        title = title.trim(),
                        score = score,
                        maxScore = maxScore
                    )
                )

                message.value = "تم حفظ الدرجة"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حفظ الدرجة"
            }
        }
    }

    fun deleteGradeRecord(id: Long) {
        val record = uiState.value.gradeRecords
            .firstOrNull { it.id == id }
            ?: return

        viewModelScope.launch {
            try {
                repository.deleteGradeRecord(record)
                message.value = "تم حذف الدرجة"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حذف الدرجة"
            }
        }
    }

    fun exportReport(uri: Uri) {
        val state = uiState.value

        viewModelScope.launch {
            isExporting.value = true

            try {
                exporter.export(
                    uri = uri,
                    students = state.students,
                    grades = state.grades,
                    attendance = state.attendance,
                    gradeRecords = state.gradeRecords
                )
                message.value = "تم حفظ تقرير الطلاب PDF"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر إنشاء التقرير"
            } finally {
                isExporting.value = false
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }
}

class TeacherToolsViewModelFactory(
    private val application: HTeacherApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TeacherToolsViewModel::class.java)) {
            return TeacherToolsViewModel(
                repository = application.container.teacherRepository,
                exporter = PdfStudentReportExporter(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
