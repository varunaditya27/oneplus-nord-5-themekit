package dev.atelier.ui

import java.util.Locale

/**
 * Maps *known functions* to original ATELIER semantics.
 *
 * This mapping deliberately avoids counterfeit brand identity.
 * A generic repository glyph is not a GitHub logo; users who prefer
 * brand recognition can select the ORIGINAL app-icon style instead.
 *
 * Rules are exact-match by package or user-facing application label.
 * Unknown apps return null and fall back to a monogram.
 */
object AtelierIconResolver {
    private val packageRules = mapOf(
        "com.google.android.dialer" to AtelierGlyph.PHONE,
        "com.android.dialer" to AtelierGlyph.PHONE,
        "com.google.android.contacts" to AtelierGlyph.CONTACT,
        "com.android.contacts" to AtelierGlyph.CONTACT,
        "com.google.android.apps.messaging" to AtelierGlyph.MESSAGE,
        "com.google.android.gm" to AtelierGlyph.MAIL,
        "com.google.android.calendar" to AtelierGlyph.CALENDAR,
        "com.android.calendar" to AtelierGlyph.CALENDAR,
        "com.android.settings" to AtelierGlyph.SETTINGS,
        "com.google.android.apps.photos" to AtelierGlyph.GALLERY,
        "com.google.android.youtube" to AtelierGlyph.VIDEO,
        "com.spotify.music" to AtelierGlyph.MUSIC,
        "com.github.android" to AtelierGlyph.REPOSITORY,
        "com.termux" to AtelierGlyph.TERMINAL,
        "com.whatsapp" to AtelierGlyph.MESSAGE,
        "org.telegram.messenger" to AtelierGlyph.SEND,
        "com.google.android.apps.maps" to AtelierGlyph.LOCATION,
        "com.google.android.googlequicksearchbox" to AtelierGlyph.SEARCH,
        "com.google.android.apps.docs" to AtelierGlyph.FOLDER,
        "com.google.android.apps.docs.editors.docs" to AtelierGlyph.FILE_TEXT,
        "com.google.android.apps.docs.editors.sheets" to AtelierGlyph.CHART,
        "com.google.android.apps.docs.editors.slides" to AtelierGlyph.DISPLAY,
        "com.google.android.music" to AtelierGlyph.MUSIC,
        "com.android.camera" to AtelierGlyph.CAMERA,
        "com.oneplus.camera" to AtelierGlyph.CAMERA
    )
    private val labelRules = mapOf(
        "camera" to AtelierGlyph.CAMERA,
        "photos" to AtelierGlyph.GALLERY,
        "gallery" to AtelierGlyph.GALLERY,
        "files" to AtelierGlyph.FOLDER,
        "file manager" to AtelierGlyph.FOLDER,
        "my files" to AtelierGlyph.FOLDER,
        "calendar" to AtelierGlyph.CALENDAR,
        "clock" to AtelierGlyph.CLOCK,
        "alarms" to AtelierGlyph.ALARM,
        "calculator" to AtelierGlyph.CALCULATOR,
        "settings" to AtelierGlyph.SETTINGS,
        "phone" to AtelierGlyph.PHONE,
        "contacts" to AtelierGlyph.CONTACT,
        "messages" to AtelierGlyph.MESSAGE,
        "messaging" to AtelierGlyph.MESSAGE,
        "gmail" to AtelierGlyph.MAIL,
        "mail" to AtelierGlyph.MAIL,
        "email" to AtelierGlyph.MAIL,
        "chrome" to AtelierGlyph.GLOBE,
        "firefox" to AtelierGlyph.GLOBE,
        "browser" to AtelierGlyph.GLOBE,
        "maps" to AtelierGlyph.LOCATION,
        "google maps" to AtelierGlyph.LOCATION,
        "terminal" to AtelierGlyph.TERMINAL,
        "termux" to AtelierGlyph.TERMINAL,
        "github" to AtelierGlyph.REPOSITORY,
        "gitlab" to AtelierGlyph.REPOSITORY,
        "spotify" to AtelierGlyph.MUSIC,
        "youtube" to AtelierGlyph.VIDEO,
        "telegram" to AtelierGlyph.SEND,
        "whatsapp" to AtelierGlyph.MESSAGE,
        "notes" to AtelierGlyph.NOTES,
        "keep notes" to AtelierGlyph.NOTES,
        "recorder" to AtelierGlyph.MICROPHONE,
        "voice recorder" to AtelierGlyph.MICROPHONE,
        "music" to AtelierGlyph.MUSIC,
        "play music" to AtelierGlyph.MUSIC,
        "podcasts" to AtelierGlyph.HEADPHONES,
        "wallet" to AtelierGlyph.WALLET,
        "drive" to AtelierGlyph.FOLDER,
        "one drive" to AtelierGlyph.CLOUD,
        "onedrive" to AtelierGlyph.CLOUD,
        "tasks" to AtelierGlyph.CHECK_LIST,
        "to do" to AtelierGlyph.CHECK_LIST,
        "files by google" to AtelierGlyph.FOLDER,
        "downloads" to AtelierGlyph.DOWNLOAD,
        "calendar+" to AtelierGlyph.CALENDAR
    )

    fun resolve(packageName: String, label: String): AtelierGlyph? {
        val pkg = packageName.trim().lowercase(Locale.ROOT)
        val name = label.trim().lowercase(Locale.ROOT)
        return packageRules[pkg] ?: labelRules[name]
    }
}
