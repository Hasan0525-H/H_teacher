package com.hasan0525.hteacher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.app.Instrumentation.ActivityResult
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import com.hasan0525.hteacher.data.files.CurriculumFileStore

@RunWith(AndroidJUnit4::class)
class CurriculumPdfImportTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun pickerImportsPdfIntoRoomAndItSurvivesActivityRestart() {
        val id = System.currentTimeMillis().toString()
        val title = "pdf_import_$id"
        val documentUri = Uri.parse("content://com.hasan0525.hteacher.testpdf/$title.pdf")
        val subject = "test_subject_$id"
        val grade = "test_grade_$id"
        Intents.init()
        try {
            intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(
                ActivityResult(
                    Activity.RESULT_OK,
                    Intent().setData(documentUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                )
            )
            compose.onAllNodesWithText("المكتبة").onFirst().performClick()
            compose.onNodeWithText("إضافة كتاب أو منهج").performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("اسم مادة جديدة").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("اسم مادة جديدة").performTextInput(subject)
            compose.onNodeWithText("اسم صف جديد").performTextInput(grade)
            compose.onNodeWithText("حفظ المنهج").performClick()

            val app = compose.activity.application as HTeacherApplication
            var savedPath = ""
            compose.waitUntil(30_000) {
                val found = runBlocking {
                    app.container.teacherRepository.curricula.first()
                        .firstOrNull { it.title == title }
                }
                savedPath = found?.localFileUri.orEmpty()
                savedPath.isNotEmpty()
            }
            assertTrue("Copied PDF missing", File(savedPath).exists())
            assertTrue("Copied PDF invalid", File(savedPath).inputStream().use {
                it.readBytes().take(5).toByteArray().contentEquals("%PDF-".toByteArray())
            })

            compose.activityRule.scenario.recreate()
            compose.waitUntil(15_000) {
                runBlocking {
                    app.container.teacherRepository.curricula.first()
                        .any { it.title == title && it.localFileUri == savedPath }
                }
            }
            assertTrue(File(savedPath).exists())
        } finally {
            Intents.release()
        }
    }
    @Test
    fun invalidPdfIsRejectedWithoutLeavingLocalFiles() {
        val app = compose.activity.application as HTeacherApplication
        val files = File(app.filesDir, "curricula")
        val before = files.listFiles()?.map { it.name }?.toSet().orEmpty()
        val uri = Uri.parse("content://com.hasan0525.hteacher.testpdf/invalid_pdf.pdf")
        val failure = runBlocking {
            runCatching { CurriculumFileStore(app).importPdf(uri) }.exceptionOrNull()
        }
        assertTrue("Invalid PDF must produce a validation error", failure is IllegalArgumentException)
        assertTrue("Invalid PDF left a copied file", files.listFiles()?.map { it.name }?.toSet().orEmpty() == before)
    }

    @Test
    fun oversizedPdfIsRejectedBeforeCopy() {
        val app = compose.activity.application as HTeacherApplication
        val files = File(app.filesDir, "curricula")
        val before = files.listFiles()?.map { it.name }?.toSet().orEmpty()
        val uri = Uri.parse("content://com.hasan0525.hteacher.testpdf/oversized_pdf.pdf")
        val failure = runBlocking {
            runCatching { CurriculumFileStore(app).importPdf(uri) }.exceptionOrNull()
        }
        assertTrue("Oversized PDF must be rejected", failure is IllegalArgumentException)
        assertTrue("Oversized PDF left a copied file", files.listFiles()?.map { it.name }?.toSet().orEmpty() == before)
    }

}
