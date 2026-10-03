package com.hasan0525.hteacher

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File

class PdfImportTestProvider : ContentProvider() {
    override fun onCreate() = true
    override fun getType(uri: Uri) = "application/pdf"
    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor {
        val columns = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        return MatrixCursor(columns).also { result ->
            val row = Array<Any?>(columns.size) { index ->
                when (columns[index]) {
                    OpenableColumns.DISPLAY_NAME -> uri.lastPathSegment ?: "sample.pdf"
                    OpenableColumns.SIZE -> if (uri.lastPathSegment?.startsWith("oversized_") == true) 80L * 1024L * 1024L + 1L else 1024L
                    else -> null
                }
            }
            result.addRow(row)
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val path = File(requireNotNull(context).cacheDir, uri.lastPathSegment ?: "sample.pdf")
        if (uri.lastPathSegment?.startsWith("invalid_") == true) {
            path.writeText("This is not a PDF.")
            return ParcelFileDescriptor.open(path, ParcelFileDescriptor.MODE_READ_ONLY)
        }
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(400, 500, 1).create())
            val paint = android.graphics.Paint().apply { textSize = 13f }
            repeat(5) { index ->
                page.canvas.drawText("Real generated PDF educational content for exam creation",
                    38f, 75f + index * 28f, paint)
            }
            document.finishPage(page)
            path.outputStream().use { output -> document.writeTo(output) }
        } finally {
            document.close()
        }
        return ParcelFileDescriptor.open(path, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
}
