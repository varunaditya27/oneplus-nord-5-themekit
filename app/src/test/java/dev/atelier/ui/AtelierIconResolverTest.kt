package dev.atelier.ui

import org.junit.Assert.*
import org.junit.Test

class AtelierIconResolverTest {
    @Test fun semanticAppsAreStable() {
        assertEquals(AtelierGlyph.MAIL, AtelierIconResolver.resolve("com.google.android.gm", "Gmail"))
        assertEquals(AtelierGlyph.TERMINAL, AtelierIconResolver.resolve("com.termux", "Termux"))
        assertEquals(AtelierGlyph.CAMERA, AtelierIconResolver.resolve("unknown", "Camera"))
        assertEquals(AtelierGlyph.CALENDAR, AtelierIconResolver.resolve("unknown", "CALENDAR"))
    }

    @Test fun notCounterfeitBrandMarks() {
        assertEquals(AtelierGlyph.REPOSITORY, AtelierIconResolver.resolve("com.github.android", "GitHub"))
        assertEquals(AtelierGlyph.MUSIC, AtelierIconResolver.resolve("com.spotify.music", "Spotify"))
        assertEquals(AtelierGlyph.MESSAGE, AtelierIconResolver.resolve("com.whatsapp", "WhatsApp"))
    }

    @Test fun unknownNamesDoNotGetIncorrectSymbols() {
        assertNull(AtelierIconResolver.resolve("io.indie.unique", "Abyssal Lines"))
        assertNull(AtelierIconResolver.resolve("io.indie.unique", "Super Calculator Pro"))
        assertNull(AtelierIconResolver.resolve("", ""))
    }
}
