package com.hasan0525.hteacher.data.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import com.hasan0525.hteacher.data.local.entity.AttendanceEntity
import com.hasan0525.hteacher.data.local.entity.GradeRecordEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.StudentEntity
import com.hasan0525.hteacher.domain.student.AttendanceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class PdfStudentReportExporter(
    private val context: Context
) {
    suspend fun export(
        uri: Uri,
        students: List<StudentEntity>,
        grades: List<GradeEntity>,
        attendance: List<AttendanceEntity>,
        gradeRecords: List<GradeRecordEntity>
    ) = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        try {
            val writer = StudentReportWriter(document)
            writer.start()

            writer.drawText(
                "تقرير الطلاب",
                writer.titlePaint,
                14f
            )

            writer.drawText(
                "عدد الطلاب: " + students.size,
                writer.metaPaint,
                16f
            )

            students.forEachIndexed { index, student ->
                val gradeName = grades
                    .firstOrNull { it.id == student.gradeId }
                    ?.name
                    .orEmpty()

                writer.drawText(
                    (index + 1).toString() + ". " + student.name,
                    writer.itemTitlePaint,
                    5f
                )

                if (gradeName.isNotBlank()) {
                    writer.drawText(
                        "الصف: " + gradeName,
                        writer.metaPaint,
                        5f
                    )
                }

                val studentAttendance = attendance.filter {
                    it.studentId == student.id
                }

                val present = studentAttendance.count {
                    AttendanceStatus.fromStorage(it.status) ==
                        AttendanceStatus.PRESENT
                }
                val absent = studentAttendance.count {
                    AttendanceStatus.fromStorage(it.status) ==
                        AttendanceStatus.ABSENT
                }
                val late = studentAttendance.count {
                    AttendanceStatus.fromStorage(it.status) ==
                        AttendanceStatus.LATE
                }
                val excused = studentAttendance.count {
                    AttendanceStatus.fromStorage(it.status) ==
                        AttendanceStatus.EXCUSED
                }

                writer.drawText(
                    "الحضور: حاضر " + present +
                        " | غائب " + absent +
                        " | متأخر " + late +
                        " | مستأذن " + excused,
                    writer.bodyPaint,
                    5f
                )

                val records = gradeRecords.filter {
                    it.studentId == student.id
                }

                if (records.isEmpty()) {
                    writer.drawText(
                        "الدرجات: لا توجد درجات مسجلة",
                        writer.bodyPaint,
                        8f
                    )
                } else {
                    val earned = records.sumOf { it.score }
                    val possible = records.sumOf { it.maxScore }
                    val percentage = if (possible > 0.0) {
                        earned / possible * 100.0
                    } else {
                        0.0
                    }

                    writer.drawText(
                        "متوسط الدرجات: " +
                            String.format(
                                Locale.US,
                                "%.1f%%",
                                percentage
                            ),
                        writer.bodyPaint,
                        8f
                    )
                }

                writer.drawRule()
            }

            writer.finish()

            val output = context.contentResolver.openOutputStream(uri, "w")
                ?: error("تعذر إنشاء التقرير")

            output.use { stream ->
                document.writeTo(stream)
            }
        } finally {
            document.close()
        }
    }
}

private class StudentReportWriter(
    private val document: PdfDocument
) {
    companion object {
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
        private const val MARGIN = 42f
        private const val FOOTER_SPACE = 34f
    }

    val titlePaint = reportPaint(21f, true)
    val itemTitlePaint = reportPaint(14f, true)
    val bodyPaint = reportPaint(12.5f, false)
    val metaPaint = reportPaint(11f, false)

    private val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = 1f
    }

    private val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 9f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private var page: PdfDocument.Page? = null
    private var pageNumber = 0
    private var y = MARGIN

    private val width: Int
        get() = (PAGE_WIDTH - MARGIN * 2).toInt()

    fun start() {
        newPage()
    }

    fun drawText(
        text: String,
        paint: TextPaint,
        spacingAfter: Float
    ) {
        val layout = createLayout(text, paint)
        ensureSpace(layout.height + spacingAfter)

        val canvas = requireNotNull(page).canvas
        canvas.save()
        canvas.translate(MARGIN, y)
        layout.draw(canvas)
        canvas.restore()

        y += layout.height + spacingAfter
    }

    fun drawRule() {
        ensureSpace(18f)
        requireNotNull(page).canvas.drawLine(
            MARGIN,
            y,
            PAGE_WIDTH - MARGIN,
            y,
            rulePaint
        )
        y += 14f
    }

    fun finish() {
        finishPage()
    }

    private fun newPage() {
        finishPage()
        pageNumber += 1
        page = document.startPage(
            PdfDocument.PageInfo.Builder(
                PAGE_WIDTH,
                PAGE_HEIGHT,
                pageNumber
            ).create()
        )
        y = MARGIN
    }

    private fun finishPage() {
        val current = page ?: return
        current.canvas.drawText(
            "صفحة " + pageNumber,
            PAGE_WIDTH / 2f,
            PAGE_HEIGHT - 16f,
            footerPaint
        )
        document.finishPage(current)
        page = null
    }

    private fun ensureSpace(height: Float) {
        if (page == null) newPage()

        if (y + height > PAGE_HEIGHT - MARGIN - FOOTER_SPACE) {
            newPage()
        }
    }

    private fun createLayout(
        text: String,
        paint: TextPaint
    ): StaticLayout {
        val safe = text.ifBlank { " " }

        return StaticLayout.Builder.obtain(
            safe,
            0,
            safe.length,
            paint,
            width
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .setLineSpacing(2f, 1.08f)
            .build()
    }
}

private fun reportPaint(
    size: Float,
    bold: Boolean
): TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    textSize = size
    typeface = Typeface.create(
        Typeface.SANS_SERIF,
        if (bold) Typeface.BOLD else Typeface.NORMAL
    )
}
