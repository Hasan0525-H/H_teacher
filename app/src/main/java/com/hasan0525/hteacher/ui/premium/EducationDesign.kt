package com.hasan0525.hteacher.ui.premium

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

object Edu {
    val Navy = Color(0xFF102A43)
    val Blue = Color(0xFF1F6F8B)
    val BlueSoft = Color(0xFFE7F3F7)
    val Teal = Color(0xFF168A78)
    val Mint = Color(0xFFE4F5EF)
    val Canvas = Color(0xFFF7F9FB)
    val Paper = Color.White
    val Line = Color(0xFFDCE5EA)
    val Muted = Color(0xFF627486)
    val Amber = Color(0xFFFFC857)
    val AmberSoft = Color(0xFFFFF4D9)
    val Error = Color(0xFFB42318)
}

object EduSpace { val xxs=4.dp; val xs=8.dp; val sm=12.dp; val md=16.dp; val lg=24.dp; val xl=32.dp }
private val CardShape = RoundedCornerShape(20.dp)

@Composable
fun AppTopBar(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null,
              action: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().background(Edu.Canvas).statusBarsPadding().padding(horizontal=20.dp,vertical=12.dp),
        verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        if (onBack != null) Surface(onClick=onBack, modifier=Modifier.size(44.dp), shape=CircleShape,
            color=Edu.Paper, border=BorderStroke(1.dp,Edu.Line)) {
            Box(contentAlignment=Alignment.Center) { Glyph(EduGlyph.BACK,Modifier.size(20.dp),Edu.Navy) }
        }
        Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(2.dp)) {
            Text(title,color=Edu.Navy,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.ExtraBold,
                maxLines=1,overflow=TextOverflow.Ellipsis)
            if (!subtitle.isNullOrBlank()) Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.labelMedium,
                maxLines=1,overflow=TextOverflow.Ellipsis)
        }
        action?.invoke()
    }
}

@Composable
fun PrimaryButton(text:String, modifier:Modifier=Modifier, enabled:Boolean=true, icon:EduGlyph?=null, onClick:()->Unit) {
    Button(onClick,enabled=enabled,modifier=modifier.heightIn(min=52.dp),shape=RoundedCornerShape(15.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Edu.Navy,contentColor=Color.White,
            disabledContainerColor=Edu.Line,disabledContentColor=Edu.Muted),contentPadding=PaddingValues(horizontal=18.dp,vertical=10.dp)) {
        if(icon!=null){Glyph(icon,Modifier.size(20.dp),Color.White);Spacer(Modifier.width(8.dp))}
        Text(text,fontWeight=FontWeight.Bold,maxLines=1)
    }
}

@Composable
fun SecondaryButton(text:String, modifier:Modifier=Modifier, enabled:Boolean=true, onClick:()->Unit) {
    OutlinedButton(onClick,enabled=enabled,modifier=modifier.heightIn(min=50.dp),border=BorderStroke(1.dp,Edu.Line),
        shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=Edu.Navy)) {
        Text(text,color=Edu.Navy,maxLines=1,fontWeight=FontWeight.SemiBold)
    }
}

@Composable
fun AppCard(modifier:Modifier=Modifier, content:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().background(Edu.Paper,CardShape).padding(18.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp),content=content)
}

@Composable
fun SectionHeader(title:String, subtitle:String?=null, action:String?=null, onAction:(()->Unit)?=null) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
            Text(title,color=Edu.Navy,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.ExtraBold)
            if(!subtitle.isNullOrBlank()) Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodySmall)
        }
        if(action!=null&&onAction!=null) TextButton(onClick=onAction){Text(action,color=Edu.Blue,fontWeight=FontWeight.Bold)}
    }
}

@Composable
fun FeatureCard(title:String,subtitle:String,icon:EduGlyph,modifier:Modifier=Modifier,accent:Color=Edu.Blue,onClick:()->Unit) {
    Surface(onClick=onClick,modifier=modifier,color=Edu.Paper,shape=CardShape,border=BorderStroke(1.dp,Edu.Line)) {
        Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(.12f)),contentAlignment=Alignment.Center){Glyph(icon,Modifier.size(24.dp),accent)}
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(title,color=Edu.Navy,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.bodyLarge)
                Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodySmall,maxLines=2,overflow=TextOverflow.Ellipsis)
            }
            Glyph(EduGlyph.ARROW,Modifier.size(18.dp),Edu.Muted)
        }
    }
}

@Composable
fun StatCard(title:String,value:String,icon:EduGlyph,modifier:Modifier=Modifier,tint:Color=Edu.Blue) {
    Column(modifier.background(Edu.Paper,CardShape).padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Glyph(icon,Modifier.size(22.dp),tint)
        Text(value,style=MaterialTheme.typography.headlineSmall,color=Edu.Navy,fontWeight=FontWeight.Black)
        Text(title,color=Edu.Muted,style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun EmptyState(title:String,subtitle:String,icon:EduGlyph,action:String?=null,onAction:(()->Unit)?=null) {
    Column(Modifier.fillMaxWidth().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(11.dp)) {
        Box(Modifier.size(60.dp).background(Edu.BlueSoft,RoundedCornerShape(18.dp)),contentAlignment=Alignment.Center){Glyph(icon,Modifier.size(28.dp),Edu.Blue)}
        Text(title,fontWeight=FontWeight.ExtraBold,color=Edu.Navy,style=MaterialTheme.typography.titleLarge)
        if(subtitle.isNotBlank()) Text(subtitle,color=Edu.Muted,style=MaterialTheme.typography.bodySmall,textAlign=TextAlign.Center,maxLines=3)
        if(action!=null&&onAction!=null) PrimaryButton(action,icon=EduGlyph.PLUS,onClick=onAction)
    }
}

@Composable
fun LoadingView(label:String,modifier:Modifier=Modifier) {
    Row(modifier.fillMaxWidth().padding(20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.CenterVertically){
        CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=2.dp,color=Edu.Teal);Text(label,color=Edu.Muted)
    }
}

@Composable
fun FilterChipPill(text:String,selected:Boolean,onClick:()->Unit) {
    val surface by animateColorAsState(if(selected)Edu.Navy else Edu.Paper,tween(160),label="chip")
    val ink by animateColorAsState(if(selected)Color.White else Edu.Navy,tween(160),label="chip ink")
    Surface(onClick=onClick,shape=RoundedCornerShape(12.dp),color=surface,border=if(selected)null else BorderStroke(1.dp,Edu.Line)){
        Text(text,Modifier.padding(horizontal=15.dp,vertical=10.dp),color=ink,fontWeight=if(selected)FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun FormField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,singleLine:Boolean=true) {
    OutlinedTextField(value,onChange,modifier=modifier,shape=RoundedCornerShape(14.dp),singleLine=singleLine,label={Text(label)},
        colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=Edu.Blue,unfocusedBorderColor=Edu.Line,focusedLabelColor=Edu.Blue,unfocusedContainerColor=Edu.Paper))
}

@Composable
fun EducationBottomNav(selected:String,onNavigate:(String)->Unit) {
    Surface(Modifier.fillMaxWidth().navigationBarsPadding(),color=Edu.Paper,shadowElevation=10.dp,border=BorderStroke(1.dp,Edu.Line)) {
        Row(Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=8.dp),horizontalArrangement=Arrangement.spacedBy(4.dp),verticalAlignment=Alignment.CenterVertically) {
            listOf(Triple("home","الرئيسية",EduGlyph.DASH),Triple("curricula","المكتبة",EduGlyph.BOOK),Triple("exams","الاختبارات",EduGlyph.EXAM),Triple("tools","الطلاب",EduGlyph.GROUP)).forEach{(route,title,icon)->
                val active=selected==route
                Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).clickable{onNavigate(route)}.padding(vertical=5.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(3.dp)){
                    Glyph(icon,Modifier.size(22.dp),if(active)Edu.Blue else Edu.Muted)
                    Text(title,color=if(active)Edu.Navy else Edu.Muted,style=MaterialTheme.typography.labelSmall,fontWeight=if(active)FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}
