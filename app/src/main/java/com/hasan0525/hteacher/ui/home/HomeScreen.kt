package com.hasan0525.hteacher.ui.home

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
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("المعلم H", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("لوحة المعلم", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Outlined.Person, null, Modifier.padding(12.dp).size(26.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("ابدأ من هنا", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("اختر الأداة التي تحتاجها الآن", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            QuickAction(Modifier.weight(1f), Icons.Outlined.MenuBook, "المناهج", state.tools.firstOrNull { it.id == ToolId.CURRICULA }, onToolClick)
                            QuickAction(Modifier.weight(1f), Icons.Outlined.Assignment, "اختبار", state.tools.firstOrNull { it.id == ToolId.EXAMS }, onToolClick)
                        }
                        QuickAction(Modifier.fillMaxWidth(), Icons.Outlined.Folder, "ملف الإنجاز", state.tools.firstOrNull { it.id == ToolId.PORTFOLIO }, onToolClick)
                    }
                }
            }
            item {
                Text("الأدوات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(state.tools, key = { it.id }) { tool ->
                ToolListRow(tool, onToolClick)
            }
        }
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tool: TeacherTool?,
    onToolClick: (TeacherTool) -> Unit
) {
    Surface(
        modifier = modifier.height(92.dp).clickable(enabled = tool != null) { tool?.let(onToolClick) },
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, null, Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}
@Composable
private fun ToolListRow(tool: TeacherTool, onToolClick: (TeacherTool) -> Unit) {
    val icon = when (tool.id) {
        ToolId.CURRICULA -> Icons.Outlined.MenuBook
        ToolId.EXAMS -> Icons.Outlined.Assignment
        ToolId.PORTFOLIO -> Icons.Outlined.Folder
        ToolId.MORE -> Icons.Outlined.GridView
    }
    Surface(
        Modifier.fillMaxWidth().clickable(enabled = tool.status == ToolStatus.READY) { onToolClick(tool) },
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Icon(icon, null, Modifier.padding(12.dp).size(24.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(tool.title, fontWeight = FontWeight.Bold)
                Text(if (tool.status == ToolStatus.READY) tool.description else "قريبًا", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(if (tool.status == ToolStatus.READY) Icons.Outlined.ChevronLeft else Icons.Outlined.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
