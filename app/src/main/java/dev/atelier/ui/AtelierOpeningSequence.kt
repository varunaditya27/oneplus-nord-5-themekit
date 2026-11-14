package dev.atelier.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.atelier.data.AtelierSettings
import dev.atelier.data.MotionMode
import kotlinx.coroutines.delay

/**
 * An optional opening title, not a permanent splash screen.
 * Displays once per Activity state, only in a motion-enabled experience.
 * It can always be skipped immediately by tap or Android Back.
 */
@Composable
fun AtelierOpeningSequence(settings: AtelierSettings) {
    val enabled=settings.openingSequenceEnabled &&
        !settings.reduceMotion && settings.motionMode!=MotionMode.STILL
    var pending by rememberSaveable { mutableStateOf(enabled) }
    var underlineTarget by remember { mutableFloatStateOf(0f) }
    val underline by animateFloatAsState(
        targetValue=underlineTarget,
        animationSpec=tween(if(enabled)700 else 0),
        label="atelier-opening-line"
    )
    val c=colors(settings.material)
    LaunchedEffect(Unit) {
        if (pending) {
            delay(100)
            underlineTarget=1f
            delay(1050)
        }
        pending=false
    }
    BackHandler(enabled=pending) { pending=false }
    AnimatedVisibility(
        visible=pending,
        exit=fadeOut(tween(if(enabled)320 else 0)),
        label="atelier-opening"
    ) {
        Box(
            Modifier.fillMaxSize().background(c.background)
                .clickable { pending=false },
            contentAlignment=Alignment.Center
        ) {
            Column(
                horizontalAlignment=Alignment.CenterHorizontally,
                modifier=Modifier.fillMaxWidth().padding(horizontal=50.dp)
            ) {
                Text("A",fontFamily=AtelierFonts.display,fontSize=124.sp,
                    fontWeight=FontWeight.ExtraLight,color=c.text)
                Spacer(Modifier.height(15.dp))
                Box(Modifier.width(92.dp).height(1.dp).background(c.border)) {
                    Box(Modifier.fillMaxWidth(underline).height(1.dp).background(c.accent))
                }
                Spacer(Modifier.height(27.dp))
                Text("A T E L I E R",fontFamily=AtelierFonts.display,
                    fontSize=19.sp,fontWeight=FontWeight.Light,color=c.text)
                Spacer(Modifier.height(11.dp))
                Text("THE BLACK SYSTEM",fontFamily=AtelierFonts.technical,
                    fontSize=9.sp,color=c.muted,letterSpacing=2.sp)
            }
            Text(
                "TOUCH TO ENTER",
                modifier=Modifier.align(Alignment.BottomCenter).padding(bottom=63.dp),
                fontSize=9.sp,fontFamily=AtelierFonts.technical,
                letterSpacing=1.8.sp,color=c.muted
            )
        }
    }
}
