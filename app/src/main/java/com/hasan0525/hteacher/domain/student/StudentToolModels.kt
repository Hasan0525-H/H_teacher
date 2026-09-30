package com.hasan0525.hteacher.domain.student

enum class AttendanceStatus(
    val storageKey: String,
    val label: String
) {
    PRESENT("present", "حاضر"),
    ABSENT("absent", "غائب"),
    LATE("late", "متأخر"),
    EXCUSED("excused", "مستأذن");

    companion object {
        fun fromStorage(value: String): AttendanceStatus? =
            entries.firstOrNull { it.storageKey == value }
    }
}

enum class TeacherToolsSection(
    val label: String
) {
    STUDENTS("الطلاب"),
    ATTENDANCE("الحضور"),
    GRADES("الدرجات"),
    REPORTS("التقارير")
}
