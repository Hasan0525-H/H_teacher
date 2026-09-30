package com.hasan0525.hteacher.data.ai

import com.hasan0525.hteacher.data.repository.AppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AiExamQuestionRequest(
    val subjectName: String,
    val curriculumTitle: String,
    val lessonContext: String,
    val count: Int,
    val types: List<String>,
    val difficulty: String
)

data class AiGeneratedQuestion(
    val type: String,
    val difficulty: String,
    val question: String,
    val answer: String
)

class AiQuestionService(
    private val settingsRepository: AppSettingsRepository,
    gatewayUrl: String
) {
    private val baseUrl = gatewayUrl.trim().trimEnd('/')

    val isConfigured: Boolean
        get() = baseUrl.startsWith("https://")

    suspend fun generateQuestions(
        request: AiExamQuestionRequest
    ): List<AiGeneratedQuestion> = withContext(Dispatchers.IO) {
        require(isConfigured) {
            "بوابة الذكاء الاصطناعي غير مفعلة"
        }

        val installId = settingsRepository.getOrCreateInstallId()
        val prompt = buildExamPrompt(request)

        val payload = JSONObject()
            .put("task", "exam_questions")
            .put("prompt", prompt)
            .put("maxOutputTokens", 4096)

        val connection = (
            URL(baseUrl + "/v1/generate").openConnection()
                as HttpURLConnection
            ).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 70_000
            doOutput = true
            setRequestProperty(
                "Content-Type",
                "application/json; charset=utf-8"
            )
            setRequestProperty(
                "Accept",
                "application/json"
            )
            setRequestProperty(
                "X-HTeacher-Install",
                installId
            )
        }

        try {
            connection.outputStream.use { output ->
                output.write(
                    payload.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val status = connection.responseCode
            val stream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = stream
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                .orEmpty()

            if (status !in 200..299) {
                val errorMessage = runCatching {
                    JSONObject(body).optString("error")
                }.getOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?: "فشل اتصال AI: " + status

                error(errorMessage)
            }

            val response = JSONObject(body)
            val text = response.optString("text")
            require(text.isNotBlank()) {
                "رد الذكاء الاصطناعي فارغ"
            }

            parseQuestions(text)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildExamPrompt(
        request: AiExamQuestionRequest
    ): String = buildString {
        appendLine(
            "أنت مساعد تعليمي للمعلم السعودي. " +
                "أنشئ أسئلة فقط من النص المرفق دون اختراع معلومات خارجه."
        )
        appendLine("المادة: " + request.subjectName)
        appendLine("المنهج: " + request.curriculumTitle)
        appendLine("عدد الأسئلة: " + request.count)
        appendLine(
            "الأنواع المسموحة: " +
                request.types.joinToString(",")
        )
        appendLine("الصعوبة: " + request.difficulty)
        appendLine()
        appendLine(
            "أعد JSON فقط بالشكل التالي دون Markdown:"
        )
        appendLine(
            "{\"questions\":[{\"type\":\"multiple_choice\"," +
                "\"difficulty\":\"medium\"," +
                "\"question\":\"...\",\"answer\":\"...\"}]}"
        )
        appendLine(
            "قيم type يجب أن تكون واحدة من: " +
                "multiple_choice,true_false,fill_blank,essay."
        )
        appendLine(
            "قيم difficulty يجب أن تكون واحدة من: easy,medium,hard."
        )
        appendLine(
            "في الاختيار من متعدد ضع الخيارات داخل نص السؤال " +
                "بصيغة أ) ب) ج) د)، والإجابة تحتوي الخيار الصحيح."
        )
        appendLine()
        appendLine("نصوص الدروس:")
        append(request.lessonContext)
    }

    private fun parseQuestions(raw: String): List<AiGeneratedQuestion> {
        val cleaned = raw
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')
        require(start >= 0 && end > start) {
            "تعذر قراءة تنسيق أسئلة AI"
        }

        val root = JSONObject(
            cleaned.substring(start, end + 1)
        )
        val questions = root.optJSONArray("questions")
            ?: JSONArray()

        val allowedTypes = setOf(
            "multiple_choice",
            "true_false",
            "fill_blank",
            "essay"
        )
        val allowedDifficulty = setOf(
            "easy",
            "medium",
            "hard"
        )

        val result = mutableListOf<AiGeneratedQuestion>()

        for (index in 0 until questions.length()) {
            val item = questions.optJSONObject(index) ?: continue
            val type = item.optString("type")
            val difficulty = item.optString("difficulty")
            val question = item.optString("question").trim()
            val answer = item.optString("answer").trim()

            if (
                type in allowedTypes &&
                difficulty in allowedDifficulty &&
                question.isNotBlank()
            ) {
                result += AiGeneratedQuestion(
                    type = type,
                    difficulty = difficulty,
                    question = question,
                    answer = answer
                )
            }
        }

        require(result.isNotEmpty()) {
            "لم يرجع AI أسئلة صالحة"
        }

        return result.take(30)
    }
}
