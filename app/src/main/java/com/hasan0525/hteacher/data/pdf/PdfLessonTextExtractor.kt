package com.hasan0525.hteacher.data.pdf

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Reads embedded text locally; the PDF itself is never uploaded to the AI service.
 * Bound extracted text and page count to keep large curricula responsive.
 * Image-only/scanned PDFs require OCR, which is intentionally not simulated here.
 */
class PdfLessonTextExtractor(private val context: Context) {
    suspend fun extract(filePath: String, maxChars: Int = 35_000): String =
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            require(file.isFile && file.length() > 0L) { "الكتاب غير موجود على الهاتف" }
            PDFBoxResourceLoader.init(context.applicationContext)
            PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly()).use { document ->
                val content = StringBuilder()
                val stripper = PDFTextStripper().apply { sortByPosition = true }
                val maxPages = minOf(document.numberOfPages, 24)
                for (page in 1..maxPages) {
                    currentCoroutineContext().ensureActive()
                    stripper.startPage = page
                    stripper.endPage = page
                    val text = stripper.getText(document).trim()
                    if (text.isNotEmpty()) {
                        content.append("صفحة ").append(page).append(":\n")
                            .append(text.take(maxChars - content.length)).append("\n\n")
                    }
                    if (content.length >= maxChars - 100) break
                }
                content.toString().trim().also {
                    require(it.length >= 80) {
                        "تعذر استخراج نص كافٍ من الكتاب. إذا كان PDF صورًا ممسوحة، الصق نص الدرس."
                    }
                }
            }
        }
}
