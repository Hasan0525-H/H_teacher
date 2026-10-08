package com.hasan0525.hteacher.ai

import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamGenerator
import com.hasan0525.hteacher.domain.exam.GeneratedExam
import com.hasan0525.hteacher.domain.exam.QuestionType

class LocalQuestionGenerator : QuestionGenerator {
    override suspend fun generate(
        title: String,
        subjectName: String,
        count: Int,
        type: QuestionType,
        difficulty: Difficulty
    ): GeneratedExam {
        return ExamGenerator().generate(
            title = title,
            subjectName = subjectName,
            count = count,
            type = type,
            difficulty = difficulty
        )
    }
}
