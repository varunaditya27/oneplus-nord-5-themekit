package dev.atelier.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atelier.data.AtelierSettings
import dev.atelier.data.WallpaperScene
import dev.atelier.wallpaper.AtelierSceneRenderer

/**
 * Miniature rendered scenes; the picker uses the same procedural renderer
 * as the real wallpaper. No fabricated thumbnails or downloaded stock art.
 */
@Composable
fun AtelierScenePicker(
    c: AtelierColors,
    settings: AtelierSettings,
    update: ((AtelierSettings) -> AtelierSettings) -> Unit
) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
        WallpaperScene.entries.forEach { scene ->
            val thumbnail=remember(scene,settings.material) {
                Bitmap.createBitmap(300,250,Bitmap.Config.ARGB_8888).let { bmp ->
                    AtelierSceneRenderer().render(
                        Canvas(bmp),scene,settings.material,0f
                    )
                    bmp.asImageBitmap()
                }
            }
            val selected=settings.wallpaperScene==scene
            Column(
                Modifier.weight(1f).border(1.dp,if(selected)c.accent else c.border,
                    RoundedCornerShape(12.dp))
                    .background(c.surface,RoundedCornerShape(12.dp))
                    .atelierPressable(settings.reduceMotion,
                        onClick={update{it.copy(wallpaperScene=scene)}},
                        mode=settings.motionMode)
                    .padding(6.dp)
            ) {
                Image(
                    bitmap=thumbnail,contentDescription=scene.name,
                    contentScale=ContentScale.Crop,
                    modifier=Modifier.fillMaxWidth().height(76.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    scene.name,color=c.text,fontSize=9.sp,
                    fontFamily=AtelierFonts.technical,maxLines=1
                )
            }
        }
    }
}
