package com.hasan0525.hteacher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TeacherSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AppCard(modifier) {
        Column(Modifier.padding(18.dp)) {
            SectionHeader(title)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
