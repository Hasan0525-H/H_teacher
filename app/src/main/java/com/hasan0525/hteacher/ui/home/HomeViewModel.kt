package com.hasan0525.hteacher.ui.home

import androidx.lifecycle.ViewModel

data class TeacherTool(val title: String, val description: String, val status: ToolStatus)
enum class ToolStatus { READY, COMING_SOON }
data class HomeUiState(val greeting: String = "مرحبًا بك في المعلم H", val tools: List<TeacherTool> = emptyList())

class HomeViewModel : ViewModel() {
    val uiState = HomeUiState(
        tools = listOf(
            TeacherTool("المناهج", "إضافة وتنظيم المناهج والملفات", ToolStatus.READY),
            TeacherTool("مولد الاختبارات", "إنشاء اختبارات ونماذج إجابة", ToolStatus.COMING_SOON),
            TeacherTool("ملف الإنجاز", "تنظيم الشواهد والتقارير المهنية", ToolStatus.COMING_SOON),
            TeacherTool("أدوات المعلم", "أدوات إضافية ستضاف تدريجيًا", ToolStatus.COMING_SOON)
        )
    )
}
