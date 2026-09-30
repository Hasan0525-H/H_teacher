package com.hasan0525.hteacher.domain.portfolio

enum class PortfolioCategory(
    val storageKey: String,
    val label: String
) {
    COURSES("courses", "الدورات والشهادات"),
    LESSON_PREPARATION("lesson_preparation", "تحضير الدروس"),
    REMEDIAL_PLANS("remedial_plans", "الخطط العلاجية"),
    WORKSHEETS("worksheets", "أوراق العمل"),
    ACTIVITIES("activities", "الأنشطة"),
    RESULTS_ANALYSIS("results_analysis", "تحليل النتائج"),
    PROFESSIONAL_EVIDENCE("professional_evidence", "الشواهد المهنية"),
    REPORTS("reports", "التقارير");

    companion object {
        fun fromStorage(value: String): PortfolioCategory =
            entries.firstOrNull { it.storageKey == value }
                ?: PROFESSIONAL_EVIDENCE
    }
}

data class PortfolioExportItem(
    val category: PortfolioCategory,
    val title: String,
    val description: String,
    val attachmentNames: List<String>
)
