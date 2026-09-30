package com.hasan0525.hteacher.data.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

data class RenderedPdfPage(
    val bitmap: Bitmap,
    val pageIndex: Int,
    val pageCount: Int
)

object PdfRendererEngine {
    suspend fun renderPage(
        filePath: String,
        requestedPage: Int,
        targetWidth: Int = 1600
    ): RenderedPdfPage = withContext(Dispatchers.IO) {
        val file = File(filePath)
        require(file.exists()) { "ملف المنهج غير موجود على الجهاز" }

        ParcelFileDescriptor.open(
            file,
            ParcelFileDescriptor.MODE_READ_ONLY
        ).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                require(renderer.pageCount > 0) { "ملف PDF لا يحتوي صفحات" }

                val pageIndex = requestedPage.coerceIn(
                    0,
                    renderer.pageCount - 1
                )

                renderer.openPage(pageIndex).use { page ->
                    val safeWidth = targetWidth.coerceAtLeast(600)
                    val ratio = page.height.toFloat() / page.width.toFloat()
                    val height = (safeWidth * ratio)
                        .roundToInt()
                        .coerceAtLeast(1)

                    val bitmap = Bitmap.createBitmap(
                        safeWidth,
                        height,
                        Bitmap.Config.ARGB_8888
                    )

                    page.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    RenderedPdfPage(
                        bitmap = bitmap,
                        pageIndex = pageIndex,
                        pageCount = renderer.pageCount
                    )
                }
            }
        }
    }
}
