package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hasan0525.hteacher.data.pdf.PdfRendererEngine
import com.hasan0525.hteacher.data.pdf.RenderedPdfPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private sealed interface ReaderState {
    data object Loading : ReaderState
    data class Ready(val page: RenderedPdfPage) : ReaderState
    data class Error(val details: String) : ReaderState
}

private val ReaderInk = Color(0xFF141F30)
private val ReaderPanel = Color(0xFF222F42)

@Composable
fun EducationPdfReader(filePath: String, title: String, onBack: () -> Unit) {
    var pageIndex by rememberSaveable(filePath) { mutableIntStateOf(0) }
    var zoom by remember(filePath, pageIndex) { mutableFloatStateOf(1f) }
    var translation by remember(filePath, pageIndex) { mutableStateOf(Offset.Zero) }
    var pageInput by remember(filePath) { mutableStateOf("1") }
    LaunchedEffect(pageIndex) { pageInput = (pageIndex + 1).toString() }

    // File I/O and bitmap rendering are off the Compose UI thread.
    val pageState by produceState<ReaderState>(ReaderState.Loading, filePath, pageIndex) {
        value = ReaderState.Loading
        value = try {
            ReaderState.Ready(withContext(Dispatchers.IO) {
                PdfRendererEngine.renderPage(filePath, pageIndex)
            })
        } catch (error: Exception) {
            ReaderState.Error(error.message ?: "تعذر قراءة هذه الصفحة")
        }
    }
    val page = (pageState as? ReaderState.Ready)?.page
    DisposableEffect(page?.bitmap) {
        val bitmap = page?.bitmap
        onDispose { if (bitmap != null && !bitmap.isRecycled) bitmap.recycle() }
    }

    Scaffold(
        containerColor = ReaderInk,
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(ReaderPanel)
                    .statusBarsPadding().padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Glyph(EduGlyph.BACK, Modifier.size(22.dp), Color.White)
                    Spacer(Modifier.width(7.dp))
                    Text("المكتبة", color = Color.White)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(title.ifBlank { "قراءة المنهج" }, color = Color.White,
                        maxLines = 1, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { zoom = 1f; translation = Offset.Zero }) {
                    Text("احتواء", color = Color.White)
                }
            }
        },
        bottomBar = {
            if (page != null) {
                Column(
                    Modifier.fillMaxWidth().background(ReaderPanel)
                        .navigationBarsPadding().padding(horizontal = 17.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) },
                            enabled = page.pageIndex > 0,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(13.dp)
                        ) { Text("السابق", color = Color.White) }
                        OutlinedTextField(
                            value = pageInput,
                            onValueChange = { pageInput = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.width(65.dp).height(54.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Edu.Amber,
                                unfocusedBorderColor = Color.White.copy(alpha = .5f)
                            )
                        )
                        TextButton(onClick = {
                            pageInput.toIntOrNull()?.let {
                                pageIndex = (it - 1).coerceIn(0, page.pageCount - 1)
                            }
                        }) { Text("/ ${page.pageCount}", color = Color.White) }
                        OutlinedButton(
                            onClick = { pageIndex = (pageIndex + 1).coerceAtMost(page.pageCount - 1) },
                            enabled = page.pageIndex < page.pageCount - 1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(13.dp)
                        ) { Text("التالي", color = Color.White) }
                    }
                }
            }
        }
    ) { padding ->
        when (val state = pageState) {
            ReaderState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Edu.Amber)
            }
            is ReaderState.Error -> Box(
                Modifier.fillMaxSize().padding(padding).background(Edu.Canvas),
                contentAlignment = Alignment.Center
            ) {
                EmptyState("تعذر فتح الكتاب", state.details, EduGlyph.PDF, "العودة") { onBack() }
            }
            is ReaderState.Ready -> {
                Column(Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.fillMaxWidth().weight(1f).padding(horizontal = 13.dp)
                            .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                            .background(Color(0xFF303B4B))
                            .verticalScroll(rememberScrollState())
                            .pointerInput(pageIndex) {
                                detectTransformGestures { _, pan, scale, _ ->
                                    val newZoom = (zoom * scale).coerceIn(1f, 4f)
                                    zoom = newZoom
                                    translation = if (newZoom <= 1f) Offset.Zero else translation + pan
                                }
                            },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            bitmap = state.page.bitmap.asImageBitmap(),
                            contentDescription = "صفحة ${state.page.pageIndex + 1}",
                            modifier = Modifier.widthIn(max = 820.dp)
                                .fillMaxWidth().padding(8.dp)
                                .background(Color.White)
                                .graphicsLayer(
                                    scaleX = zoom, scaleY = zoom,
                                    translationX = translation.x, translationY = translation.y
                                ),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}
