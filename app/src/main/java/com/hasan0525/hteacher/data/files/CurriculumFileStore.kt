package com.hasan0525.hteacher.data.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

data class StoredPdf(
    val title: String,
    val absolutePath: String
)

class CurriculumFileStore(
    private val context: Context
) {
    private val curriculaDirectory: File
        get() = File(context.filesDir, "curricula").apply { mkdirs() }

    suspend fun importPdf(uri: Uri): StoredPdf = withContext(Dispatchers.IO) {
        val displayName = readDisplayName(uri)
        val target = File(
            curriculaDirectory,
            UUID.randomUUID().toString() + ".pdf"
        )

        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: error("تعذر فتح ملف PDF")

            input.use { source ->
                target.outputStream().buffered().use { destination ->
                    source.copyTo(destination)
                }
            }

            StoredPdf(
                title = displayName
                    .removeSuffix(".pdf")
                    .removeSuffix(".PDF")
                    .ifBlank { "منهج" },
                absolutePath = target.absolutePath
            )
        } catch (error: Throwable) {
            target.delete()
            throw error
        }
    }

    fun delete(absolutePath: String?) {
        if (absolutePath.isNullOrBlank()) return
        runCatching { File(absolutePath).delete() }
    }

    private fun readDisplayName(uri: Uri): String {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) {
                return cursor.getString(index).orEmpty()
            }
        }

        return "منهج.pdf"
    }
}
