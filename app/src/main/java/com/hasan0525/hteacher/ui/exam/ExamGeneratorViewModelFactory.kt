package com.hasan0525.hteacher.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.data.pdf.PdfExamExporter
import com.hasan0525.hteacher.data.pdf.PdfLessonTextExtractor

class ExamGeneratorViewModelFactory(
    private val application: HTeacherApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamGeneratorViewModel::class.java)) {
            return ExamGeneratorViewModel(
                repository = application.container.teacherRepository,
                exporter = PdfExamExporter(application.applicationContext),
                aiQuestionService = application.container.aiQuestionService,
                pdfTextExtractor = PdfLessonTextExtractor(application.applicationContext)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
