package com.hasan0525.hteacher

import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamGenerationState
import com.hasan0525.hteacher.domain.exam.ExamGenerator
import com.hasan0525.hteacher.domain.exam.QuestionType

@Composable
fun ExamScreen() {
    MaterialTheme {
        var state by remember { mutableStateOf<ExamGenerationState>(ExamGenerationState.Idle) }

        Text("المعلم H - توليد الاختبارات")

        if (state is ExamGenerationState.Progress) {
            LinearProgressIndicator()
            Text("جاري التوليد ${(state as ExamGenerationState.Progress).value}%")
        }

        if (state is ExamGenerationState.Completed) {
            Text("تم إنشاء ${(state as ExamGenerationState.Completed).exam.questions.size} أسئلة")
        }

        Button(onClick = {
            state = ExamGenerationState.Progress(50)
            val exam = ExamGenerator().generate(
                title = "اختبار تجريبي",
                subjectName = "المادة",
                count = 10,
                type = QuestionType.TRUE_FALSE,
                difficulty = Difficulty.MEDIUM
            )
            state = ExamGenerationState.Completed(exam)
        }) {
            Text("توليد الأسئلة")
        }
    }
}
