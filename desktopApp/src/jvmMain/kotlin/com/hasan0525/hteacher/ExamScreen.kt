package com.hasan0525.hteacher

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamGenerator
import com.hasan0525.hteacher.domain.exam.QuestionType

@Composable
fun ExamScreen() {
    MaterialTheme {
        val exam = ExamGenerator().generate(
            title = "اختبار تجريبي",
            subjectName = "المادة",
            count = 10,
            type = QuestionType.TRUE_FALSE,
            difficulty = Difficulty.MEDIUM
        )

        Text("عدد الأسئلة: ${exam.questions.size}")
        Button(onClick = {}) {
            Text("توليد الأسئلة")
        }
    }
}
