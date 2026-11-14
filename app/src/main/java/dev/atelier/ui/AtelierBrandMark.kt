package dev.atelier.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

/**
 * Canonical ATELIER monogram: a deliberately unbarred geometric A and a
 * single brass register line. Same proportions as the vector brand kit.
 */
@Composable
fun AtelierBrandMark(ink:Color,accent:Color,modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val u=size.minDimension / 256f
        fun pt(x:Float,y:Float)=Offset(x*u,y*u)
        drawLine(ink,pt(43f,170f),pt(114f,28f),12.5f*u,cap=StrokeCap.Square)
        drawLine(ink,pt(114f,28f),pt(185f,170f),12.5f*u,cap=StrokeCap.Square)
        drawLine(accent,pt(142f,92f),pt(205f,92f),7.5f*u,cap=StrokeCap.Square)
    }
}
