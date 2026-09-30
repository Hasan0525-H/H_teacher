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
import com.hasan0525.hteacher.data.repository.AppSettings
import com.hasan0525.hteacher.domain.portfolio.PortfolioExportItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfPortfolioExporter(
    private val context: Context
) {
    suspend fun export(
        uri: Uri,
        profile: AppSettings,
        items: List<PortfolioExportItem>
    ) = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        try {
            val writer = PortfolioPdfWriter(document)
            writer.start()

            writer.drawText(
                "ملف الإنجاز المهني",
                writer.titlePaint,
                12f
            )

            val profileLines = buildList {
                if (profile.teacherName.isNotBlank()) {
                    add("المعلم/ة: " + profile.teacherName)
                }
                if (profile.jobTitle.isNotBlank()) {
                    add("المسمى الوظيفي: " + profile.jobTitle)
                }
                if (profile.specialization.isNotBlank()) {
                    add("التخصص: " + profile.specialization)
                }
                if (profile.schoolName.isNotBlank()) {
                    add("المدرسة: " + profile.schoolName)
                }
            }

            if (profileLines.isNotEmpty()) {
                writer.drawText(
                    profileLines.joinToString("\n"),
                    writer.metaPaint,
                    18f
                )
            }

            if (items.isEmpty()) {
                writer.drawText(
                    "لا توجد عناصر محفوظة في ملف الإنجاز.",
                    writer.bodyPaint,
                    12f
                )
            } else {
                val grouped = items.groupBy { it.category }

                grouped.forEach { entry ->
                    writer.drawText(
                        entry.key.label,
                        writer.sectionPaint,
                        10f
                    )

                    entry.value.forEachIndexed { index, item ->
                        writer.drawText(
                            (index + 1).toString() + ". " + item.title,
                            writer.itemTitlePaint,
                            5f
                        )

                        if (item.description.isNotBlank()) {
                            writer.drawText(
                                item.description,
                                writer.bodyPaint,
                                5f
                            )
                        }

                        if (item.attachmentNames.isNotEmpty()) {
                            writer.drawText(
                                "المرفقات: " +
                                    item.attachmentNames.joinToString("، "),
                                writer.metaPaint,
                                8f
                            )
                        }

                        writer.drawRule()
                    }
                }
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
}

private class PortfolioPdfWriter(
    private val document: PdfDocument
) {
    companion object {
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
        private const val MARGIN = 42f
        private const val FOOTER_SPACE = 34f
    }

    val titlePaint = portfolioTextPaint(21f, true)
    val sectionPaint = portfolioTextPaint(16f, true)
    val itemTitlePaint = portfolioTextPaint(13.5f, true)
    val bodyPaint = portfolioTextPaint(12.5f, false)
    val metaPaint = portfolioTextPaint(11f, false)

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

    private var currentPage: PdfDocument.Page? = null
    private var pageNumber = 0
    private var y = MARGIN

    private val contentWidth: Int
        get() = (PAGE_WIDTH - MARGIN * 2).toInt()

    fun start() {
        newPage()
    }

    fun drawText(
        text: String,
        paint: TextPaint,
        spacingAfter: Float
    ) {
        val layout = layout(text, paint)
        ensureSpace(layout.height + spacingAfter)

        val canvas = requireNotNull(currentPage).canvas
        canvas.save()
        canvas.translate(MARGIN, y)
        layout.draw(canvas)
        canvas.restore()

        y += layout.height + spacingAfter
    }

    fun drawRule() {
        ensureSpace(18f)
        requireNotNull(currentPage).canvas.drawLine(
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

        val info = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH,
            PAGE_HEIGHT,
            pageNumber
        ).create()

        currentPage = document.startPage(info)
        y = MARGIN
    }

    private fun finishPage() {
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

    private fun ensureSpace(height: Float) {
        if (currentPage == null) newPage()

        val maxY = PAGE_HEIGHT - MARGIN - FOOTER_SPACE
        if (y + height > maxY) {
            newPage()
        }
    }

    private fun layout(
        text: String,
        paint: TextPaint
    ): StaticLayout {
        val safe = text.ifBlank { " " }

        return StaticLayout.Builder.obtain(
            safe,
            0,
            safe.length,
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

private fun portfolioTextPaint(
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
