package dev.atelier.ui

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import dev.atelier.data.*
import dev.atelier.domain.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

private enum class Page { HOME, LIBRARY, COMMAND, CONTROL, SETTINGS }

@Composable
fun AtelierShell(
    apps: List<AppItem>,
    settings: AtelierSettings,
    onUpdate: ((AtelierSettings) -> AtelierSettings) -> Unit,
    onFavorite: (String) -> Unit,
    onLaunch: (AppItem) -> Boolean,
    onRequestHome: () -> Unit,
    onOpenWallpaper: () -> Unit
) {
    val c = colors(settings.material)
    var page by remember { mutableStateOf(Page.HOME) }
    BackHandler(enabled = page != Page.HOME) { page = Page.HOME }
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) { now = LocalDateTime.now(); delay(10000) }
    }
    fun execute(cmd: Command) {
        when(cmd) {
            Command.FocusOn -> onUpdate { it.copy(focusMode = true) }
            Command.FocusOff -> onUpdate { it.copy(focusMode = false) }
            Command.OpenDrawer -> page = Page.LIBRARY
            Command.OpenSettings -> page = Page.SETTINGS
            Command.OpenWallpaper -> onOpenWallpaper()
            Command.ToggleClock -> onUpdate { it.copy(clockStyle = if(it.clockStyle == ClockStyle.COMPACT) ClockStyle.SIGNATURE else ClockStyle.COMPACT) }
            is Command.Launch -> {
                val match = apps.firstOrNull { it.name.equals(cmd.query, true) }
                    ?: apps.firstOrNull { it.name.contains(cmd.query, true) }
                if (match != null) onLaunch(match) else page = Page.LIBRARY
            }
            Command.Unknown -> Unit
        }
    }
    Column(Modifier.fillMaxSize().background(c.background).statusBarsPadding().navigationBarsPadding().padding(horizontal=24.dp)) {
        Row(Modifier.fillMaxWidth().height(58.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
            Text("ATELIER", color=c.text, fontSize=12.sp, letterSpacing=4.sp, fontWeight=FontWeight.Medium, fontFamily=AtelierFonts.display)
            Text(if(page==Page.HOME) "INTERFACE / 001" else "CLOSE ×", color=c.muted, fontSize=10.sp,
                fontFamily=AtelierFonts.technical, modifier=Modifier.clickable { page=Page.HOME })
        }
        Box(Modifier.weight(1f)) {
            Crossfade(page, animationSpec=tween(if(settings.reduceMotion) 0 else 220), label="page") { current ->
                when(current) {
                    Page.HOME -> Home(c,now,settings,apps,onLaunch,{page=Page.COMMAND},{page=Page.LIBRARY},{page=Page.CONTROL})
                    Page.LIBRARY -> Library(c,apps,settings,onFavorite,onLaunch)
                    Page.COMMAND -> CommandPage(c,apps,::execute,onLaunch)
                    Page.CONTROL -> Control(c,settings,onUpdate,onOpenWallpaper)
                    Page.SETTINGS -> SettingsPage(c,settings,onUpdate,onRequestHome,onOpenWallpaper)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.border))
        Row(Modifier.fillMaxWidth().height(68.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            listOf("HOME","APPS","CMD","CTRL","SET").forEachIndexed { index,label ->
                val selected = page.ordinal==index
                Column(Modifier.clickable { page=Page.entries[index] }.padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                    AtelierGlyphIcon(listOf(AtelierGlyph.HOME,AtelierGlyph.LIBRARY,AtelierGlyph.COMMAND,AtelierGlyph.FOCUS,AtelierGlyph.SETTINGS)[index],
                    tint=if(selected)c.accent else c.muted,modifier=Modifier.size(18.dp))
                    Text(label,color=if(selected)c.text else c.muted,fontSize=9.sp,letterSpacing=1.sp)
                }
            }
        }
    }
}
@Composable
private fun Home(c:AtelierColors,now:LocalDateTime,settings:AtelierSettings,apps:List<AppItem>,
    launch:(AppItem)->Boolean,command:()->Unit,library:()->Unit,control:()->Unit) {
    Column(
        Modifier.fillMaxSize().pointerInput(library,control) {
            var distance = 0f
            detectVerticalDragGestures(
                onDragStart = { distance = 0f },
                onVerticalDrag = { change,amount ->
                    distance += amount
                    change.consume()
                },
                onDragEnd = {
                    if (distance < -85f) library()
                    if (distance > 85f) control()
                    distance = 0f
                },
                onDragCancel = { distance = 0f }
            )
        },
        verticalArrangement=Arrangement.SpaceBetween
    ) {
        Column(Modifier.padding(top=32.dp)) {
            Text("THE BLACK SYSTEM",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
            Spacer(Modifier.height(28.dp))
            if(settings.clockStyle==ClockStyle.SIGNATURE) {
                SignatureClock(now.format(DateTimeFormatter.ofPattern("HH")),c.text,digitWidth=65,digitHeight=90,reducedMotion=settings.reduceMotion)
                SignatureClock(now.format(DateTimeFormatter.ofPattern("mm")),c.text,digitWidth=65,digitHeight=90,reducedMotion=settings.reduceMotion)
            } else Text(now.format(DateTimeFormatter.ofPattern("HH:mm")),color=c.text,fontWeight=FontWeight.ExtraLight,fontSize=72.sp,fontFamily=AtelierFonts.display)
            Spacer(Modifier.height(24.dp))
            Box(Modifier.width(32.dp).height(2.dp).background(c.accent))
            Spacer(Modifier.height(16.dp))
            Text(now.format(DateTimeFormatter.ofPattern("EEEE, dd MMM")).uppercase(),color=c.text,fontSize=12.sp,letterSpacing=1.6.sp)
            Text(if(settings.focusMode) "FOCUS / ACTIVE" else "WORKSPACE / READY",color=c.muted,fontSize=10.sp,fontFamily=AtelierFonts.technical)
        }
        Column {
            Text("TODAY / WORKSPACE",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
            Spacer(Modifier.height(9.dp))
            Text(if(settings.focusMode) "Make room for the work." else "Everything in its place.",
                color=c.text,fontSize=22.sp,fontWeight=FontWeight.Light,fontFamily=AtelierFonts.display)
            Spacer(Modifier.height(23.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.border))
            Spacer(Modifier.height(23.dp))
            val favored=apps.filter { it.id in settings.favorites }.take(4).ifEmpty { apps.take(4) }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                favored.forEach { AppCell(it,c,settings.showLabels,false,settings.iconStyle,{launch(it)},{}) }
            }
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surface)
                .border(1.dp,c.border,RoundedCornerShape(14.dp)).clickable(onClick=command).padding(17.dp),
                verticalAlignment=Alignment.CenterVertically) {
                Text("›",color=c.accent,fontSize=24.sp)
                Spacer(Modifier.width(13.dp))
                Text("Search, launch or command",color=c.muted,fontSize=12.sp)
            }
            Spacer(Modifier.height(18.dp))
            Text("SWIPE TO APPS  /  "+apps.size+" INSTALLED",color=c.muted,fontSize=9.sp,
                modifier=Modifier.clickable(onClick=library),letterSpacing=1.sp)
            Spacer(Modifier.height(13.dp))
        }
    }
}
@Composable
private fun Library(c:AtelierColors,apps:List<AppItem>,s:AtelierSettings,
    favorite:(String)->Unit,launch:(AppItem)->Boolean) {
    var query by remember { mutableStateOf("") }
    val filtered=remember(apps,query) { apps.filter { it.name.contains(query,true) } }
    Column {
        Heading(c,"Library","APPLICATIONS / "+apps.size)
        SearchBox(query,{query=it},"Search installed apps",c)
        Text("Long-press an icon to pin or unpin it from Home",color=c.muted,fontSize=10.sp)
        Spacer(Modifier.height(18.dp))
        LazyVerticalGrid(columns=GridCells.Fixed(4),horizontalArrangement=Arrangement.spacedBy(6.dp),
            verticalArrangement=Arrangement.spacedBy(21.dp),contentPadding=PaddingValues(bottom=16.dp)) {
            items(filtered,key={it.id}) { app ->
                AppCell(app,c,true,app.id in s.favorites,s.iconStyle,{launch(app)},{favorite(app.id)})
            }
        }
    }
}
@Composable
private fun CommandPage(c:AtelierColors,apps:List<AppItem>,execute:(Command)->Unit,launch:(AppItem)->Boolean) {
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Heading(c,"Command","ACTIONS / LAUNCH / SEARCH")
        SearchBox(query,{query=it},"/focus on or GitHub",c,onSubmit={execute(CommandEngine.parse(query))})
        Spacer(Modifier.height(22.dp))
        Text("AVAILABLE ACTIONS",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
        CommandEngine.suggest(query).forEach { action ->
            Row(Modifier.fillMaxWidth().clickable { execute(CommandEngine.parse(action.trigger)) }.padding(vertical=13.dp),
                horizontalArrangement=Arrangement.SpaceBetween) {
                Column {
                    Text(action.label,color=c.text,fontSize=14.sp)
                    Text(action.description,color=c.muted,fontSize=10.sp)
                }
                Text(action.trigger,color=c.accent,fontSize=10.sp,fontFamily=AtelierFonts.technical)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.border))
        }
        if(query.isNotBlank() && !query.startsWith("/")) {
            apps.filter { it.name.contains(query,true) }.take(6).forEach { app ->
                Text(app.name,color=c.text,modifier=Modifier.fillMaxWidth().clickable{launch(app)}.padding(vertical=12.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick={execute(CommandEngine.parse(query))},modifier=Modifier.fillMaxWidth(),
            colors=ButtonDefaults.buttonColors(containerColor=c.text,contentColor=c.background)) {
            Text("EXECUTE",letterSpacing=2.sp)
        }
    }
}
@Composable
private fun Control(c:AtelierColors,s:AtelierSettings,update:((AtelierSettings)->AtelierSettings)->Unit,wallpaper:()->Unit) {
    val context=LocalContext.current
    Column {
        Heading(c,"Control","SHELL CONTROLS / NOT SYSTEMUI")
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            ControlTile(c,"Focus",s.focusMode,AtelierGlyph.FOCUS,Modifier.weight(1f)) {update{it.copy(focusMode=!it.focusMode)}}
            ControlTile(c,"Wi-Fi",false,AtelierGlyph.WIFI,Modifier.weight(1f)) {
                runCatching{context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))}
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            ControlTile(c,"Bluetooth",false,AtelierGlyph.BLUETOOTH,Modifier.weight(1f)) {
                runCatching{context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))}
            }
            ControlTile(c,"Wallpaper",false,AtelierGlyph.WALLPAPER,Modifier.weight(1f),wallpaper)
        }
        Spacer(Modifier.height(25.dp))
        Text("MATERIAL",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
        MaterialPicker(c,s,update)
        Spacer(Modifier.height(25.dp))
        Text("ATELIER controls its own Focus state. The OxygenOS notification shade remains system-owned.",
            color=c.muted,fontSize=12.sp,lineHeight=19.sp)
    }
}
@Composable
private fun ControlTile(c:AtelierColors,title:String,active:Boolean,glyph:AtelierGlyph,modifier:Modifier,onClick:()->Unit) {
    Column(modifier.height(122.dp).clip(RoundedCornerShape(16.dp))
        .background(if(active)c.text else c.surface)
        .border(1.dp,if(active)c.text else c.border,RoundedCornerShape(16.dp))
        .clickable(onClick=onClick).padding(16.dp),verticalArrangement=Arrangement.SpaceBetween) {
        AtelierGlyphIcon(glyph,tint=if(active)c.background else c.text,modifier=Modifier.size(23.dp))
        Column {
            Text(title,color=if(active)c.background else c.text,fontSize=15.sp)
            Text(if(active)"ACTIVE" else if(title=="Focus") "OFF" else "OPEN SETTINGS",
                color=if(active)c.background else c.muted,fontSize=10.sp)
        }
    }
}
@Composable
private fun SettingsPage(c:AtelierColors,s:AtelierSettings,update:((AtelierSettings)->AtelierSettings)->Unit,
    request:()->Unit,wallpaper:()->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Heading(c,"Settings","ATELIER / 0.1 PRE-ALPHA")
        Text("APPEARANCE",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
        MaterialPicker(c,s,update)
        SwitchRow(c,"Reduced motion",s.reduceMotion){v->update{it.copy(reduceMotion=v)}}
        SwitchRow(c,"Show labels",s.showLabels){v->update{it.copy(showLabels=v)}}
        SwitchRow(c,"ATELIER monograms",s.iconStyle==IconStyle.MONOGRAM){v->
            update{it.copy(iconStyle=if(v)IconStyle.MONOGRAM else IconStyle.ORIGINAL)}
        }
        SwitchRow(c,"Compact clock",s.clockStyle==ClockStyle.COMPACT){v->
            update{it.copy(clockStyle=if(v)ClockStyle.COMPACT else ClockStyle.SIGNATURE)}
        }
        Spacer(Modifier.height(24.dp))
        Text("INTEGRATION",color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
        OutlinedButton(onClick=request,modifier=Modifier.fillMaxWidth()){Text("REQUEST DEFAULT HOME ROLE",color=c.text,fontSize=11.sp)}
        OutlinedButton(onClick=wallpaper,modifier=Modifier.fillMaxWidth()){Text("CHOOSE SILKFIELD WALLPAPER",color=c.text,fontSize=11.sp)}
        Spacer(Modifier.height(18.dp))
        Text("The secure lock screen, AOD, biometric UI and system Quick Settings remain controlled by OxygenOS.",
            color=c.muted,fontSize=12.sp,lineHeight=19.sp)
    }
}
@Composable
private fun SwitchRow(c:AtelierColors,label:String,checked:Boolean,onChange:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically) {
        Text(label,modifier=Modifier.weight(1f),color=c.text,fontSize=13.sp)
        Switch(checked=checked,onCheckedChange=onChange,
            colors=SwitchDefaults.colors(checkedTrackColor=c.accent))
    }
}
@Composable
private fun MaterialPicker(c:AtelierColors,s:AtelierSettings,update:((AtelierSettings)->AtelierSettings)->Unit) {
    Row(Modifier.fillMaxWidth().padding(top=13.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Material.entries.forEach { material ->
            val m=colors(material)
            Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(c.surface)
                .border(1.dp,if(s.material==material)c.accent else c.border,RoundedCornerShape(12.dp))
                .clickable{update{it.copy(material=material)}}.padding(10.dp)) {
                Box(Modifier.fillMaxWidth().height(32.dp).background(m.background))
                Spacer(Modifier.height(8.dp))
                Text(material.name,color=c.text,fontSize=10.sp)
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppCell(app:AppItem,c:AtelierColors,showLabel:Boolean,favorite:Boolean,iconStyle:IconStyle,onClick:()->Unit,onLongClick:()->Unit) {
    val haptics = LocalHapticFeedback.current
    Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.width(70.dp)) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(c.surface)
            .border(1.dp,if(favorite)c.accent else c.border,RoundedCornerShape(14.dp))
            .combinedClickable(onClick=onClick,onLongClick={haptics.performHapticFeedback(HapticFeedbackType.LongPress);onLongClick()},onLongClickLabel="Pin or unpin app"),contentAlignment=Alignment.Center) {
            val bmp=remember(app.id,iconStyle){
                if(iconStyle==IconStyle.ORIGINAL) runCatching{app.icon.toBitmap(width=72,height=72).asImageBitmap()}.getOrNull()
                else null
            }
            if(bmp!=null) Image(bitmap=bmp,contentDescription=app.name,modifier=Modifier.size(30.dp))
            else Text(app.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color=c.text,fontSize=23.sp,fontFamily=AtelierFonts.display,fontWeight=FontWeight.Light)
        }
        if(showLabel) {
            Spacer(Modifier.height(7.dp))
            Text(app.name,color=c.muted,fontSize=10.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
        }
    }
}
@Composable
private fun Heading(c:AtelierColors,title:String,description:String) {
    Spacer(Modifier.height(24.dp))
    Text(title,color=c.text,fontSize=36.sp,fontWeight=FontWeight.Light,fontFamily=AtelierFonts.display)
    Text(description,color=c.muted,fontSize=10.sp,letterSpacing=2.sp)
    Spacer(Modifier.height(27.dp))
}
@Composable
private fun SearchBox(value:String,onChange:(String)->Unit,hint:String,c:AtelierColors,onSubmit:()->Unit={}) {
    TextField(value=value,onValueChange=onChange,modifier=Modifier.fillMaxWidth(),singleLine=true,
        keyboardOptions=KeyboardOptions(imeAction=ImeAction.Search),
        keyboardActions=KeyboardActions(onSearch=onSubmit),
        placeholder={Text(hint,color=c.muted,fontSize=12.sp)},shape=RoundedCornerShape(13.dp),
        colors=TextFieldDefaults.colors(
            focusedContainerColor=c.surface,unfocusedContainerColor=c.surface,
            focusedTextColor=c.text,unfocusedTextColor=c.text,
            focusedIndicatorColor=c.accent,unfocusedIndicatorColor=Color.Transparent,
            cursorColor=c.accent
        ))
}
