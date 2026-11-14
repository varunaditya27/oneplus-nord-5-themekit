package dev.atelier.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import dev.atelier.data.Material
import dev.atelier.data.WallpaperScene
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Three original procedural scenes, rendered with the platform Canvas.
 *
 * No image loops, texture files or network assets. Each frame is driven by
 * elapsed time and wallpaper offsets, so it is deterministic and reversible.
 * Rendering is deliberately sparse to keep motion readable beneath the UI.
 */
class AtelierSceneRenderer {
    private val pen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val path = Path()
    private val rect = RectF()

    fun render(
        canvas: Canvas,
        scene: WallpaperScene,
        material: Material,
        seconds: Float,
        offsetX: Float = .5f,
        offsetY: Float = .5f,
        touchX: Float = -1f,
        touchY: Float = -1f,
        rippleAge: Float = 5f,
        drawBackground: Boolean = true
    ) {
        val w = canvas.width.toFloat().coerceAtLeast(1f)
        val h = canvas.height.toFloat().coerceAtLeast(1f)
        if(drawBackground) canvas.drawColor(
            when(material) {
                Material.INK -> Color.rgb(8,8,9)
                Material.GRAPHITE -> Color.rgb(17,19,21)
                Material.SABLE -> Color.rgb(16,15,14)
            }
        )
        canvas.save()
        canvas.translate((offsetX - .5f) * w * .038f, (offsetY - .5f) * h * .016f)
        when(scene) {
            WallpaperScene.SILKFIELD -> silkfield(canvas,w,h,seconds)
            WallpaperScene.STRATA -> strata(canvas,w,h,seconds)
            WallpaperScene.ECLIPSE -> eclipse(canvas,w,h,seconds)
        }
        canvas.restore()
        if(touchX >= 0f && touchY >= 0f && rippleAge in 0f..1.3f) {
            val progress = rippleAge / 1.3f
            pen.shader = null
            pen.color = Color.argb(((1-progress) * 58).toInt(),205,166,100)
            pen.strokeWidth = 1.15f
            canvas.drawCircle(touchX,touchY,(28+170*progress)*w/1080f,pen)
        }
        pen.shader = null
        fill.shader = null
    }

    private fun silkfield(canvas:Canvas,w:Float,h:Float,t:Float) {
        // Satin fold: one atmospheric volume, never a gradient over the UI.
        path.reset()
        path.moveTo(w*.03f,h*1.04f)
        path.cubicTo(w*.19f,h*.76f,w*.56f,h*.45f,w*.57f,h*.12f)
        path.lineTo(w*.77f,h*.15f)
        path.cubicTo(w*.76f,h*.49f,w*.44f,h*.79f,w*.33f,h*1.04f)
        path.close()
        fill.shader = LinearGradient(
            w*.24f,h*.87f,w*.65f,h*.31f,
            intArrayOf(
                Color.TRANSPARENT,
                Color.argb(77,66,62,58),
                Color.argb(25,30,31,33),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f,.36f,.78f,1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(path,fill)
        fill.shader = null
        // Layered satin-like curves, each offset minutely to suggest depth.
        for (i in 0..68) {
            val p=i/68f
            val drift=sin((t*.095f + i*.043f).toDouble()).toFloat()*w*.009f
            path.reset()
            path.moveTo(w*(.02f+p*.42f)+drift,h*1.04f)
            path.cubicTo(
                w*(.14f+p*.22f)+drift,h*.72f,
                w*(.52f+p*.12f)+drift,h*.48f,
                w*(.56f+p*.16f)+drift,h*.15f
            )
            val shade=(38+(i%11)*3.0f).toInt()
            pen.shader=null
            pen.color=Color.argb(if(i%13==0) 70 else 28,shade,shade,shade+3)
            pen.strokeWidth=if(i%17==0) 1.35f else .7f
            canvas.drawPath(path,pen)
        }
        // An intentionally singular gold contour, not a neon highlight.
        path.reset()
        path.moveTo(w*.11f,h*.95f)
        path.cubicTo(w*.34f,h*.77f,w*.68f,h*.52f,w*.73f,h*.16f)
        pen.color=Color.argb(132,205,166,100)
        pen.strokeWidth=1.35f
        canvas.drawPath(path,pen)
    }

    private fun strata(canvas:Canvas,w:Float,h:Float,t:Float) {
        // Geological contour ridges at distinct depths. No random particles.
        fill.shader = LinearGradient(0f,h*.4f,w,h,
            intArrayOf(Color.TRANSPARENT, Color.argb(48,34,35,37),Color.TRANSPARENT),
            floatArrayOf(0f,.67f,1f),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,h*.35f,w,h,fill)
        fill.shader=null
        for (layer in 0..44) {
            val p=layer/44f
            val drift=sin(t*.055f+layer*.09f)*h*.002f
            val depth=h*(.75f+p*.34f)+drift
            path.reset()
            path.moveTo(-w*.12f,depth+h*.12f)
            path.cubicTo(w*.19f,depth-h*.28f*(1-p),
                w*.28f,depth-h*.21f*(1-p),
                w*.48f,depth-h*.095f*(1-p))
            path.cubicTo(w*.72f,depth+h*.09f*(1-p),
                w*.87f,depth-h*.24f*(1-p),
                w*1.12f,depth-h*.16f*(1-p))
            val intensity=(38*(1-p)).toInt().coerceAtLeast(8)
            pen.shader=null
            pen.color=Color.argb(intensity,136,137,139)
            pen.strokeWidth=if(layer%9==0) 1.15f else .65f
            canvas.drawPath(path,pen)
        }
        path.reset()
        path.moveTo(-w*.1f,h*.78f)
        path.cubicTo(w*.2f,h*.53f,w*.32f,h*.69f,w*.47f,h*.67f)
        path.cubicTo(w*.75f,h*.74f,w*.83f,h*.5f,w*1.1f,h*.62f)
        pen.color=Color.argb(82,205,166,100)
        pen.strokeWidth=1.25f
        canvas.drawPath(path,pen)
    }

    private fun eclipse(canvas:Canvas,w:Float,h:Float,t:Float) {
        // A partial luminous orbital edge, closer to watchmaking than sci-fi.
        val cx=w*.77f
        val cy=h*.39f
        val radius=w*.68f
        fill.shader=RadialGradient(cx,cy,radius*1.24f,
            intArrayOf(Color.argb(40,82,72,57),Color.argb(15,36,37,40),Color.TRANSPARENT),
            floatArrayOf(0f,.62f,1f),Shader.TileMode.CLAMP)
        canvas.drawCircle(cx,cy,radius*1.24f,fill)
        fill.shader=null
        rect.set(cx-radius,cy-radius,cx+radius,cy+radius)
        for(i in 0..6) {
            val spread=i*2.5f
            rect.inset(-spread,-spread)
            pen.color=Color.argb((58-i*7).coerceAtLeast(4),170+i*4,147+i*2,110)
            pen.strokeWidth=if(i==0) 1.35f else .65f
            val tilt=sin(t*.028f)*1.8f
            canvas.drawArc(rect,123f+tilt,167f,false,pen)
            rect.inset(spread,spread)
        }
        pen.color=Color.argb(22,185,185,185)
        pen.strokeWidth=.8f
        for(i in 1..12) {
            val r=radius*(.8f+i*.023f)
            rect.set(cx-r,cy-r,cx+r,cy+r)
            canvas.drawArc(rect,117f+i*.6f,170f,false,pen)
        }
    }
}
