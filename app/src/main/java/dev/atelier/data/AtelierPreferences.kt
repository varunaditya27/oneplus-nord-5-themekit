package dev.atelier.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Material { INK, GRAPHITE, SABLE }
enum class ClockStyle { SIGNATURE, COMPACT }
enum class IconStyle { SEMANTIC, MONOGRAM, ORIGINAL }
enum class IconWeight { CONTOUR, CORE }

data class AtelierSettings(
    val material: Material = Material.INK,
    val clockStyle: ClockStyle = ClockStyle.SIGNATURE,
    val iconStyle: IconStyle = IconStyle.SEMANTIC,
    val iconWeight: IconWeight = IconWeight.CORE,
    val focusMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val showLabels: Boolean = true,
    val favorites: Set<String> = emptySet()
)

/** Local-only and explicitly reversible; no identity, telemetry or cloud sync. */
class AtelierPreferences(context: Context) {
    private val store = context.getSharedPreferences("atelier.v1", Context.MODE_PRIVATE)
    private val mutable = MutableStateFlow(read())
    val settings: StateFlow<AtelierSettings> = mutable

    private fun read(): AtelierSettings = AtelierSettings(
        material = runCatching { Material.valueOf(store.getString("material", "INK")!!) }.getOrDefault(Material.INK),
        clockStyle = runCatching { ClockStyle.valueOf(store.getString("clock", "SIGNATURE")!!) }.getOrDefault(ClockStyle.SIGNATURE),
        iconStyle = runCatching { IconStyle.valueOf(store.getString("icon_style", "SEMANTIC")!!) }.getOrDefault(IconStyle.SEMANTIC),
        iconWeight = runCatching { IconWeight.valueOf(store.getString("icon_weight", "CORE")!!) }.getOrDefault(IconWeight.CORE),
        focusMode = store.getBoolean("focus", false),
        reduceMotion = store.getBoolean("reduce_motion", false),
        showLabels = store.getBoolean("show_labels", true),
        favorites = store.getStringSet("favorites", emptySet()).orEmpty().toSet()
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
            .putBoolean("focus", next.focusMode)
            .putBoolean("reduce_motion", next.reduceMotion)
            .putBoolean("show_labels", next.showLabels)
            .putStringSet("favorites", next.favorites.toSet())
            .apply()
        mutable.value = next
    }
    fun toggleFavorite(id: String) = update {
        it.copy(favorites = if (id in it.favorites) it.favorites - id else it.favorites + id)
    }
    fun setFocus(enabled: Boolean) = update { it.copy(focusMode = enabled) }
}
