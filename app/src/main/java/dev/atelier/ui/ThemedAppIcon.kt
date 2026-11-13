package dev.atelier.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build

/**
 * Renders an application's own monochrome adaptive icon, when supplied.
 *
 * Android API 33+ feature. No proprietary logo paths are copied into ATELIER;
 * recognition is preserved by using the installed application's drawable.
 *
 * Never mutate the Drawable retained by AppCatalog, since ORIGINAL mode may
 * reuse it later. Missing or incompatible layers fall back to ATELIER glyphs.
 */
object ThemedAppIcon {
    fun rasterize(icon: Drawable, tint: Int, sizePx: Int = 96): Bitmap? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
        val adaptive = icon as? AdaptiveIconDrawable ?: return null
        val source = adaptive.monochrome ?: return null
        val clone = source.constantState?.newDrawable()?.mutate() ?: return null
        return runCatching {
            clone.setTint(tint)
            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            clone.setBounds(0, 0, sizePx, sizePx)
            clone.draw(canvas)
            bitmap
        }.getOrNull()
    }
}
