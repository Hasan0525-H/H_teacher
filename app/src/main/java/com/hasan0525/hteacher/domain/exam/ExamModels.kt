package com.hasan0525.hteacher.domain.exam

enum class QuestionType(
    val storageKey: String,
    val label: String
) {
    MULTIPLE_CHOICE("multiple_choice", "اختيار من متعدد"),
    TRUE_FALSE("true_false", "صح أو خطأ"),
    FILL_BLANK("fill_blank", "أكمل"),
    ESSAY("essay", "مقالي");

    companion object {
        fun fromStorage(value: String): QuestionType =
            entries.firstOrNull { it.storageKey == value } ?: ESSAY
    }
}

enum class Difficulty(
    val storageKey: String,
    val label: String
) {
    EASY("easy", "سهل"),
    MEDIUM("medium", "متوسط"),
    HARD("hard", "صعب");

    companion object {
        fun fromStorage(value: String): Difficulty =
            entries.firstOrNull { it.storageKey == value } ?: MEDIUM
    }
}

data class ExamQuestionItem(
    val number: Int,
    val questionText: String,
    val answerText: String?,
    val type: QuestionType,
    val difficulty: Difficulty,
    val mark: Double
)

data class GeneratedExam(
    val title: String,
    val subjectName: String,
    val curriculumTitle: String?,
    val totalMarks: Int,
    val questions: List<ExamQuestionItem>,
    val isTemplate: Boolean = false
)
