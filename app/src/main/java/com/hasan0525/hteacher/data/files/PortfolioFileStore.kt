package com.hasan0525.hteacher.data.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

data class StoredPortfolioAttachment(
    val fileName: String,
    val mimeType: String,
    val absolutePath: String
)

class PortfolioFileStore(
    private val context: Context
) {
    private val portfolioDirectory: File
        get() = File(context.filesDir, "portfolio").apply { mkdirs() }

    suspend fun importFile(uri: Uri): StoredPortfolioAttachment =
        withContext(Dispatchers.IO) {
            val fileName = readDisplayName(uri)
            val extension = fileName
                .substringAfterLast('.', "")
                .takeIf { it.isNotBlank() }
                ?.let { "." + it }
                .orEmpty()

            val target = File(
                portfolioDirectory,
                UUID.randomUUID().toString() + extension
            )

            try {
                val input = context.contentResolver.openInputStream(uri)
                    ?: error("تعذر فتح المرفق")

                input.use { source ->
                    target.outputStream().buffered().use { destination ->
                        source.copyTo(destination)
                    }
                }

                StoredPortfolioAttachment(
                    fileName = fileName.ifBlank { "مرفق" },
                    mimeType = context.contentResolver.getType(uri)
                        ?: "application/octet-stream",
                    absolutePath = target.absolutePath
                )
            } catch (error: Throwable) {
                target.delete()
                throw error
            }
        }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }

    private fun readDisplayName(uri: Uri): String {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) {
                return cursor.getString(index).orEmpty()
            }
        }

        return "مرفق"
    }
}
