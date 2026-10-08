package com.hasan0525.hteacher.domain

import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamGenerator
import com.hasan0525.hteacher.domain.exam.QuestionType
import kotlin.test.Test
import kotlin.test.assertEquals

class ExamGeneratorTest {
    @Test
    fun generatesRequestedQuestionCount() {
        val exam = ExamGenerator().generate(
            title = "اختبار",
            subjectName = "رياضيات",
            count = 10,
            type = QuestionType.TRUE_FALSE,
            difficulty = Difficulty.EASY
        )

        assertEquals(10, exam.questions.size)
    }
}
