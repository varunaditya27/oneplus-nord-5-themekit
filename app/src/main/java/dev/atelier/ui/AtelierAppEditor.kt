package dev.atelier.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.atelier.data.AppItem
import dev.atelier.data.AtelierSettings

/**
 * In-app icon customization. The user chooses a real glyph from the complete
 * catalog; no external icon pack or root access is required.
 *
 * Favorites and glyph selection are independent, with an explicit reset.
 */
@Composable
fun AtelierAppEditor(
    app: AppItem,
    settings: AtelierSettings,
    c: AtelierColors,
    onDismiss: () -> Unit,
    onPin: () -> Unit,
    onSetIcon: (AtelierGlyph?) -> Unit
) {
    var query by remember(app.id) { mutableStateOf("") }
    val glyphs=remember(query) {
        AtelierGlyph.entries.filter {
            it.name.contains(query.trim(),ignoreCase=true)
        }
    }
    Dialog(onDismissRequest=onDismiss) {
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(c.surface)
                .border(1.dp,c.border,RoundedCornerShape(21.dp))
                .padding(19.dp)
        ) {
            Text("APPLICATION / PERSONALIZE",color=c.accent,
                fontFamily=AtelierFonts.technical,fontSize=10.sp)
            Spacer(Modifier.height(12.dp))
            Text(app.name,color=c.text,fontFamily=AtelierFonts.display,
                fontSize=27.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                TextButton(
                    onClick=onPin,
                    modifier=Modifier.weight(1f)
                ) {
                    Text(if(app.id in settings.favorites)"UNPIN" else "PIN TO HOME",
                        color=c.text,fontSize=10.sp,fontFamily=AtelierFonts.technical)
                }
                TextButton(
                    onClick={onSetIcon(null)},
                    modifier=Modifier.weight(1f)
                ) {
                    Text("RESET ICON",color=c.accent,
                        fontSize=10.sp,fontFamily=AtelierFonts.technical)
                }
            }
            OutlinedTextField(
                value=query,onValueChange={query=it},
                modifier=Modifier.fillMaxWidth(),
                singleLine=true,
                placeholder={Text("Find an icon…",color=c.muted,fontSize=12.sp)},
                label={Text("155 / SEMANTIC GLYPHS",color=c.muted,fontSize=10.sp)}
            )
            Spacer(Modifier.height(14.dp))
            LazyVerticalGrid(
                columns=GridCells.Fixed(5),
                modifier=Modifier.fillMaxWidth().heightIn(max=345.dp),
                verticalArrangement=Arrangement.spacedBy(8.dp),
                horizontalArrangement=Arrangement.spacedBy(6.dp),
                contentPadding=PaddingValues(bottom=8.dp)
            ) {
                items(glyphs,key={it.name}) { glyph ->
                    val selected=settings.iconOverrides[app.id]==glyph.name
                    Column(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if(selected)c.border else c.background)
                            .border(1.dp,if(selected)c.accent else c.border,
                                RoundedCornerShape(10.dp))
                            .clickable{onSetIcon(glyph)}
                            .padding(vertical=10.dp,horizontal=2.dp),
                        horizontalAlignment=Alignment.CenterHorizontally
                    ) {
                        AtelierGlyphIcon(
                            glyph,tint=c.text,modifier=Modifier.size(23.dp),
                            description=glyph.name,
                            weight=AtelierIconWeight.CORE
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(glyph.name.replace('_',' ').lowercase(),
                            color=if(selected)c.text else c.muted,
                            fontSize=7.sp,maxLines=1,overflow=TextOverflow.Ellipsis)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Your selection persists locally. Original app artwork remains available.",
                color=c.muted,fontSize=10.sp,lineHeight=15.sp)
            Spacer(Modifier.height(13.dp))
            TextButton(onClick=onDismiss,modifier=Modifier.align(Alignment.End)) {
                Text("DONE",color=c.accent,
                    fontFamily=AtelierFonts.technical,fontSize=12.sp)
            }
        }
    }
}
