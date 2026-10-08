package com.hasan0525.hteacher.domain.exam

sealed class ExamGenerationState {
    data object Idle : ExamGenerationState()
    data class Progress(val value: Int) : ExamGenerationState()
    data class Completed(val exam: GeneratedExam) : ExamGenerationState()
    data class Error(val message: String) : ExamGenerationState()
}
