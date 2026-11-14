package dev.atelier.ui

import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.atelier.data.AtelierSettings
import dev.atelier.data.MotionMode
import dev.atelier.wallpaper.AtelierSceneRenderer
import kotlinx.coroutines.delay

/**
 * Thin translucent ink atmosphere behind Home.
 *
 * This is drawn at the UI layer while the installed live wallpaper is
 * visible through our translucent window; it is NOT a second opaque wallpaper.
 * Disabled outside Home, when the Activity is backgrounded, in battery saver,
 * or when the user selects Still/Reduced Motion.
 */
@Composable
fun AtelierCinematicBackdrop(
    settings: AtelierSettings,
    modifier: Modifier = Modifier
) {
    if (!settings.ambientEnabled) return
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val lifecycle by owner.lifecycle.currentStateFlow.collectAsState()
    val power = remember(context) {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    val renderer = remember { AtelierSceneRenderer() }
    val animate = lifecycle.isAtLeast(Lifecycle.State.STARTED) &&
        !power.isPowerSaveMode && !settings.reduceMotion &&
        settings.motionMode != MotionMode.STILL
    var seconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(animate, settings.motionMode) {
        if (animate) {
            val start = SystemClock.elapsedRealtime()
            while (true) {
                seconds = (SystemClock.elapsedRealtime() - start) / 1000f
                delay(if (settings.motionMode == MotionMode.EXPRESSIVE) 42L else 85L)
            }
        } else {
            seconds = 0f
        }
    }
    Canvas(
        modifier.fillMaxSize().graphicsLayer {
            alpha = if (settings.motionMode == MotionMode.EXPRESSIVE) .52f else .28f
        }
    ) {
        drawIntoCanvas {
            renderer.render(
                canvas = it.nativeCanvas,
                scene = settings.wallpaperScene,
                material = settings.material,
                seconds = seconds,
                drawBackground = false
            )
        }
    }
}
