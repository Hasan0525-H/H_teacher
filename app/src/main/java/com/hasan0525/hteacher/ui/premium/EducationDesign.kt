package com.hasan0525.hteacher.ui.premium

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

object Edu {
    val Navy = Color(0xFF162841)
    val Blue = Color(0xFF265DE0)
    val BlueSoft = Color(0xFFEAF0FF)
    val Teal = Color(0xFF087E83)
    val Mint = Color(0xFFE3F3F1)
    val Canvas = Color(0xFFF6F8FC)
    val Paper = Color.White
    val Line = Color(0xFFE7EBF2)
    val Muted = Color(0xFF68768A)
    val Amber = Color(0xFFF4AA40)
    val AmberSoft = Color(0xFFFFF2DC)
    val Error = Color(0xFFBB3A47)
}

enum class EduGlyph { DASH, BOOK, EXAM, FOLDER, GROUP, DATE, CHART, PDF, PLUS, ARROW, SEARCH, SPARK, CHECK, DOC, MORE, BACK }

@Composable
fun Glyph(name: EduGlyph, modifier: Modifier = Modifier, tint: Color = Edu.Blue) {
    Canvas(modifier.size(25.dp)) {
        val unit = size.minDimension / 24f
        fun line(a: Float,b: Float,c: Float,d: Float) {
            drawLine(tint, Offset(a*unit,b*unit),Offset(c*unit,d*unit),strokeWidth=1.8f*unit)
        }
        fun rect(x:Float,y:Float,w:Float,h:Float,r:Float=2f) {
            drawRoundRect(tint, Offset(x*unit,y*unit),Size(w*unit,h*unit),
                androidx.compose.ui.geometry.CornerRadius(r*unit),style=Stroke(1.7f*unit))
        }
        when(name) {
            EduGlyph.DASH -> { rect(3f,3f,8f,8f);rect(13f,3f,8f,5f);rect(3f,13f,8f,8f);rect(13f,10f,8f,11f) }
            EduGlyph.BOOK -> { line(12f,5f,12f,20f);line(12f,7f,5f,5f);line(5f,5f,3f,7f);line(3f,7f,3f,19f);line(3f,19f,12f,21f);line(12f,7f,19f,5f);line(19f,5f,21f,7f);line(21f,7f,21f,19f);line(21f,19f,12f,21f)}
            EduGlyph.EXAM -> { rect(5f,4f,14f,17f,2f);line(9f,3f,15f,3f);line(9f,9f,10f,10f);line(10f,10f,12f,7f);line(14f,9f,17f,9f);line(9f,14f,10f,15f);line(10f,15f,12f,12f);line(14f,14f,17f,14f)}
            EduGlyph.FOLDER -> { line(3f,8f,3f,19f);line(3f,19f,21f,19f);line(21f,19f,21f,8f);line(21f,8f,11f,8f);line(11f,8f,9f,5f);line(9f,5f,3f,5f);line(3f,5f,3f,8f)}
            EduGlyph.GROUP -> { drawCircle(tint,3f*unit,Offset(9f*unit,8f*unit),style=Stroke(1.7f*unit));drawCircle(tint,2f*unit,Offset(17f*unit,9f*unit),style=Stroke(1.7f*unit));line(3f,20f,3f,17f);line(3f,17f,6f,14f);line(6f,14f,12f,14f);line(12f,14f,15f,17f);line(15f,17f,15f,20f);line(15f,20f,3f,20f);line(17f,15f,21f,17f);line(21f,17f,21f,20f)}
            EduGlyph.DATE -> {rect(3f,5f,18f,16f);line(3f,10f,21f,10f);line(8f,3f,8f,7f);line(16f,3f,16f,7f);line(8f,15f,10f,17f);line(10f,17f,16f,13f)}
            EduGlyph.CHART -> {line(3f,20f,21f,20f);line(3f,20f,3f,5f);line(6f,16f,11f,11f);line(11f,11f,14f,14f);line(14f,14f,21f,5f)}
            EduGlyph.PDF, EduGlyph.DOC -> {rect(5f,2f,14f,20f);line(9f,9f,15f,9f);line(9f,13f,15f,13f);line(9f,17f,14f,17f)}
            EduGlyph.PLUS -> {line(12f,4f,12f,20f);line(4f,12f,20f,12f)}
            EduGlyph.ARROW -> {line(5f,12f,19f,12f);line(13f,6f,19f,12f);line(19f,12f,13f,18f)}
            EduGlyph.BACK -> {line(19f,12f,5f,12f);line(11f,6f,5f,12f);line(5f,12f,11f,18f)}
            EduGlyph.SEARCH -> {drawCircle(tint,6f*unit,Offset(10f*unit,10f*unit),style=Stroke(1.8f*unit));line(14f,14f,21f,21f)}
            EduGlyph.SPARK -> {line(12f,2f,12f,22f);line(3f,12f,21f,12f);line(6f,6f,18f,18f);line(18f,6f,6f,18f)}
            EduGlyph.CHECK -> {line(4f,12f,10f,18f);line(10f,18f,20f,6f)}
            EduGlyph.MORE -> { listOf(6f,12f,18f).forEach{drawCircle(tint,1.6f*unit,Offset(it*unit,12f*unit))} }
        }
    }
}

@Composable
fun AppTopBar(title:String, subtitle:String?=null, onBack:(()->Unit)?=null, action:(@Composable ()->Unit)?=null) {
    Row(Modifier.fillMaxWidth().background(Edu.Paper).padding(horizontal=20.dp,vertical=15.dp),
        verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
        if(onBack!=null) IconButton(onClick=onBack) { Glyph(EduGlyph.BACK, tint=Edu.Navy) }
        Column(Modifier.weight(1f)) {
            Text(title,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,color=Edu.Navy)
            subtitle?.let { Text(it,style=MaterialTheme.typography.bodySmall,color=Edu.Muted,maxLines=1) }
        }
        action?.invoke()
    }
}

@Composable
fun PrimaryButton(text:String,modifier:Modifier=Modifier,enabled:Boolean=true,icon:EduGlyph?=null,onClick:()->Unit) {
    Button(onClick=onClick,enabled=enabled,modifier=modifier.heightIn(min=52.dp),shape=RoundedCornerShape(16.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Edu.Blue,disabledContainerColor=Edu.Line)) {
        if(icon!=null){Glyph(icon,Modifier.size(19.dp),Color.White);Spacer(Modifier.width(8.dp))}
        Text(text,fontWeight=FontWeight.Bold)
    }
}

@Composable
fun SecondaryButton(text:String,modifier:Modifier=Modifier,enabled:Boolean=true,onClick:()->Unit) {
    OutlinedButton(onClick=onClick,enabled=enabled,modifier=modifier.heightIn(min=48.dp),
        border=BorderStroke(1.dp,Edu.Line),shape=RoundedCornerShape(16.dp)) {Text(text,color=Edu.Navy)}
}

@Composable
fun AppCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){
    Column(modifier.background(Edu.Paper,RoundedCornerShape(23.dp))
        .padding(18.dp),verticalArrangement=Arrangement.spacedBy(9.dp),content=content)
}

@Composable
fun SectionHeader(title:String,subtitle:String?=null,action:String?=null,onAction:(()->Unit)?=null){
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title,color=Edu.Navy,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
            subtitle?.let{Text(it,color=Edu.Muted,style=MaterialTheme.typography.bodySmall)}
        }
        if(action!=null&&onAction!=null) TextButton(onClick=onAction){Text(action,color=Edu.Blue)}
    }
}

@Composable
fun FeatureCard(title:String,subtitle:String,icon:EduGlyph,modifier:Modifier=Modifier,accent:Color=Edu.Blue,
                onClick:()->Unit) {
    Row(modifier.background(Edu.Paper,RoundedCornerShape(19.dp)).clickable(onClick=onClick).padding(15.dp),
        verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(48.dp).background(accent.copy(alpha=.11f),RoundedCornerShape(15.dp)),
            contentAlignment=Alignment.Center){Glyph(icon,tint=accent)}
        Column(Modifier.weight(1f)) {
            Text(title,color=Edu.Navy,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.bodyLarge)
            Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodySmall,maxLines=2,overflow=TextOverflow.Ellipsis)
        }
        Glyph(EduGlyph.ARROW,Modifier.size(18.dp),Edu.Muted)
    }
}

@Composable
fun StatCard(title:String,value:String,icon:EduGlyph,modifier:Modifier=Modifier,tint:Color=Edu.Blue){
    Column(modifier.background(Edu.Paper,RoundedCornerShape(18.dp)).padding(14.dp),
        verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Glyph(icon,Modifier.size(23.dp),tint)
        Text(value,style=MaterialTheme.typography.headlineSmall,color=Edu.Navy,fontWeight=FontWeight.Bold)
        Text(title,color=Edu.Muted,style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun EmptyState(title:String,subtitle:String,icon:EduGlyph,action:String?=null,onAction:(()->Unit)?=null){
    Column(Modifier.fillMaxWidth().padding(vertical=30.dp,horizontal=20.dp),
        horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(11.dp)){
        Box(Modifier.size(76.dp).background(Edu.BlueSoft,CircleShape),contentAlignment=Alignment.Center) {
            Glyph(icon,Modifier.size(32.dp))
        }
        Text(title,fontWeight=FontWeight.Bold,color=Edu.Navy)
        Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodyMedium)
        if(action!=null&&onAction!=null) PrimaryButton(action,icon=EduGlyph.PLUS,onClick=onAction)
    }
}

@Composable
fun LoadingView(label:String,modifier:Modifier=Modifier) {
    Row(modifier.fillMaxWidth().padding(18.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),
        verticalAlignment=Alignment.CenterVertically) {
        CircularProgressIndicator(Modifier.size(23.dp),strokeWidth=2.dp,color=Edu.Blue)
        Text(label,color=Edu.Muted)
    }
}

@Composable
fun FilterChipPill(text:String,selected:Boolean,onClick:()->Unit) {
    Surface(onClick=onClick,shape=RoundedCornerShape(13.dp),color=if(selected)Edu.Navy else Edu.Paper,
        border=if(selected)null else BorderStroke(1.dp,Edu.Line)) {
        Text(text,modifier=Modifier.padding(horizontal=15.dp,vertical=9.dp),
            color=if(selected)Color.White else Edu.Navy,fontWeight=if(selected)FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun FormField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,
              singleLine:Boolean=true) {
    OutlinedTextField(value=value,onValueChange=onChange,modifier=modifier,shape=RoundedCornerShape(15.dp),
        singleLine=singleLine,label={Text(label)},colors=OutlinedTextFieldDefaults.colors(
        focusedBorderColor=Edu.Blue,unfocusedBorderColor=Edu.Line,focusedLabelColor=Edu.Blue))
}

@Composable
fun EducationBottomNav(selected:String,onNavigate:(String)->Unit) {
    NavigationBar(containerColor=Edu.Paper,tonalElevation=0.dp) {
        listOf(Triple("home","الرئيسية",EduGlyph.DASH),Triple("curricula","المكتبة",EduGlyph.BOOK),
            Triple("exams","الاختبارات",EduGlyph.EXAM),Triple("portfolio","الإنجاز",EduGlyph.FOLDER),
            Triple("tools","الأدوات",EduGlyph.GROUP)).forEach { (route,title,icon) ->
            NavigationBarItem(
                selected=selected==route,onClick={onNavigate(route)},
                icon={Glyph(icon,tint=if(selected==route)Edu.Blue else Edu.Muted)},
                label={Text(title,maxLines=1)},
                colors=NavigationBarItemDefaults.colors(selectedIconColor=Edu.Blue,
                    selectedTextColor=Edu.Blue,indicatorColor=Edu.BlueSoft,unselectedTextColor=Edu.Muted)
            )
        }
    }
}
