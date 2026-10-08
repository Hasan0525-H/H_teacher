package com.hasan0525.hteacher

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "المعلم H - Windows"
    ) {
        DesktopApp()
    }
}

@Composable
private fun DesktopApp() {
    MaterialTheme {
        Text("المعلم H - نسخة ويندوز التجريبية")
    }
}
