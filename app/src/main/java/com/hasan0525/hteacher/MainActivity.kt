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
import com.hasan0525.hteacher.ui.modern.ModernCurriculum
import com.hasan0525.hteacher.ui.modern.ModernExam
import com.hasan0525.hteacher.ui.modern.ModernHome
import com.hasan0525.hteacher.ui.modern.ModernPortfolio
import com.hasan0525.hteacher.ui.modern.ModernTools
import com.hasan0525.hteacher.ui.pdf.PdfViewerScreen
import com.hasan0525.hteacher.ui.theme.HTeacherTheme

private const val HOME="home"
private const val CURRICULA="curricula"
private const val EXAMS="exams"
private const val PORTFOLIO="portfolio"
private const val TOOLS="tools"
private const val PDF="pdf"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HTeacherTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var route by rememberSaveable { mutableStateOf(HOME) }
                    var pdfPath by rememberSaveable { mutableStateOf("") }
                    var pdfTitle by rememberSaveable { mutableStateOf("") }

                    fun navigate(target:String) {
                        if (target in setOf(HOME,CURRICULA,EXAMS,PORTFOLIO,TOOLS)) route=target
                    }

                    BackHandler(enabled=route!=HOME) {
                        route=if(route==PDF) CURRICULA else HOME
                    }

                    when(route) {
                        CURRICULA -> ModernCurriculum(
                            onBack={route=HOME},
                            onNavigate=::navigate,
                            onOpenPdf={path,title->pdfPath=path;pdfTitle=title;route=PDF}
                        )
                        EXAMS -> ModernExam(onBack={route=HOME},onNavigate=::navigate)
                        PORTFOLIO -> ModernPortfolio(onBack={route=HOME},onNavigate=::navigate)
                        TOOLS -> ModernTools(onBack={route=HOME},onNavigate=::navigate)
                        PDF -> PdfViewerScreen(filePath=pdfPath,title=pdfTitle,onBack={route=CURRICULA})
                        else -> ModernHome(onOpen=::navigate)
                    }
                }
            }
        }
    }
}
