package dev.atelier.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.atelier.black.R

enum class AtelierGlyph {
    HOME, LIBRARY, COMMAND, FOCUS, SETTINGS, WIFI, BLUETOOTH, WALLPAPER
}

@DrawableRes
private fun glyphResource(glyph: AtelierGlyph): Int = when (glyph) {
    AtelierGlyph.HOME -> R.drawable.atelier_home
    AtelierGlyph.LIBRARY -> R.drawable.atelier_library
    AtelierGlyph.COMMAND -> R.drawable.atelier_command
    AtelierGlyph.FOCUS -> R.drawable.atelier_focus
    AtelierGlyph.SETTINGS -> R.drawable.atelier_settings
    AtelierGlyph.WIFI -> R.drawable.atelier_wifi
    AtelierGlyph.BLUETOOTH -> R.drawable.atelier_bluetooth
    AtelierGlyph.WALLPAPER -> R.drawable.atelier_wallpaper
}

@Composable
fun AtelierGlyphIcon(
    glyph: AtelierGlyph,
    tint: Color,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Icon(
        painter = painterResource(glyphResource(glyph)),
        contentDescription = description,
        modifier = modifier,
        tint = tint
    )
}
