package dev.atelier.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.atelier.data.Material
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.atelier.black.R


/** Material tokens are shared conceptually with the HTML study and generated assets. */
object Ink {
    val black = Color(0xFF080809)
    val surface = Color(0xFF151516)
    val hairline = Color(0xFF313134)
    val paper = Color(0xFFF2F2EE)
    val quiet = Color(0xFF9C9C9C)
    val brass = Color(0xFFCDA664)
}
object AtelierFonts {
    val display = FontFamily(Font(R.font.hubot_sans))
    val body = FontFamily(Font(R.font.spline_sans))
    val technical = FontFamily(Font(R.font.departure_mono))
}

private val AtelierTypography = Typography(
    displayLarge = TextStyle(fontFamily = AtelierFonts.display, fontWeight = FontWeight.ExtraLight),
    headlineLarge = TextStyle(fontFamily = AtelierFonts.display, fontWeight = FontWeight.Light),
    titleLarge = TextStyle(fontFamily = AtelierFonts.display, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontFamily = AtelierFonts.display),
    bodyLarge = TextStyle(fontFamily = AtelierFonts.body),
    bodyMedium = TextStyle(fontFamily = AtelierFonts.body),
    bodySmall = TextStyle(fontFamily = AtelierFonts.body),
    labelLarge = TextStyle(fontFamily = AtelierFonts.body),
    labelMedium = TextStyle(fontFamily = AtelierFonts.technical)
)

data class AtelierColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val muted: Color,
    val border: Color,
    val accent: Color
)
fun colors(material: Material): AtelierColors = when (material) {
    Material.INK -> AtelierColors(Ink.black, Ink.surface, Ink.paper, Ink.quiet, Ink.hairline, Ink.brass)
    Material.GRAPHITE -> AtelierColors(Color(0xFF111315), Color(0xFF1B1E21),
        Ink.paper, Color(0xFFA4A7AA), Color(0xFF353A3D), Ink.brass)
    Material.PAPER -> AtelierColors(Color(0xFFEAE8E3), Color(0xFFF7F6F3),
        Color(0xFF141414), Color(0xFF626260), Color(0xFFCAC7C1), Color(0xFF9C7540))
}

@Composable
fun AtelierTheme(material: Material, content: @Composable () -> Unit) {
    val c = colors(material)
    val scheme = if (material == Material.PAPER) lightColorScheme(
        primary = c.text, onPrimary = c.background,
        background = c.background, onBackground = c.text,
        surface = c.surface, onSurface = c.text,
        secondary = c.accent
    ) else darkColorScheme(
        primary = c.text, onPrimary = c.background,
        background = c.background, onBackground = c.text,
        surface = c.surface, onSurface = c.text,
        secondary = c.accent
    )
    MaterialTheme(colorScheme = scheme, typography = AtelierTypography, content = content)
}
