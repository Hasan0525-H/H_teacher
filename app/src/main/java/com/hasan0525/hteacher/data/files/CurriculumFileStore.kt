package com.hasan0525.hteacher.data.files

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.system.ErrnoException
import android.system.OsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.util.UUID

data class StoredPdf(val title: String, val absolutePath: String)

class CurriculumFileStore(private val context: Context) {
    private val curriculaDirectory: File
        get() = File(context.filesDir, "curricula").apply {
            if (!exists() && !mkdirs()) throw IOException("تعذر إنشاء مجلد المناهج")
        }

    suspend fun importPdf(uri: Uri): StoredPdf = withContext(Dispatchers.IO) {
        val displayName = readDisplayName(uri)
        val directory = curriculaDirectory
        val id = UUID.randomUUID().toString()
        val staging = File(directory, "$id.part")
        val target = File(directory, "$id.pdf")

        try {
            context.contentResolver.openInputStream(uri)?.use { raw ->
                BufferedInputStream(raw).use { input ->
                    val header = ByteArray(1024)
                    val read = input.read(header)
                    val pdfMarker = "%PDF-".toByteArray(Charsets.US_ASCII)
                    val valid = read >= pdfMarker.size &&
                        (0..(read - pdfMarker.size)).any { offset ->
                            pdfMarker.indices.all { header[offset + it] == pdfMarker[it] }
                        }
                    require(valid) { "الملف المختار ليس PDF صالحًا" }

                    staging.outputStream().buffered().use { destination ->
                        destination.write(header, 0, read)
                        // Stream the entire document in fixed-size chunks; size is
                        // limited only by available storage and Android/PDF support.
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            currentCoroutineContext().ensureActive()
                            val count = input.read(buffer)
                            if (count < 0) break
                            destination.write(buffer, 0, count)
                        }
                        destination.flush()
                    }
                }
            } ?: throw IOException("تعذر الوصول إلى الملف المختار")

            require(staging.length() > 0L) { "الملف فارغ" }
            // Checking only the extension or header would accept corrupt/truncated PDFs.
            try {
                ParcelFileDescriptor.open(staging, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                    PdfRenderer(fd).use { renderer ->
                        require(renderer.pageCount > 0) { "ملف PDF لا يحتوي صفحات" }
                    }
                }
            } catch (error: Exception) {
                throw IllegalArgumentException("تعذر قراءة PDF. الملف قد يكون تالفًا أو محميًا بكلمة مرور", error)
            }
            if (!staging.renameTo(target)) throw IOException("تعذر إنهاء نسخ ملف PDF")
            StoredPdf(
                title = displayName.replace(Regex("(?i)\\.pdf$"), "").ifBlank { "منهج" },
                absolutePath = target.absolutePath
            )
        } catch (e: SecurityException) {
            throw IOException("لا يوجد إذن لقراءة الملف المختار", e)
        } catch (e: IOException) {
            val diskFull = generateSequence<Throwable>(e) { it.cause }
                .any { it is ErrnoException && it.errno == OsConstants.ENOSPC }
            throw IOException(
                if (diskFull) "مساحة التخزين غير كافية. وفر مساحة ثم أعد المحاولة"
                else "فشل نسخ الملف. تحقق من مساحة التخزين وإمكانية قراءة الملف", e
            )
        } finally {
            staging.delete()
        }
    }

    fun delete(absolutePath: String?) {
        if (absolutePath.isNullOrBlank()) return
        runCatching { File(absolutePath).delete() }
    }

    private fun readDisplayName(uri: Uri): String = runCatching {
        context.contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
        )?.use { cursor ->
            val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (column >= 0 && cursor.moveToFirst()) cursor.getString(column) else null
        }
    }.getOrNull().orEmpty().ifBlank { "منهج.pdf" }

}
