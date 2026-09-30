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
import com.hasan0525.hteacher.domain.exam.GeneratedExam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfExamExporter(
    private val context: Context
) {
    suspend fun export(
        uri: Uri,
        exam: GeneratedExam,
        includeAnswers: Boolean
    ) = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        try {
            val writer = ExamPdfWriter(
                document = document,
                runningTitle = if (includeAnswers) {
                    exam.title + " - نموذج الإجابة"
                } else {
                    exam.title
                }
            )

            writer.start()

            writer.drawText(
                text = exam.title,
                paint = writer.titlePaint,
                spacingAfter = 10f
            )

            writer.drawText(
                text = "المادة: " + exam.subjectName +
                    (exam.curriculumTitle?.let { "    المنهج: " + it } ?: ""),
                paint = writer.metaPaint,
                spacingAfter = 8f
            )

            if (!includeAnswers) {
                writer.drawText(
                    text = "اسم الطالب: ____________________    الصف: ______________    التاريخ: ______________",
                    paint = writer.metaPaint,
                    spacingAfter = 8f
                )
            }

            writer.drawText(
                text = "الدرجة الكلية: " + exam.totalMarks,
                paint = writer.metaPaint,
                spacingAfter = 16f
            )

            exam.questions.forEach { question ->
                val heading = "السؤال " + question.number +
                    " - " + question.type.label +
                    " (" + formatMark(question.mark) + " درجة)"

                writer.drawText(
                    text = heading,
                    paint = writer.questionTitlePaint,
                    spacingAfter = 6f
                )

                writer.drawText(
                    text = question.questionText,
                    paint = writer.bodyPaint,
                    spacingAfter = if (includeAnswers) 6f else 18f
                )

                if (includeAnswers) {
                    writer.drawText(
                        text = "الإجابة: " +
                            (question.answerText?.takeIf { it.isNotBlank() } ?: "غير محفوظة"),
                        paint = writer.answerPaint,
                        spacingAfter = 14f
                    )
                }

                writer.drawRule()
            }

            writer.finish()

            val output = context.contentResolver.openOutputStream(uri, "w")
                ?: error("تعذر إنشاء ملف PDF")

            output.use { stream ->
                document.writeTo(stream)
            }
        } finally {
            document.close()
        }
    }

    private fun formatMark(mark: Double): String =
        if (mark % 1.0 == 0.0) {
            mark.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", mark)
        }
}

private class ExamPdfWriter(
    private val document: PdfDocument,
    private val runningTitle: String
) {
    companion object {
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
        private const val MARGIN = 42f
        private const val FOOTER_SPACE = 34f
    }

    val titlePaint = textPaint(
        size = 20f,
        bold = true
    )

    val questionTitlePaint = textPaint(
        size = 14f,
        bold = true
    )

    val bodyPaint = textPaint(
        size = 13f,
        bold = false
    )

    val answerPaint = textPaint(
        size = 13f,
        bold = true
    )

    val metaPaint = textPaint(
        size = 11.5f,
        bold = false
    )

    private val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 9f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = 1f
    }

    private var pageNumber = 0
    private var currentPage: PdfDocument.Page? = null
    private var y = MARGIN

    private val contentWidth: Int
        get() = (PAGE_WIDTH - (MARGIN * 2)).toInt()

    fun start() {
        startNewPage()
    }

    fun drawText(
        text: String,
        paint: TextPaint,
        spacingAfter: Float
    ) {
        val layout = buildLayout(text, paint)
        ensureSpace(layout.height + spacingAfter)

        val canvas = requireNotNull(currentPage).canvas
        canvas.save()
        canvas.translate(MARGIN, y)
        layout.draw(canvas)
        canvas.restore()

        y += layout.height + spacingAfter
    }

    fun drawRule() {
        ensureSpace(20f)
        val canvas = requireNotNull(currentPage).canvas
        canvas.drawLine(
            MARGIN,
            y,
            PAGE_WIDTH - MARGIN,
            y,
            rulePaint
        )
        y += 16f
    }

    fun finish() {
        finishCurrentPage()
    }

    private fun startNewPage() {
        finishCurrentPage()

        pageNumber += 1
        val pageInfo = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH,
            PAGE_HEIGHT,
            pageNumber
        ).create()

        currentPage = document.startPage(pageInfo)
        y = MARGIN

        val header = buildLayout(
            text = runningTitle,
            paint = metaPaint
        )

        val canvas = requireNotNull(currentPage).canvas
        canvas.save()
        canvas.translate(MARGIN, y)
        header.draw(canvas)
        canvas.restore()

        y += header.height + 12f
        canvas.drawLine(
            MARGIN,
            y,
            PAGE_WIDTH - MARGIN,
            y,
            rulePaint
        )
        y += 16f
    }

    private fun finishCurrentPage() {
        val page = currentPage ?: return
        page.canvas.drawText(
            "صفحة " + pageNumber,
            PAGE_WIDTH / 2f,
            PAGE_HEIGHT - 16f,
            footerPaint
        )
        document.finishPage(page)
        currentPage = null
    }

    private fun ensureSpace(requiredHeight: Float) {
        if (currentPage == null) {
            startNewPage()
            return
        }

        val bottomLimit = PAGE_HEIGHT - MARGIN - FOOTER_SPACE
        if (y + requiredHeight > bottomLimit) {
            startNewPage()
        }
    }

    private fun buildLayout(
        text: String,
        paint: TextPaint
    ): StaticLayout {
        val safeText = text.ifBlank { " " }

        return StaticLayout.Builder.obtain(
            safeText,
            0,
            safeText.length,
            paint,
            contentWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .setLineSpacing(2f, 1.08f)
            .build()
    }
}

private fun textPaint(
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
