package dev.atelier.ui

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/**
 * ATELIER original thin-stroke numeral grammar.
 * All 0–9 numerals are vector paths; no display font license required.
 */
@Composable
fun SignatureClock(
    value: String,
    ink: Color,
    modifier: Modifier = Modifier,
    digitWidth: Int = 72,
    digitHeight: Int = 104,
    reducedMotion: Boolean = false
) {
    var isVisible by remember(value) { mutableStateOf(false) }
    LaunchedEffect(value) { isVisible = true }
    val reveal by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 560),
        label = "signature-clock-reveal"
    )
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(0.dp)) {
        value.forEach { digit ->
            if (digit == ':') {
                Canvas(Modifier.width((digitWidth * 0.25f).dp).height(digitHeight.dp)) {
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ink.toArgb() }
                    drawIntoCanvas {
                        val c = it.nativeCanvas
                        c.drawCircle(size.width / 2f, size.height * 0.36f, 2f, paint)
                        c.drawCircle(size.width / 2f, size.height * 0.71f, 2f, paint)
                    }
                }
            } else {
                Canvas(Modifier.width(digitWidth.dp).height(digitHeight.dp)) {
                    val basePath = numeral(digit)
                    val segment = Path()
                    val measure = PathMeasure(basePath, false)
                    measure.getSegment(0f, measure.length * reveal, segment, true)
                    val pen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = ink.toArgb()
                        style = Paint.Style.STROKE
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                        strokeWidth = 1.15f
                    }
                    drawIntoCanvas {
                        it.nativeCanvas.apply {
                            save()
                            scale(size.width / 44f, size.height / 76f)
                            drawPath(segment, pen)
                            restore()
                        }
                    }
                }
            }
        }
    }
}

private fun numeral(d: Char) = Path().apply {
    when (d) {
        '0' -> { moveTo(22f,4f); cubicTo(40f,4f,39f,25f,39f,38f); cubicTo(39f,59f,37f,72f,22f,72f); cubicTo(6f,72f,5f,58f,5f,38f); cubicTo(5f,20f,6f,4f,22f,4f) }
        '1' -> { moveTo(7f,17f); lineTo(25f,4f); lineTo(25f,72f); moveTo(11f,72f); lineTo(38f,72f) }
        '2' -> { moveTo(5f,20f); cubicTo(5f,1f,40f,-2f,39f,22f); cubicTo(39f,37f,23f,45f,6f,72f); lineTo(40f,72f) }
        '3' -> { moveTo(6f,11f); cubicTo(15f,2f,38f,1f,38f,20f); cubicTo(38f,30f,32f,36f,21f,37f); cubicTo(36f,36f,40f,46f,39f,57f); cubicTo(38f,77f,12f,76f,5f,65f) }
        '4' -> { moveTo(33f,72f); lineTo(33f,4f); lineTo(5f,51f); lineTo(41f,51f) }
        '5' -> { moveTo(39f,5f); lineTo(8f,5f); lineTo(5f,35f); cubicTo(32f,22f,41f,41f,39f,57f); cubicTo(37f,78f,11f,75f,5f,63f) }
        '6' -> { moveTo(35f,8f); cubicTo(13f,-4f,5f,25f,5f,45f); cubicTo(5f,67f,14f,73f,24f,72f); cubicTo(49f,70f,42f,34f,24f,35f); cubicTo(15f,35f,10f,40f,5f,46f) }
        '7' -> { moveTo(5f,5f); lineTo(40f,5f); lineTo(15f,72f) }
        '8' -> { moveTo(23f,37f); cubicTo(-1f,38f,2f,3f,22f,4f); cubicTo(42f,4f,46f,36f,23f,37f); cubicTo(-4f,38f,1f,72f,22f,72f); cubicTo(45f,72f,48f,40f,23f,37f) }
        '9' -> { moveTo(39f,31f); cubicTo(39f,6f,30f,4f,21f,4f); cubicTo(-1f,4f,1f,38f,20f,39f); cubicTo(29f,40f,36f,34f,39f,31f); cubicTo(39f,53f,30f,76f,7f,69f) }
    }
}
