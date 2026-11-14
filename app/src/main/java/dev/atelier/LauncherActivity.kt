package dev.atelier

import android.app.role.RoleManager
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import java.io.ByteArrayOutputStream
import dev.atelier.data.AtelierThemeBackup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var appLoadJob: Job? = null
    private var stopPreferenceObservation: (() -> Unit)? = null

    private val exportTheme = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if(uri!=null) runCatching {
            contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(AtelierThemeBackup.encode(preferences.settings.value)
                    .toByteArray(Charsets.UTF_8))
            } ?: error("Could not open selected document")
        }.onSuccess {
            Toast.makeText(this,"ATELIER theme exported",Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this,"Export failed: ${it.message}",Toast.LENGTH_LONG).show()
        }
    }

    private val importTheme = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if(uri!=null) runCatching {
            val bytes=contentResolver.openInputStream(uri)?.use { input ->
                val output=ByteArrayOutputStream()
                val buffer=ByteArray(4096)
                while(true) {
                    val count=input.read(buffer)
                    if(count<0)break
                    require(output.size()+count<=AtelierThemeBackup.MAX_BYTES) {
                        "Theme file is too large"
                    }
                    output.write(buffer,0,count)
                }
                output.toByteArray()
            } ?: error("Could not open selected document")
            val incoming=AtelierThemeBackup.decode(String(bytes,Charsets.UTF_8))
            preferences.update { incoming }
        }.onSuccess {
            Toast.makeText(this,"ATELIER theme restored",Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this,"Import failed: ${it.message}",Toast.LENGTH_LONG).show()
        }
    }

    private val requestHome = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* Android determines the new default. No forceful setting changes. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalog = AppCatalog(this)
        preferences = AtelierPreferences(this)
        stopPreferenceObservation = preferences.observeChanges()

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
                    onOpenWallpaper = ::openWallpaperPicker,
                    onExportTheme = { exportTheme.launch("atelier-theme.json") },
                    onImportTheme = { importTheme.launch(arrayOf("application/json","text/plain")) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.refresh()
        appLoadJob?.cancel()
        appLoadJob = lifecycleScope.launch {
            val discovered = withContext(Dispatchers.IO) { catalog.load() }
            apps = discovered
        }
    }

    override fun onPause() {
        appLoadJob?.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        stopPreferenceObservation?.invoke()
        super.onDestroy()
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
