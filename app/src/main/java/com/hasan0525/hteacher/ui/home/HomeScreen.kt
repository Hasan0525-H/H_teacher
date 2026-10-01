package com.hasan0525.hteacher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
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
                else -> Unit
            }
        }
    )
}

@Composable
private fun HomeDashboard(
    state: HomeUiState,
    onToolClick: (TeacherTool) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "لوحة المعلم",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Text(
                            text = state.greeting,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Text(
                    text = "الأدوات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.tools, key = { it.id }) { tool ->
                TeacherToolCard(tool, onToolClick)
            }
        }
    }
}

@Composable
private fun TeacherToolCard(
    tool: TeacherTool,
    onToolClick: (TeacherTool) -> Unit
) {
    val icon = when (tool.id) {
        ToolId.CURRICULA -> Icons.Outlined.MenuBook
        ToolId.EXAMS -> Icons.Outlined.Assignment
        ToolId.PORTFOLIO -> Icons.Outlined.Folder
        ToolId.MORE -> Icons.Outlined.GridView
        else -> Icons.Outlined.School
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onToolClick(tool) },
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = tool.title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Outlined.ChevronLeft,
                contentDescription = null
            )
        }
    }
}
