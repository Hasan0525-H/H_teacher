package com.hasan0525.hteacher.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable fun AppCard(modifier:Modifier=Modifier,content:@Composable()->Unit)=Card(modifier.fillMaxWidth(),shape=MaterialTheme.shapes.large,colors=CardDefaults.cardColors(MaterialTheme.colorScheme.surface),elevation=CardDefaults.cardElevation(1.dp),content=content)
@Composable fun PrimaryButton(text:String,modifier:Modifier=Modifier,enabled:Boolean=true,onClick:()->Unit)=Button(modifier=modifier,enabled=enabled,onClick=onClick,contentPadding=PaddingValues(horizontal=18.dp,vertical=12.dp)){Text(text)}
@Composable fun SectionHeader(title:String,subtitle:String?=null,modifier:Modifier=Modifier){Column(modifier,verticalArrangement=Arrangement.spacedBy(3.dp)){Text(title,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);subtitle?.let{Text(it,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
@Composable fun StatCard(title:String,value:String,icon:ImageVector,modifier:Modifier=Modifier){AppCard(modifier){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Surface(shape=MaterialTheme.shapes.medium,color=MaterialTheme.colorScheme.primaryContainer){Icon(icon,null,Modifier.padding(10.dp).size(22.dp),tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.size(12.dp));Column{Text(value,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Text(title,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}
@Composable fun EmptyState(title:String,message:String?=null,modifier:Modifier=Modifier){AppCard(modifier){Column(Modifier.fillMaxWidth().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(6.dp)){Text(title,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold);message?.let{Text(it,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}
@Composable fun LoadingView(modifier:Modifier=Modifier,label:String="جارٍ التحميل..."){Column(modifier.fillMaxWidth().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){CircularProgressIndicator(Modifier.size(28.dp),strokeWidth=3.dp);Text(label,color=MaterialTheme.colorScheme.onSurfaceVariant)}}