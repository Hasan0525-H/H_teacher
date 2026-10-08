package com.hasan0525.hteacher.ai

import com.hasan0525.hteacher.domain.exam.GeneratedExam
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.QuestionType

interface QuestionGenerator {
    suspend fun generate(
        title: String,
        subjectName: String,
        count: Int,
        type: QuestionType,
        difficulty: Difficulty
    ): GeneratedExam
}
