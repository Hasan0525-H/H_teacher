package com.hasan0525.hteacher.ui.premium

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hasan0525.hteacher.HTeacherApplication
import com.hasan0525.hteacher.domain.portfolio.PortfolioCategory
import com.hasan0525.hteacher.ui.portfolio.PortfolioItemUi
import com.hasan0525.hteacher.ui.portfolio.PortfolioViewModel
import com.hasan0525.hteacher.ui.portfolio.PortfolioViewModelFactory
import com.hasan0525.hteacher.data.repository.AppSettings

@Composable
fun ProfessionalPortfolio(onBack:()->Unit,onNavigate:(String)->Unit) {
    val app=LocalContext.current.applicationContext as HTeacherApplication
    val vm:PortfolioViewModel=viewModel(factory=PortfolioViewModelFactory(app))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbar=remember{SnackbarHostState()}
    var showCategories by remember { mutableStateOf(false) }
    var profileEditor by remember{mutableStateOf(false)}
    var add by remember{mutableStateOf(false)}
    var editing by remember{mutableStateOf<PortfolioItemUi?>(null)}
    var deleting by remember{mutableStateOf<PortfolioItemUi?>(null)}
    var removeAttachment by remember{mutableStateOf<Long?>(null)}
    var attachTo by remember{mutableLongStateOf(0L)}
    val attachmentPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri:Uri?->
        if(uri!=null&&attachTo>0L)vm.addAttachment(attachTo,uri)
        attachTo=0L
    }
    val exportPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){uri:Uri?->
        uri?.let(vm::exportPortfolio)
    }
    LaunchedEffect(state.message){state.message?.let{snackbar.showSnackbar(it);vm.clearMessage()}}
    Scaffold(containerColor=Edu.Canvas,
        topBar={AppTopBar("الإنجازات", onBack = onBack,
            action={TextButton(onClick={profileEditor=true}){Text("الملف الشخصي",color=Edu.Blue)}})},
        bottomBar={EducationBottomNav("portfolio",onNavigate)},snackbarHost={SnackbarHost(snackbar)}
    ){pad->
        LazyColumn(Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(horizontal=19.dp,vertical=18.dp),
            verticalArrangement=Arrangement.spacedBy(17.dp)){
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    PrimaryButton("إضافة إنجاز",Modifier.weight(1f),icon=EduGlyph.PLUS){add=true}
                    SecondaryButton(if(state.isExporting)"جارٍ التصدير" else "تصدير PDF",
                        Modifier.weight(1f),enabled=!state.isExporting){
                        exportPicker.launch("ملف-إنجاز.pdf")
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("${state.allItems.size} إنجاز", Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = Edu.Navy, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showCategories = !showCategories }) {
                        Text(if (showCategories) "إخفاء التصنيفات" else "تصفية")
                    }
                }
            }
            if (showCategories) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { FilterChipPill("الكل", state.selectedCategory == null) { vm.selectCategory(null) } }
                        items(PortfolioCategory.entries) { cat ->
                            FilterChipPill(cat.label, state.selectedCategory == cat) {
                                vm.selectCategory(cat)
                            }
                        }
                    }
                }
            }
            if(state.items.isEmpty()) item {
                EmptyState("لا توجد إنجازات","",
                    EduGlyph.FOLDER,"إضافة إنجاز"){add=true}
            } else items(state.items,key={it.item.id}){entry->
                TimelineEntry(
                    heading=entry.item.title,
                    subtitle=PortfolioCategory.fromStorage(entry.item.category).label,
                    modifier=Modifier.fillMaxWidth(),
                    last=false
                ) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={editing=entry}){Text("تعديل")}
                    }
                    if(entry.item.description.isNotBlank()) Text(entry.item.description,color=Edu.Muted)
                    if(entry.attachments.isNotEmpty()) {
                        HorizontalDivider(color=Edu.Line)
                        entry.attachments.forEach{attachment->
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                Glyph(EduGlyph.DOC,Modifier.size(18.dp),Edu.Muted)
                                Spacer(Modifier.width(7.dp))
                                Text(attachment.fileName,Modifier.weight(1f),
                                    maxLines=1,overflow=TextOverflow.Ellipsis,color=Edu.Navy)
                                TextButton(onClick={removeAttachment=attachment.id}){Text("حذف",color=Edu.Error)}
                            }
                        }
                    }
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        TextButton(onClick={
                            attachTo=entry.item.id;attachmentPicker.launch(arrayOf("*/*"))
                        }) {Glyph(EduGlyph.PLUS,Modifier.size(18.dp));Text("إرفاق")}
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={deleting=entry}){Text("حذف الإنجاز",color=Edu.Error)}
                    }
                }
            }
        }
    }
    if(profileEditor) PortfolioProfileEditor(state.profile,{profileEditor=false}){a,b,c,d->
        vm.saveProfile(a,b,c,d);profileEditor=false
    }
    if(add) PortfolioEntryEditor(null,{add=false}){category,title,description->
        vm.addItem(category,title,description);add=false
    }
    editing?.let{entry->PortfolioEntryEditor(entry,{editing=null}){category,title,description->
        vm.updateItem(entry.item.id,category,title,description);editing=null
    }}
    deleting?.let{entry->EducationConfirm("حذف الإنجاز؟",entry.item.title,{deleting=null}){
        vm.deleteItem(entry.item.id);deleting=null
    }}
    removeAttachment?.let{id->EducationConfirm("حذف المرفق؟","سيُحذف الملف المرتبط.",{removeAttachment=null}){
        vm.deleteAttachment(id);removeAttachment=null
    }}
}

@Composable
private fun PortfolioProfileEditor(profile:AppSettings,onDismiss:()->Unit,
    onSave:(String,String,String,String)->Unit) {
    var name by remember{mutableStateOf(profile.teacherName)}
    var school by remember{mutableStateOf(profile.schoolName)}
    var specialist by remember{mutableStateOf(profile.specialization)}
    var job by remember{mutableStateOf(profile.jobTitle)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("الملف الشخصي")},text={
        Column(Modifier.heightIn(max=420.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(9.dp)) {
            FormField("الاسم",name,{name=it},Modifier.fillMaxWidth())
            FormField("المدرسة",school,{school=it},Modifier.fillMaxWidth())
            FormField("التخصص",specialist,{specialist=it},Modifier.fillMaxWidth())
            FormField("المسمى الوظيفي",job,{job=it},Modifier.fillMaxWidth())
        }
    },confirmButton={TextButton(onClick={onSave(name,school,specialist,job)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}

@Composable
private fun PortfolioEntryEditor(entry:PortfolioItemUi?,onDismiss:()->Unit,
    onSave:(PortfolioCategory,String,String)->Unit) {
    var category by remember(entry){mutableStateOf(
        entry?.let{PortfolioCategory.fromStorage(it.item.category)}?:PortfolioCategory.entries.first())}
    var title by remember(entry){mutableStateOf(entry?.item?.title.orEmpty())}
    var desc by remember(entry){mutableStateOf(entry?.item?.description.orEmpty())}
    AlertDialog(onDismissRequest=onDismiss,title={Text(if(entry==null)"إضافة إنجاز" else "تعديل الإنجاز")},text={
        Column(Modifier.heightIn(max=470.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(9.dp)) {
            FormField("عنوان الإنجاز",title,{title=it},Modifier.fillMaxWidth())
            FormField("التفاصيل",desc,{desc=it},Modifier.fillMaxWidth(),singleLine=false)
            Text("التصنيف",color=Edu.Navy,fontWeight=FontWeight.Bold)
            PortfolioCategory.entries.forEach{cat->
                FilterChipPill(cat.label,category==cat){category=cat}
            }
        }
    },confirmButton={TextButton(enabled=title.isNotBlank(),onClick={onSave(category,title,desc)}){Text("حفظ")}},
        dismissButton={TextButton(onClick=onDismiss){Text("إلغاء")}})
}
