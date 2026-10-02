package com.hasan0525.hteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.hasan0525.hteacher.ui.premium.HomeDashboard
import com.hasan0525.hteacher.ui.premium.EducationLibrary
import com.hasan0525.hteacher.ui.premium.ExamStudio
import com.hasan0525.hteacher.ui.premium.ProfessionalPortfolio
import com.hasan0525.hteacher.ui.premium.ClassroomHub
import com.hasan0525.hteacher.ui.premium.EducationPdfReader
import com.hasan0525.hteacher.ui.theme.HTeacherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HTeacherTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var route by rememberSaveable { mutableStateOf("home") }
                    var pdfPath by rememberSaveable { mutableStateOf("") }
                    var pdfTitle by rememberSaveable { mutableStateOf("") }
                    fun navigate(target: String) {
                        if (target in setOf("home", "curricula", "exams", "portfolio", "tools")) {
                            route = target
                        }
                    }
                    BackHandler(route != "home") {
                        route = if (route == "pdf") "curricula" else "home"
                    }
                    AnimatedContent(
                        targetState = route,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(210)) +
                                slideInHorizontally(animationSpec = tween(210)) { it / 12 })
                                .togetherWith(fadeOut(animationSpec = tween(130)))
                        },
                        label = "main navigation"
                    ) { current ->
                    when(current) {
                        "curricula" -> EducationLibrary(
                            onBack = { route = "home" },
                            onNavigate = ::navigate,
                            onOpenPdf = { path, title ->
                                pdfPath = path
                                pdfTitle = title
                                route = "pdf"
                            }
                        )
                        "exams" -> ExamStudio(onBack={route="home"},onNavigate=::navigate)
                        "portfolio" -> ProfessionalPortfolio(onBack={route="home"},onNavigate=::navigate)
                        "tools" -> ClassroomHub(onBack={route="home"},onNavigate=::navigate)
                        "pdf" -> EducationPdfReader(
                            filePath=pdfPath,title=pdfTitle,onBack={route="curricula"}
                        )
                        else -> HomeDashboard(onNavigate=::navigate)
                    }
                    }
                }
            }
        }
    }
}
