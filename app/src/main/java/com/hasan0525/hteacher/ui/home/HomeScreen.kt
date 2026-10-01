package com.hasan0525.hteacher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeRoute(
    onOpenCurricula: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenPortfolio: () -> Unit,
    onOpenTools: () -> Unit,
    viewModel: androidx.lifecycle.viewmodel.compose.viewModel<HomeViewModel>() = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    HomeDashboard(
        state = viewModel.uiState,
        onToolClick = { tool ->
            if (tool.status != ToolStatus.READY) return@HomeDashboard
            when (tool.id) {
                ToolId.CURRICULA -> onOpenCurricula()
                ToolId.EXAMS -> onOpenExams()
                ToolId.PORTFOLIO -> onOpenPortfolio()
                ToolId.MORE -> onOpenTools()
            }
        }
    )
}

@Composable
private fun HomeDashboard(
    state: HomeUiState,
    onToolClick: (TeacherTool) -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Outlined.School,
                        null,
                        Modifier.padding(12.dp).size(26.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("المعلم H", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("لوحتك", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.MoreVert, "المزيد")
                }
            }

            Spacer(Modifier.height(22.dp))

            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("مرحبًا", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            state.greeting.removePrefix("مرحبًا بك في المعلم H").ifBlank { "ابدأ من هنا" },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            null,
                            Modifier.padding(14.dp).size(30.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("الأدوات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.tools, key = { it.id }) { tool ->
                    TeacherToolCard(tool, onToolClick)
                }
            }
        }
    }
}

@Composable
private fun TeacherToolCard(
    tool: TeacherTool,
    onToolClick: (TeacherTool) -> Unit
) {
    val (icon, accent) = when (tool.id) {
        ToolId.CURRICULA -> Icons.Outlined.MenuBook to MaterialTheme.colorScheme.primaryContainer
        ToolId.EXAMS -> Icons.Outlined.Assignment to MaterialTheme.colorScheme.secondaryContainer
        ToolId.PORTFOLIO -> Icons.Outlined.Folder to MaterialTheme.colorScheme.tertiaryContainer
        ToolId.MORE -> Icons.Outlined.GridView to MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        Modifier.fillMaxWidth().height(158.dp).clip(RoundedCornerShape(26.dp)).clickable { onToolClick(tool) },
        shape = RoundedCornerShape(26.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(shape = CircleShape, color = accent) {
                Icon(icon, null, Modifier.padding(11.dp).size(25.dp))
            }
            Column {
                Text(tool.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(
                    if (tool.status == ToolStatus.READY) tool.description else "قريبًا",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
