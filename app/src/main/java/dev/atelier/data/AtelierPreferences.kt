package dev.atelier.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Material { INK, GRAPHITE, PAPER }
enum class ClockStyle { SIGNATURE, COMPACT }

data class AtelierSettings(
    val material: Material = Material.INK,
    val clockStyle: ClockStyle = ClockStyle.SIGNATURE,
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
        focusMode = store.getBoolean("focus", false),
        reduceMotion = store.getBoolean("reduce_motion", false),
        showLabels = store.getBoolean("show_labels", true),
        favorites = store.getStringSet("favorites", emptySet()).orEmpty().toSet()
    )

    fun refresh() { mutable.value = read() }

    fun update(transform: (AtelierSettings) -> AtelierSettings) {
        val next = transform(mutable.value)
        store.edit()
            .putString("material", next.material.name)
            .putString("clock", next.clockStyle.name)
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
