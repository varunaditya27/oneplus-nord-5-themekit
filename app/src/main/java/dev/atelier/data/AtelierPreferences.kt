package dev.atelier.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import kotlinx.coroutines.flow.StateFlow

enum class Material { INK, GRAPHITE, SABLE }
enum class ClockStyle { SIGNATURE, COMPACT }
enum class IconStyle { THEMED, SEMANTIC, MONOGRAM, ORIGINAL }
enum class IconWeight { CONTOUR, CORE }
enum class MotionMode { STILL, SIGNATURE, EXPRESSIVE }
enum class WallpaperScene { SILKFIELD, STRATA, ECLIPSE }

data class AtelierSettings(
    val material: Material = Material.INK,
    val clockStyle: ClockStyle = ClockStyle.SIGNATURE,
    val iconStyle: IconStyle = IconStyle.THEMED,
    val iconWeight: IconWeight = IconWeight.CORE,
    val motionMode: MotionMode = MotionMode.EXPRESSIVE,
    val wallpaperScene: WallpaperScene = WallpaperScene.SILKFIELD,
    val parallaxEnabled: Boolean = true,
    val ambientEnabled: Boolean = true,
    val openingSequenceEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val focusMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val showLabels: Boolean = true,
    val favorites: Set<String> = emptySet(),
    val iconOverrides: Map<String,String> = emptyMap()
)

/** Local-only and explicitly reversible; no identity, telemetry or cloud sync. */
class AtelierPreferences(context: Context) {
    private val store = context.getSharedPreferences("atelier.v1", Context.MODE_PRIVATE)
    private val mutable = MutableStateFlow(read())
    val settings: StateFlow<AtelierSettings> = mutable

    private fun read(): AtelierSettings = AtelierSettings(
        material = runCatching { Material.valueOf(store.getString("material", "INK")!!) }.getOrDefault(Material.INK),
        clockStyle = runCatching { ClockStyle.valueOf(store.getString("clock", "SIGNATURE")!!) }.getOrDefault(ClockStyle.SIGNATURE),
        iconStyle = runCatching { IconStyle.valueOf(store.getString("icon_style", "THEMED")!!) }.getOrDefault(IconStyle.THEMED),
        iconWeight = runCatching { IconWeight.valueOf(store.getString("icon_weight", "CORE")!!) }.getOrDefault(IconWeight.CORE),
        motionMode = runCatching {
            MotionMode.valueOf(store.getString("motion_mode", "EXPRESSIVE")!!)
        }.getOrDefault(MotionMode.EXPRESSIVE),
        wallpaperScene = runCatching {
            WallpaperScene.valueOf(store.getString("wallpaper_scene", "SILKFIELD")!!)
        }.getOrDefault(WallpaperScene.SILKFIELD),
        parallaxEnabled = store.getBoolean("parallax_enabled", true),
        ambientEnabled = store.getBoolean("ambient_enabled", true),
        openingSequenceEnabled = store.getBoolean("opening_sequence", true),
        hapticsEnabled = store.getBoolean("haptics_enabled", true),
        focusMode = store.getBoolean("focus", false),
        reduceMotion = store.getBoolean("reduce_motion", false),
        showLabels = store.getBoolean("show_labels", true),
        favorites = store.getStringSet("favorites", emptySet()).orEmpty().toSet(),
        iconOverrides = runCatching {
            val objectValue = JSONObject(store.getString("icon_overrides","{}")!!)
            objectValue.keys().asSequence().associateWith { key ->
                objectValue.optString(key)
            }.filterValues { it.isNotBlank() }
        }.getOrDefault(emptyMap())
    )

    fun refresh() { mutable.value = read() }

    fun observeChanges(): () -> Unit {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> refresh() }
        store.registerOnSharedPreferenceChangeListener(listener)
        return { store.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun update(transform: (AtelierSettings) -> AtelierSettings) {
        val next = transform(mutable.value)
        store.edit()
            .putString("material", next.material.name)
            .putString("clock", next.clockStyle.name)
            .putString("icon_style", next.iconStyle.name)
            .putString("icon_weight", next.iconWeight.name)
            .putString("motion_mode", next.motionMode.name)
            .putString("wallpaper_scene", next.wallpaperScene.name)
            .putBoolean("parallax_enabled", next.parallaxEnabled)
            .putBoolean("ambient_enabled", next.ambientEnabled)
            .putBoolean("opening_sequence", next.openingSequenceEnabled)
            .putBoolean("haptics_enabled", next.hapticsEnabled)
            .putBoolean("focus", next.focusMode)
            .putBoolean("reduce_motion", next.reduceMotion)
            .putBoolean("show_labels", next.showLabels)
            .putStringSet("favorites", next.favorites.toSet())
            .putString("icon_overrides", JSONObject(next.iconOverrides).toString())
            .apply()
        mutable.value = next
    }
    fun toggleFavorite(id: String) = update {
        it.copy(favorites = if (id in it.favorites) it.favorites - id else it.favorites + id)
    }
    fun setFocus(enabled: Boolean) = update { it.copy(focusMode = enabled) }
    fun setAppIcon(id: String, glyphName: String?) = update {
        it.copy(iconOverrides = if(glyphName==null)it.iconOverrides-id
            else it.iconOverrides + (id to glyphName))
    }
}
