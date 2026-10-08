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
import com.hasan0525.hteacher.ai.LocalQuestionGenerator
import com.hasan0525.hteacher.domain.exam.Difficulty
import com.hasan0525.hteacher.domain.exam.ExamGenerationState
import com.hasan0525.hteacher.domain.exam.QuestionType

@Composable
fun ExamScreen() {
    MaterialTheme {
        var state by remember { mutableStateOf<ExamGenerationState>(ExamGenerationState.Idle) }
        var count by remember { mutableStateOf(10) }
        var selectedType by remember { mutableStateOf(QuestionType.TRUE_FALSE) }
        var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

        Text("المعلم H - توليد الاختبارات")
        Text("عدد الأسئلة: $count")
        Text("نوع السؤال: ${selectedType.label}")
        Text("الصعوبة: ${selectedDifficulty.label}")

        if (state is ExamGenerationState.Progress) {
            LinearProgressIndicator()
            Text("جاري التوليد ${(state as ExamGenerationState.Progress).value}%")
        }

        if (state is ExamGenerationState.Completed) {
            Text("تم إنشاء ${(state as ExamGenerationState.Completed).exam.questions.size} أسئلة")
        }

        Button(onClick = {
            state = ExamGenerationState.Progress(0)
            state = ExamGenerationState.Progress(50)

            val exam = LocalQuestionGenerator().generate(
                title = "اختبار تجريبي",
                subjectName = "المادة",
                count = count,
                type = selectedType,
                difficulty = selectedDifficulty
            )

            state = ExamGenerationState.Progress(100)
            state = ExamGenerationState.Completed(exam)
        }) {
            Text("توليد الأسئلة")
        }
    }
}
