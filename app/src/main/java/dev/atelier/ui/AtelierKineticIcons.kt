package dev.atelier.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atelier.data.AtelierSettings
import dev.atelier.data.MotionMode

enum class KineticGlyph { PLAY_PAUSE, MENU_CLOSE, SOUND_MUTE }

/** Animated when, and only when, the icon's semantic state changes. */
@Composable
fun AtelierKineticIcon(
    glyph:KineticGlyph, active:Boolean, c:AtelierColors,
    settings:AtelierSettings, modifier:Modifier=Modifier
) {
    val progress by animateFloatAsState(
        targetValue=if(active)1f else 0f,
        animationSpec=tween(
            if(settings.reduceMotion || settings.motionMode==MotionMode.STILL)0
            else if(settings.motionMode==MotionMode.EXPRESSIVE)370 else 235
        ),
        label="semantic-glyph-state"
    )
    Canvas(modifier) {
        val u=size.minDimension/24f
        val w=1.65f*u
        fun p(x:Float,y:Float)=Offset(x*u,y*u)
        when(glyph) {
            KineticGlyph.PLAY_PAUSE -> {
                val play=Path().apply {
                    moveTo(7f*u,5f*u)
                    lineTo(19f*u,12f*u)
                    lineTo(7f*u,19f*u)
                    close()
                }
                drawPath(play,c.text,alpha=1f-progress,
                    style=Stroke(width=w,cap=StrokeCap.Round))
                drawLine(c.text,p(8f,5f),p(8f,19f),3f*u,alpha=progress,cap=StrokeCap.Round)
                drawLine(c.text,p(16f,5f),p(16f,19f),3f*u,alpha=progress,cap=StrokeCap.Round)
            }
            KineticGlyph.MENU_CLOSE -> {
                rotate(45f*progress,pivot=p(12f,12f)) {
                    drawLine(c.text,p(5f,7f+5f*progress),
                        p(19f,7f+5f*progress),w,cap=StrokeCap.Round)
                }
                drawLine(c.text,p(5f,12f),p(19f,12f),
                    w,alpha=1f-progress,cap=StrokeCap.Round)
                rotate(-45f*progress,pivot=p(12f,12f)) {
                    drawLine(c.text,p(5f,17f-5f*progress),
                        p(19f,17f-5f*progress),w,cap=StrokeCap.Round)
                }
            }
            KineticGlyph.SOUND_MUTE -> {
                val speaker=Path().apply {
                    moveTo(3f*u,9f*u);lineTo(7f*u,9f*u);lineTo(12f*u,5f*u)
                    lineTo(12f*u,19f*u);lineTo(7f*u,15f*u)
                    lineTo(3f*u,15f*u);close()
                }
                drawPath(speaker,c.text,style=Stroke(width=w,cap=StrokeCap.Round))
                val wave=Path().apply {
                    moveTo(15f*u,8.5f*u)
                    cubicTo(18f*u,10f*u,18f*u,14f*u,15f*u,15.5f*u)
                    moveTo(18f*u,5f*u)
                    cubicTo(23f*u,9f*u,23f*u,15f*u,18f*u,19f*u)
                }
                drawPath(wave,c.text,alpha=1f-progress,style=Stroke(width=w,cap=StrokeCap.Round))
                drawLine(c.text,p(15f,9f),p(21f,15f),w,
                    alpha=progress,cap=StrokeCap.Round)
                drawLine(c.text,p(21f,9f),p(15f,15f),w,
                    alpha=progress,cap=StrokeCap.Round)
            }
        }
    }
}

@Composable
fun AtelierKineticGallery(c:AtelierColors,settings:AtelierSettings) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        KineticGlyph.entries.forEach { glyph ->
            var active by remember { mutableStateOf(false) }
            val haptics=LocalHapticFeedback.current
            Column(
                Modifier.weight(1f)
                    .border(1.dp,c.border,RoundedCornerShape(13.dp))
                    .background(c.surface,RoundedCornerShape(13.dp))
                    .atelierPressable(
                        settings.reduceMotion,
                        onClick={
                            active=!active
                            if(settings.hapticsEnabled)haptics.performHapticFeedback(
                                if(active)HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                            )
                        },
                        mode=settings.motionMode
                    )
                    .padding(vertical=17.dp,horizontal=5.dp),
                horizontalAlignment=Alignment.CenterHorizontally
            ) {
                AtelierKineticIcon(glyph,active,c,settings,Modifier.size(33.dp))
                Spacer(Modifier.height(13.dp))
                Text(
                    when(glyph) {
                        KineticGlyph.PLAY_PAUSE -> "PLAY / PAUSE"
                        KineticGlyph.MENU_CLOSE -> "MENU / CLOSE"
                        KineticGlyph.SOUND_MUTE -> "SOUND / MUTE"
                    },
                    color=c.text,fontFamily=AtelierFonts.technical,fontSize=8.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(if(active)"STATE 02" else "STATE 01",
                    color=c.muted,fontSize=9.sp)
            }
        }
    }
}
