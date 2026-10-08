package com.hasan0525.hteacher.domain.exam

class ExamGenerator {
    fun generate(
        title: String,
        subjectName: String,
        count: Int,
        type: QuestionType,
        difficulty: Difficulty
    ): GeneratedExam {
        val questions = (1..count).map { index ->
            ExamQuestionItem(
                number = index,
                questionText = "سؤال $index",
                answerText = null,
                type = type,
                difficulty = difficulty,
                mark = 1.0
            )
        }

        return GeneratedExam(
            title = title,
            subjectName = subjectName,
            curriculumTitle = null,
            totalMarks = count,
            questions = questions
        )
    }
}
