package com.hasan0525.hteacher.ui.premium

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hasan0525.hteacher.data.pdf.PdfRendererEngine
import com.hasan0525.hteacher.data.pdf.RenderedPdfPage

private sealed interface ReaderState {
    data object Loading:ReaderState
    data class Ready(val page:RenderedPdfPage):ReaderState
    data class Error(val details:String):ReaderState
}

@Composable
fun EducationPdfReader(filePath:String,title:String,onBack:()->Unit){
    var pageIndex by rememberSaveable(filePath){mutableIntStateOf(0)}
    var zoom by remember(filePath,pageIndex){mutableFloatStateOf(1f)}
    var translation by remember(filePath,pageIndex){mutableStateOf(Offset.Zero)}
    var pageInput by remember(filePath){mutableStateOf("")}
    val pageState by produceState<ReaderState>(ReaderState.Loading,filePath,pageIndex){
        value=ReaderState.Loading
        value=try {
            ReaderState.Ready(PdfRendererEngine.renderPage(filePath,pageIndex))
        }catch(e:Exception){ReaderState.Error(e.message?:"تعذر فتح الملف")}
    }
    val rendered=(pageState as? ReaderState.Ready)?.page
    DisposableEffect(rendered?.bitmap) {
        val old=rendered?.bitmap
        onDispose { if(old!=null&&!old.isRecycled)old.recycle() }
    }
    Scaffold(containerColor=Edu.Canvas,
        topBar={AppTopBar(title.ifBlank{"قراءة المنهج"},"قارئ PDF",onBack,
            action={TextButton(onClick={zoom=1f;translation=Offset.Zero}){Text("احتواء",color=Edu.Blue)}})},
        bottomBar={
            if(rendered!=null){
                Column(Modifier.fillMaxWidth().background(Edu.Paper).navigationBarsPadding()
                    .padding(horizontal=18.dp,vertical=10.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                        SecondaryButton("السابق",Modifier.weight(1f),enabled=rendered.pageIndex>0){
                            pageIndex=rendered.pageIndex-1
                        }
                        Text("${rendered.pageIndex+1} / ${rendered.pageCount}",
                            color=Edu.Navy,fontWeight=FontWeight.Bold)
                        SecondaryButton("التالي",Modifier.weight(1f),
                            enabled=rendered.pageIndex<rendered.pageCount-1){pageIndex=rendered.pageIndex+1}
                    }
                    if(rendered.pageCount>1) Slider(
                        value=rendered.pageIndex.toFloat(),
                        onValueChange={pageIndex=it.toInt().coerceIn(0,rendered.pageCount-1)},
                        valueRange=0f..(rendered.pageCount-1).toFloat(),
                        colors=SliderDefaults.colors(thumbColor=Edu.Blue,activeTrackColor=Edu.Blue))
                }
            }
        }
    ){pad->
        when(val result=pageState){
            ReaderState.Loading->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){
                LoadingView("جارٍ تجهيز الصفحة")
            }
            is ReaderState.Error->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){
                EmptyState("تعذر فتح الملف",result.details,EduGlyph.PDF,"رجوع"){onBack()}
            }
            is ReaderState.Ready->{
                Column(Modifier.fillMaxSize().padding(pad),
                    verticalArrangement=Arrangement.spacedBy(7.dp),
                    horizontalAlignment=Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth().padding(horizontal=18.dp),verticalAlignment=Alignment.CenterVertically){
                        Text("اسحب للتنقل بين صفحات الكتاب",color=Edu.Muted,
                            style=MaterialTheme.typography.bodySmall,modifier=Modifier.weight(1f))
                        Text("تكبير ${(zoom*100).toInt()}%",color=Edu.Blue,
                            style=MaterialTheme.typography.bodySmall)
                    }
                    Box(Modifier.fillMaxWidth().weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Edu.Canvas)
                        .verticalScroll(rememberScrollState())
                        .pointerInput(pageIndex){
                            detectTransformGestures { _,pan,magnification,_ ->
                                val updated=(zoom*magnification).coerceIn(1f,3.5f)
                                zoom=updated
                                translation=if(updated<=1f)Offset.Zero else translation+pan
                            }
                        },contentAlignment=Alignment.TopCenter) {
                        Image(
                            bitmap=result.page.bitmap.asImageBitmap(),
                            contentDescription="صفحة ${result.page.pageIndex+1}",
                            modifier=Modifier.fillMaxWidth().padding(horizontal=8.dp)
                                .graphicsLayer(scaleX=zoom,scaleY=zoom,translationX=translation.x,translationY=translation.y),
                            contentScale=ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}
