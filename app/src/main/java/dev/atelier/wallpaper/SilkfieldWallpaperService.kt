package dev.atelier.wallpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlin.math.sin

/**
 * Silkfield, a mathematically generated atmosphere of graphite strands.
 * Stays idle when hidden. A CPU Canvas prototype, not yet a GPU shader engine.
 */
class SilkfieldWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = SilkfieldEngine()

    inner class SilkfieldEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private var destroyed = false
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
        private val start = SystemClock.elapsedRealtime()
        private val animation = Runnable { drawFrame() }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            handler.removeCallbacks(animation)
            if (visible) drawFrame()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            if (visible) drawFrame()
        }

        private fun drawFrame() {
            if (!visible || destroyed) return
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) render(canvas, (SystemClock.elapsedRealtime() - start) / 1000f)
            } catch (_: IllegalArgumentException) {
                // Surface was removed before a frame could be locked.
            } finally {
                if (canvas != null) runCatching { surfaceHolder.unlockCanvasAndPost(canvas) }
            }
            handler.removeCallbacks(animation)
            if (visible && !destroyed) handler.postDelayed(animation, 100L)
        }

        private fun render(canvas: Canvas, seconds: Float) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            canvas.drawColor(android.graphics.Color.rgb(8, 8, 9))
            for (i in 0..80) {
                val p = i / 80f
                val shift = sin((seconds * .08f + i * .031f).toDouble()).toFloat() * w * .004f
                val path = Path().apply {
                    moveTo(w * (.19f + p * .30f) + shift, h * 1.02f)
                    cubicTo(
                        w * (.20f + p * .23f) + shift, h * .70f,
                        w * (.55f + p * .13f) + shift, h * .49f,
                        w * (.54f + p * .15f) + shift, h * .28f
                    )
                }
                val grey = (20 + (i % 11) * 2.4f).toInt()
                paint.color = android.graphics.Color.argb(if (i % 13 == 0) 52 else 20, grey, grey, grey + 2)
                paint.strokeWidth = if (i % 11 == 0) 1.3f else .75f
                canvas.drawPath(path, paint)
            }
            val brassEdge = Path().apply {
                moveTo(w * .27f, h * .91f)
                cubicTo(w * .40f, h * .76f, w * .65f, h * .58f, w * .68f, h * .30f)
            }
            paint.color = android.graphics.Color.argb(55, 205, 166, 100)
            paint.strokeWidth = 1.05f
            canvas.drawPath(brassEdge, paint)
        }

        override fun onDestroy() {
            destroyed = true
            visible = false
            handler.removeCallbacks(animation)
            super.onDestroy()
        }
    }
}
