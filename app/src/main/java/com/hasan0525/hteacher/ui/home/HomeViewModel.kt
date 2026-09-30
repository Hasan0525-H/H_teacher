package com.hasan0525.hteacher.ui.home

import androidx.lifecycle.ViewModel

enum class ToolId {
    CURRICULA,
    EXAMS,
    PORTFOLIO,
    MORE
}

data class TeacherTool(
    val id: ToolId,
    val title: String,
    val description: String,
    val status: ToolStatus
)

enum class ToolStatus {
    READY,
    COMING_SOON
}

data class HomeUiState(
    val greeting: String = "مرحبًا بك في المعلم H",
    val tools: List<TeacherTool> = emptyList()
)

class HomeViewModel : ViewModel() {
    val uiState = HomeUiState(
        tools = listOf(
            TeacherTool(
                id = ToolId.CURRICULA,
                title = "المناهج",
                description = "إضافة وتنظيم المناهج والملفات",
                status = ToolStatus.READY
            ),
            TeacherTool(
                id = ToolId.EXAMS,
                title = "مولد الاختبارات",
                description = "إنشاء اختبارات ونماذج إجابة",
                status = ToolStatus.COMING_SOON
            ),
            TeacherTool(
                id = ToolId.PORTFOLIO,
                title = "ملف الإنجاز",
                description = "تنظيم الشواهد والتقارير المهنية",
                status = ToolStatus.COMING_SOON
            ),
            TeacherTool(
                id = ToolId.MORE,
                title = "أدوات المعلم",
                description = "أدوات إضافية ستضاف تدريجيًا",
                status = ToolStatus.COMING_SOON
            )
        )
    )
}
