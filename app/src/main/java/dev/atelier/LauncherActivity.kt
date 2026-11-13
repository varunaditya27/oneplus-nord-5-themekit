package dev.atelier

import android.app.RoleManager
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import dev.atelier.data.AppCatalog
import dev.atelier.data.AppItem
import dev.atelier.data.AtelierPreferences
import dev.atelier.ui.AtelierShell
import dev.atelier.ui.AtelierTheme

/** Opt-in launcher: Android remains in charge of the HOME role and recovery path. */
class LauncherActivity : ComponentActivity() {
    private lateinit var catalog: AppCatalog
    private lateinit var preferences: AtelierPreferences
    private var apps by mutableStateOf<List<AppItem>>(emptyList())

    private val requestHome = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* Android determines the new default. No forceful setting changes. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalog = AppCatalog(this)
        preferences = AtelierPreferences(this)

        setContent {
            val settings by preferences.settings.collectAsState()
            AtelierTheme(settings.material) {
                AtelierShell(
                    apps = apps,
                    settings = settings,
                    onUpdate = preferences::update,
                    onFavorite = preferences::toggleFavorite,
                    onLaunch = catalog::launch,
                    onRequestHome = ::requestDefaultHome,
                    onOpenWallpaper = ::openWallpaperPicker
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.refresh()
        apps = catalog.load()
    }

    private fun requestDefaultHome() {
        val roles = getSystemService(RoleManager::class.java)
        if (roles?.isRoleAvailable(RoleManager.ROLE_HOME) == true
            && !roles.isRoleHeld(RoleManager.ROLE_HOME)) {
            requestHome.launch(roles.createRequestRoleIntent(RoleManager.ROLE_HOME))
        }
    }

    private fun openWallpaperPicker() {
        runCatching { startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)) }
            .onFailure { runCatching { startActivity(Intent(Intent.ACTION_SET_WALLPAPER)) } }
    }
}
