package dev.atelier.tiles

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dev.atelier.data.AtelierPreferences

/**
 * An optional tile for the launcher-only Focus flag.
 * This does NOT toggle system Do Not Disturb or alter OxygenOS Quick Settings.
 */
class FocusTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        refresh()
    }

    override fun onClick() {
        super.onClick()
        val prefs = AtelierPreferences(applicationContext)
        prefs.setFocus(!prefs.settings.value.focusMode)
        refresh()
    }

    private fun refresh() {
        val enabled = AtelierPreferences(applicationContext).settings.value.focusMode
        qsTile?.apply {
            state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (enabled) "Focus active" else "ATELIER Focus"
            updateTile()
        }
    }
}
