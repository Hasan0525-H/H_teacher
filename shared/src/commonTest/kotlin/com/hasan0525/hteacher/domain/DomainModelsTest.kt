package com.hasan0525.hteacher.domain

import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.QuestionType
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainModelsTest {
    @Test
    fun questionTypeUsesStableStorageKeys() {
        assertEquals(QuestionType.TRUE_FALSE, QuestionType.fromStorage("true_false"))
    }

    @Test
    fun difficultyFallsBackToMedium() {
        assertEquals(Difficulty.MEDIUM, Difficulty.fromStorage("unknown"))
    }
}
