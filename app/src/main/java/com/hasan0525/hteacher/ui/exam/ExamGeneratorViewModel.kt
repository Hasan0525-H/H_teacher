package com.hasan0525.hteacher.ui.exam

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity
import com.hasan0525.hteacher.data.pdf.PdfExamExporter
import com.hasan0525.hteacher.data.repository.OfflineTeacherRepository
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamQuestionItem
import com.hasan0525.hteacher.domain.exam.GeneratedExam
import com.hasan0525.hteacher.domain.exam.QuestionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class ExamConfig(
    val selectedSubjectId: Long? = null,
    val selectedCurriculumId: Long? = null,
    val selectedTypes: Set<QuestionType> = QuestionType.entries.toSet(),
    val selectedDifficulty: Difficulty? = null,
    val questionCount: String = "10",
    val totalMarks: String = "20",
    val title: String = "اختبار"
)

data class ExamGeneratorUiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val curricula: List<CurriculumEntity> = emptyList(),
    val questions: List<QuestionEntity> = emptyList(),
    val selectedSubjectId: Long? = null,
    val selectedCurriculumId: Long? = null,
    val selectedTypes: Set<QuestionType> = QuestionType.entries.toSet(),
    val selectedDifficulty: Difficulty? = null,
    val questionCount: String = "10",
    val totalMarks: String = "20",
    val title: String = "اختبار",
    val generatedExam: GeneratedExam? = null,
    val isExporting: Boolean = false,
    val message: String? = null
)

private data class ExamCoreState(
    val subjects: List<SubjectEntity>,
    val curricula: List<CurriculumEntity>,
    val questions: List<QuestionEntity>,
    val config: ExamConfig,
    val effectiveSubjectId: Long?,
    val effectiveCurriculumId: Long?
)

class ExamGeneratorViewModel(
    private val repository: OfflineTeacherRepository,
    private val exporter: PdfExamExporter
) : ViewModel() {
    private val config = MutableStateFlow(ExamConfig())
    private val generatedExam = MutableStateFlow<GeneratedExam?>(null)
    private val isExporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val coreState = combine(
        repository.subjects,
        repository.curricula,
        repository.questions,
        config
    ) { subjects, curricula, questions, currentConfig ->
        val effectiveSubjectId = currentConfig.selectedSubjectId
            ?.takeIf { id -> subjects.any { it.id == id } }
            ?: subjects.firstOrNull()?.id

        val subjectCurricula = curricula.filter {
            it.subjectId == effectiveSubjectId
        }

        val effectiveCurriculumId = currentConfig.selectedCurriculumId
            ?.takeIf { id -> subjectCurricula.any { it.id == id } }

        val availableQuestions = questions.filter { question ->
            question.subjectId == effectiveSubjectId &&
                (
                    effectiveCurriculumId == null ||
                        question.curriculumId == null ||
                        question.curriculumId == effectiveCurriculumId
                    )
        }

        ExamCoreState(
            subjects = subjects,
            curricula = subjectCurricula,
            questions = availableQuestions,
            config = currentConfig,
            effectiveSubjectId = effectiveSubjectId,
            effectiveCurriculumId = effectiveCurriculumId
        )
    }

    val uiState = combine(
        coreState,
        generatedExam,
        isExporting,
        message
    ) { core, exam, exporting, currentMessage ->
        ExamGeneratorUiState(
            subjects = core.subjects,
            curricula = core.curricula,
            questions = core.questions,
            selectedSubjectId = core.effectiveSubjectId,
            selectedCurriculumId = core.effectiveCurriculumId,
            selectedTypes = core.config.selectedTypes,
            selectedDifficulty = core.config.selectedDifficulty,
            questionCount = core.config.questionCount,
            totalMarks = core.config.totalMarks,
            title = core.config.title,
            generatedExam = exam,
            isExporting = exporting,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExamGeneratorUiState()
    )

    fun selectSubject(id: Long) {
        updateConfig {
            it.copy(
                selectedSubjectId = id,
                selectedCurriculumId = null
            )
        }
    }

    fun selectCurriculum(id: Long?) {
        updateConfig {
            it.copy(selectedCurriculumId = id)
        }
    }

    fun toggleType(type: QuestionType) {
        updateConfig { current ->
            val types = current.selectedTypes.toMutableSet()
            if (type in types) {
                if (types.size > 1) types.remove(type)
            } else {
                types.add(type)
            }
            current.copy(selectedTypes = types)
        }
    }

    fun selectDifficulty(difficulty: Difficulty?) {
        updateConfig {
            it.copy(selectedDifficulty = difficulty)
        }
    }

    fun updateQuestionCount(value: String) {
        val normalized = value.filter { it.isDigit() }.take(3)
        updateConfig { it.copy(questionCount = normalized) }
    }

    fun updateTotalMarks(value: String) {
        val normalized = value.filter { it.isDigit() }.take(3)
        updateConfig { it.copy(totalMarks = normalized) }
    }

    fun updateTitle(value: String) {
        updateConfig { it.copy(title = value.take(80)) }
    }

    fun addQuestion(
        text: String,
        answer: String,
        type: QuestionType,
        difficulty: Difficulty
    ) {
        val state = uiState.value
        val subjectId = state.selectedSubjectId

        if (subjectId == null) {
            message.value = "أضف مادة أولًا"
            return
        }

        viewModelScope.launch {
            try {
                require(text.isNotBlank()) { "اكتب نص السؤال" }

                repository.addQuestion(
                    QuestionEntity(
                        subjectId = subjectId,
                        curriculumId = state.selectedCurriculumId,
                        questionType = type.storageKey,
                        difficulty = difficulty.storageKey,
                        questionText = text.trim(),
                        answerText = answer.trim().ifBlank { null }
                    )
                )

                message.value = "تمت إضافة السؤال"
                generatedExam.value = null
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر إضافة السؤال"
            }
        }
    }

    fun deleteQuestion(id: Long) {
        val question = uiState.value.questions
            .firstOrNull { it.id == id }
            ?: return

        viewModelScope.launch {
            try {
                repository.deleteQuestion(question)
                generatedExam.value = null
                message.value = "تم حذف السؤال"
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر حذف السؤال"
            }
        }
    }

    fun generateExam() {
        val state = uiState.value
        val subject = state.subjects
            .firstOrNull { it.id == state.selectedSubjectId }

        if (subject == null) {
            message.value = "أضف مادة واخترها أولًا"
            return
        }

        val requestedCount = state.questionCount.toIntOrNull()
        val marks = state.totalMarks.toIntOrNull()

        if (requestedCount == null || requestedCount !in 1..100) {
            message.value = "عدد الأسئلة يجب أن يكون بين 1 و100"
            return
        }

        if (marks == null || marks !in 1..500) {
            message.value = "الدرجة الكلية يجب أن تكون بين 1 و500"
            return
        }

        val candidates = state.questions.filter { question ->
            QuestionType.fromStorage(question.questionType) in state.selectedTypes &&
                (
                    state.selectedDifficulty == null ||
                        Difficulty.fromStorage(question.difficulty) == state.selectedDifficulty
                    )
        }

        if (candidates.isEmpty()) {
            message.value = "لا توجد أسئلة مطابقة للإعدادات الحالية"
            return
        }

        val selected = selectBalancedQuestions(
            candidates = candidates,
            types = state.selectedTypes.toList(),
            limit = requestedCount
        )

        val markPerQuestion = marks.toDouble() / selected.size.toDouble()

        val examQuestions = selected.mapIndexed { index, question ->
            ExamQuestionItem(
                number = index + 1,
                questionText = question.questionText,
                answerText = question.answerText,
                type = QuestionType.fromStorage(question.questionType),
                difficulty = Difficulty.fromStorage(question.difficulty),
                mark = markPerQuestion
            )
        }

        val curriculumTitle = state.curricula
            .firstOrNull { it.id == state.selectedCurriculumId }
            ?.title

        generatedExam.value = GeneratedExam(
            title = state.title.trim().ifBlank { "اختبار" },
            subjectName = subject.name,
            curriculumTitle = curriculumTitle,
            totalMarks = marks,
            questions = examQuestions
        )

        message.value = if (selected.size < requestedCount) {
            "تم إنشاء الاختبار بالأسئلة المتوفرة فقط: " + selected.size
        } else {
            "تم إنشاء الاختبار"
        }
    }

    fun exportExam(
        uri: Uri,
        includeAnswers: Boolean
    ) {
        val exam = generatedExam.value

        if (exam == null) {
            message.value = "أنشئ الاختبار أولًا"
            return
        }

        viewModelScope.launch {
            isExporting.value = true
            try {
                exporter.export(
                    uri = uri,
                    exam = exam,
                    includeAnswers = includeAnswers
                )
                message.value = if (includeAnswers) {
                    "تم حفظ نموذج الإجابة PDF"
                } else {
                    "تم حفظ الاختبار PDF"
                }
            } catch (error: Throwable) {
                message.value = error.message ?: "تعذر إنشاء ملف PDF"
            } finally {
                isExporting.value = false
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }

    private fun updateConfig(
        transform: (ExamConfig) -> ExamConfig
    ) {
        config.value = transform(config.value)
        generatedExam.value = null
    }

    private fun selectBalancedQuestions(
        candidates: List<QuestionEntity>,
        types: List<QuestionType>,
        limit: Int
    ): List<QuestionEntity> {
        val buckets = types.associateWith { type ->
            candidates
                .filter {
                    QuestionType.fromStorage(it.questionType) == type
                }
                .shuffled()
                .toMutableList()
        }

        val selected = mutableListOf<QuestionEntity>()

        while (selected.size < limit) {
            var addedThisRound = false

            types.forEach { type ->
                if (selected.size >= limit) return@forEach

                val bucket = buckets[type] ?: return@forEach
                if (bucket.isNotEmpty()) {
                    selected += bucket.removeAt(0)
                    addedThisRound = true
                }
            }

            if (!addedThisRound) break
        }

        return selected
    }
}

class ExamGeneratorViewModelFactory(
    private val application: HTeacherApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ExamGeneratorViewModel::class.java)) {
            return ExamGeneratorViewModel(
                repository = application.container.teacherRepository,
                exporter = PdfExamExporter(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
