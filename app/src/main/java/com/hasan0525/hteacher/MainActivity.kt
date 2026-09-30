package com.hasan0525.hteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.hasan0525.hteacher.ui.curriculum.CurriculumRoute
import com.hasan0525.hteacher.ui.exam.ExamGeneratorRoute
import com.hasan0525.hteacher.ui.home.HomeRoute
import com.hasan0525.hteacher.ui.pdf.PdfViewerScreen
import com.hasan0525.hteacher.ui.theme.HTeacherTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_CURRICULA = "curricula"
private const val ROUTE_EXAMS = "exams"
private const val ROUTE_PDF = "pdf"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HTeacherTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var route by rememberSaveable { mutableStateOf(ROUTE_HOME) }
                    var pdfPath by rememberSaveable { mutableStateOf("") }
                    var pdfTitle by rememberSaveable { mutableStateOf("") }

                    BackHandler(enabled = route != ROUTE_HOME) {
                        route = if (route == ROUTE_PDF) {
                            ROUTE_CURRICULA
                        } else {
                            ROUTE_HOME
                        }
                    }

                    when (route) {
                        ROUTE_CURRICULA -> CurriculumRoute(
                            onBack = { route = ROUTE_HOME },
                            onOpenPdf = { path, title ->
                                pdfPath = path
                                pdfTitle = title
                                route = ROUTE_PDF
                            }
                        )

                        ROUTE_EXAMS -> ExamGeneratorRoute(
                            onBack = { route = ROUTE_HOME }
                        )

                        ROUTE_PDF -> PdfViewerScreen(
                            filePath = pdfPath,
                            title = pdfTitle,
                            onBack = { route = ROUTE_CURRICULA }
                        )

                        else -> HomeRoute(
                            onOpenCurricula = {
                                route = ROUTE_CURRICULA
                            },
                            onOpenExams = {
                                route = ROUTE_EXAMS
                            }
                        )
                    }
                }
            }
        }
    }
}
