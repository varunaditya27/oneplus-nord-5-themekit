package dev.atelier.data

import org.junit.Assert.*
import org.junit.Test

class AtelierThemeBackupTest {
    @Test fun roundTripCustomAppearanceAndAppIcons() {
        val original=AtelierSettings(
            material=Material.SABLE,clockStyle=ClockStyle.COMPACT,
            iconStyle=IconStyle.SEMANTIC,iconWeight=IconWeight.CONTOUR,
            motionMode=MotionMode.STILL,wallpaperScene=WallpaperScene.ECLIPSE,
            parallaxEnabled=false,ambientEnabled=false,openingSequenceEnabled=false,
            hapticsEnabled=false,focusMode=true,reduceMotion=true,showLabels=false,
            favorites=setOf("pkg/.Home"),iconOverrides=mapOf("pkg/.Home" to "TERMINAL")
        )
        assertEquals(original,AtelierThemeBackup.decode(AtelierThemeBackup.encode(original)))
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsUnknownFileFormats() {
        AtelierThemeBackup.decode("""{"format":"strange","settings":{}}""")
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsUnreasonablyLargeFiles() {
        AtelierThemeBackup.decode("A".repeat(AtelierThemeBackup.MAX_BYTES+1))
    }
}
