package com.hasan0525.hteacher.ui.exam

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.ai.AiExamQuestionRequest
import com.hasan0525.hteacher.data.ai.AiQuestionService
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.CurriculumUnitEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.LessonEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity
import com.hasan0525.hteacher.data.pdf.PdfExamExporter
import com.hasan0525.hteacher.data.pdf.PdfLessonTextExtractor
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
    val isAiGenerating: Boolean = false,
    val generationProgress: Int = 0,
    val aiConfigured: Boolean = false,
    val indexedLessonCount: Int = 0,
    val matchingQuestionCount: Int = 0,
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

private data class CurriculumIndexState(
    val units: List<CurriculumUnitEntity> = emptyList(),
    val lessons: List<LessonEntity> = emptyList()
)

private data class ExamAuxState(
    val generatedExam: GeneratedExam?,
    val isExporting: Boolean,
    val isAiGenerating: Boolean,
    val generationProgress: Int,
    val message: String?
)

class ExamGeneratorViewModel(
    private val repository: OfflineTeacherRepository,
    private val exporter: PdfExamExporter,
    private val aiQuestionService: AiQuestionService,
    private val pdfTextExtractor: PdfLessonTextExtractor? = null
) : ViewModel() {
    private val config = MutableStateFlow(ExamConfig())
    private val generatedExam = MutableStateFlow<GeneratedExam?>(null)
    private val isExporting = MutableStateFlow(false)
    private val isAiGenerating = MutableStateFlow(false)
    private val generationProgress = MutableStateFlow(0)
    private val message = MutableStateFlow<String?>(null)

    private val indexState = combine(
        repository.curriculumUnits,
        repository.lessons
    ) { units, lessons ->
        CurriculumIndexState(
            units = units,
            lessons = lessons
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CurriculumIndexState()
    )

    private val auxState = combine(
        generatedExam,
        isExporting,
        isAiGenerating,
        generationProgress,
        message
    ) { exam, exporting, aiGenerating, progress, currentMessage ->
        ExamAuxState(
            generatedExam = exam,
            isExporting = exporting,
            isAiGenerating = aiGenerating,
            generationProgress = progress,
            message = currentMessage
        )
    }

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
        indexState,
        auxState
    ) { core, index, aux ->
        val selectedCurriculumId = core.effectiveCurriculumId
        val unitIds = index.units
            .filter { it.curriculumId == selectedCurriculumId }
            .map { it.id }
            .toSet()

        val indexedLessonCount = index.lessons.count {
            it.unitId in unitIds && it.textContent.isNotBlank()
        }

        ExamGeneratorUiState(
            subjects = core.subjects,
            curricula = core.curricula,
            questions = core.questions,
            selectedSubjectId = core.effectiveSubjectId,
            selectedCurriculumId = selectedCurriculumId,
            selectedTypes = core.config.selectedTypes,
            selectedDifficulty = core.config.selectedDifficulty,
            questionCount = core.config.questionCount,
            totalMarks = core.config.totalMarks,
            title = core.config.title,
            generatedExam = aux.generatedExam,
            isExporting = aux.isExporting,
            isAiGenerating = aux.isAiGenerating,
            generationProgress = aux.generationProgress,
            aiConfigured = aiQuestionService.isConfigured,
            indexedLessonCount = indexedLessonCount,
            matchingQuestionCount = core.questions.count { question ->
                QuestionType.fromStorage(question.questionType) in core.config.selectedTypes &&
                    (core.config.selectedDifficulty == null ||
                        Difficulty.fromStorage(question.difficulty) == core.config.selectedDifficulty)
            },
            message = aux.message
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

    fun generateExam(): Boolean {
        val state = uiState.value
        val subject = state.subjects
            .firstOrNull { it.id == state.selectedSubjectId }

        if (subject == null) {
            message.value = "أضف مادة واخترها أولًا"
            return false
        }

        val requestedCount = state.questionCount.toIntOrNull()
        val marks = state.totalMarks.toIntOrNull()

        if (requestedCount == null || requestedCount !in 1..100) {
            message.value = "عدد الأسئلة يجب أن يكون بين 1 و100"
            return false
        }

        if (marks == null || marks !in 1..500) {
            message.value = "الدرجة الكلية يجب أن تكون بين 1 و500"
            return false
        }

        val candidates = state.questions.filter { question ->
            QuestionType.fromStorage(question.questionType) in state.selectedTypes &&
                (
                    state.selectedDifficulty == null ||
                        Difficulty.fromStorage(question.difficulty) == state.selectedDifficulty
                    )
        }

        if (candidates.isEmpty()) {
            message.value = if (state.questions.isEmpty()) {
                "لا توجد أسئلة لهذه المادة. أضف سؤالًا أو أنشئ أسئلة بالذكاء الاصطناعي."
            } else {
                "لا توجد أسئلة مطابقة. غيّر النوع أو المستوى، أو أضف سؤالًا."
            }
            return false
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
        return true
    }

    /** Printable template; blank items are intentionally not generated questions. */
    fun generateBlankExam(): Boolean {
        val state = uiState.value
        val subject = state.subjects.firstOrNull { it.id == state.selectedSubjectId }
        if (subject == null) {
            message.value = "اختر المادة أولاً"
            return false
        }
        val count = state.questionCount.toIntOrNull()
        val marks = state.totalMarks.toIntOrNull()
        if (count == null || count !in 1..100 || marks == null || marks !in 1..500) {
            message.value = "تحقق من عدد الأسئلة والدرجة الكلية"
            return false
        }
        generatedExam.value = GeneratedExam(
            title = state.title.trim().ifBlank { "اختبار" },
            subjectName = subject.name,
            curriculumTitle = state.curricula.firstOrNull { it.id == state.selectedCurriculumId }?.title,
            totalMarks = marks,
            isTemplate = true,
            questions = (1..count).map { index ->
                ExamQuestionItem(
                    number = index,
                    questionText = "........................................................................",
                    answerText = null,
                    type = QuestionType.ESSAY,
                    difficulty = Difficulty.MEDIUM,
                    mark = marks.toDouble() / count.toDouble()
                )
            }
        )
        message.value = "تم إعداد نموذج فارغ للطباعة"
        return true
    }

    fun generateAiQuestionsFromText(text: String) = generateAiQuestions(text.trim().take(50_000))

    fun generateAiQuestionsFromPdf() = generateAiQuestions(null, fromPdf = true)

    /** Generates directly from the curriculum selected in the source step. */
    fun generateAiQuestionsFromSelectedSource() {
        val state = uiState.value
        val curriculum = state.curricula.firstOrNull { it.id == state.selectedCurriculumId }
        when {
            curriculum?.localFileUri?.isNotBlank() == true -> generateAiQuestionsFromPdf()
            state.selectedCurriculumId != null && state.indexedLessonCount > 0 -> generateAiQuestions()
            else -> message.value = "اختر منهجًا يحتوي على ملف أو نص دروس"
        }
    }

    fun generateAiQuestions() = generateAiQuestions(null)

    private fun generateAiQuestions(sourceText: String?, fromPdf: Boolean = false) {
        if (isAiGenerating.value) return
        val state = uiState.value
        val subject = state.subjects.firstOrNull {
            it.id == state.selectedSubjectId
        }
        val curriculum = state.curricula.firstOrNull {
            it.id == state.selectedCurriculumId
        }

        if (!aiQuestionService.isConfigured) {
            message.value = "بوابة الذكاء الاصطناعي غير مفعلة في هذا البناء"
            return
        }

        if (subject == null || (curriculum == null && sourceText.isNullOrBlank())) {
            message.value = "اختر المادة ومنهجًا أو الصق نص الدرس"
            return
        }

        val requestedCount = state.questionCount.toIntOrNull()
        if (requestedCount == null || requestedCount !in 1..30) {
            message.value = "لتوليد AI اختر من 1 إلى 30 سؤالًا"
            return
        }

        val customSource = sourceText?.takeIf { it.isNotBlank() }
        val index = indexState.value
        val units = index.units.filter { it.curriculumId == curriculum?.id }
        val unitById = units.associateBy { it.id }
        val unitIds = unitById.keys
        val indexedLessons = index.lessons
            .filter { it.unitId in unitIds && it.textContent.isNotBlank() }
            .sortedWith(compareBy<LessonEntity>(
                { unitById[it.unitId]?.sortOrder ?: 0 }, { it.sortOrder }
            ))

        if (!fromPdf && indexedLessons.isEmpty() && customSource == null) {
            message.value = "الصق نص الدرس أو أضف نصوص الدروس في المكتبة"
            return
        }
        if (fromPdf && curriculum?.localFileUri.isNullOrBlank()) {
            message.value = "المنهج لا يحتوي ملف PDF محفوظًا"
            return
        }

        viewModelScope.launch {
            isAiGenerating.value = true
            generationProgress.value = 5
            try {
                val lessonContext = if (fromPdf) {
                    val extractor = requireNotNull(pdfTextExtractor) {
                        "استخراج نص PDF غير متاح في هذه النسخة"
                    }
                    generationProgress.value = 22
                    extractor.extract(requireNotNull(curriculum?.localFileUri))
                } else {
                    customSource ?: indexedLessons.joinToString(separator = "\n\n") { lesson ->
                        "الوحدة: ${unitById[lesson.unitId]?.title.orEmpty()}\n" +
                            "الدرس: ${lesson.title}\n${lesson.textContent}"
                    }.take(50_000)
                }
                generationProgress.value = 38
                val generated = aiQuestionService.generateQuestions(
                    AiExamQuestionRequest(
                        subjectName = subject.name,
                        curriculumTitle = curriculum?.title ?: "نص الدرس",
                        lessonContext = lessonContext,
                        count = requestedCount,
                        types = state.selectedTypes.map {
                            it.storageKey
                        },
                        difficulty = state.selectedDifficulty
                            ?.storageKey
                            ?: "mixed"
                    )
                )

                generationProgress.value = 78
                generated.forEach { item ->
                    repository.addQuestion(
                        QuestionEntity(
                            subjectId = subject.id,
                            curriculumId = curriculum?.id,
                            questionType = item.type,
                            difficulty = item.difficulty,
                            questionText = item.question,
                            answerText = item.answer
                                .trim()
                                .ifBlank { null }
                        )
                    )
                }

                generationProgress.value = 94
                // Generation is the end-to-end action: persist the questions and open
                // the reviewable paper immediately, regardless of whether the source
                // was pasted text, indexed lessons, or a PDF.
                val marks = state.totalMarks.toIntOrNull()?.takeIf { it in 1..500 }
                    ?: generated.size
                generatedExam.value = GeneratedExam(
                    title = state.title.trim().ifBlank { "اختبار" },
                    subjectName = subject.name,
                    curriculumTitle = curriculum?.title,
                    totalMarks = marks,
                    questions = generated.mapIndexed { index, item ->
                        ExamQuestionItem(
                            number = index + 1,
                            questionText = item.question,
                            answerText = item.answer.ifBlank { null },
                            type = QuestionType.fromStorage(item.type),
                            difficulty = Difficulty.fromStorage(item.difficulty),
                            mark = marks.toDouble() / generated.size
                        )
                    }
                )
                generationProgress.value = 100
                message.value = "تم توليد ${generated.size} سؤالًا. راجع الأسئلة قبل الطباعة."
            } catch (error: Throwable) {
                generationProgress.value = 0
                message.value = error.message
                    ?: "تعذر توليد الأسئلة بالذكاء الاصطناعي"
            } finally {
                isAiGenerating.value = false
            }
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
                exporter = PdfExamExporter(application),
                aiQuestionService = application.container.aiQuestionService,
                pdfTextExtractor = PdfLessonTextExtractor(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
