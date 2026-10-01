package com.hasan0525.hteacher.ui.pdf

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hasan0525.hteacher.data.pdf.PdfRendererEngine
import com.hasan0525.hteacher.data.pdf.RenderedPdfPage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack

private sealed interface PdfPageState {
    data object Loading : PdfPageState
    data class Ready(val page: RenderedPdfPage) : PdfPageState
    data class Error(val message: String) : PdfPageState
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    filePath: String,
    title: String,
    onBack: () -> Unit
) {
    var requestedPage by remember(filePath) {
        mutableIntStateOf(0)
    }

    val pageState by produceState<PdfPageState>(
        initialValue = PdfPageState.Loading,
        key1 = filePath,
        key2 = requestedPage
    ) {
        value = PdfPageState.Loading
        value = try {
            PdfPageState.Ready(
                PdfRendererEngine.renderPage(
                    filePath = filePath,
                    requestedPage = requestedPage
                )
            )
        } catch (error: Throwable) {
            PdfPageState.Error(
                error.message ?: "تعذر فتح ملف PDF"
            )
        }
    }

    val currentBitmap = remember(pageState) {
        (pageState as? PdfPageState.Ready)?.page?.bitmap
    }

    DisposableEffect(currentBitmap) {
        onDispose {
            recycleSafely(currentBitmap)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title.ifBlank { "المنهج" },
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = pageState) {
            PdfPageState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "جارٍ فتح الصفحة...",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            is PdfPageState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is PdfPageState.Ready -> {
                val page = state.page

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxWidth()
                    ) {
                        Image(
                            bitmap = page.bitmap.asImageBitmap(),
                            contentDescription = "صفحة " + (page.pageIndex + 1),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = page.pageIndex > 0,
                            onClick = {
                                requestedPage = page.pageIndex - 1
                            }
                        ) {
                            Text("السابق")
                        }

                        Text(
                            text = (page.pageIndex + 1).toString() +
                                " / " + page.pageCount,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = page.pageIndex < page.pageCount - 1,
                            onClick = {
                                requestedPage = page.pageIndex + 1
                            }
                        ) {
                            Text("التالي")
                        }
                    }
                }
            }
        }
    }
}

private fun recycleSafely(bitmap: Bitmap?) {
    if (bitmap != null && !bitmap.isRecycled) {
        bitmap.recycle()
    }
}
