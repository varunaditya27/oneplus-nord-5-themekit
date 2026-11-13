package dev.atelier.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.UserHandle

data class AppItem(
    val id: String,
    val name: String,
    val component: ComponentName,
    val profile: UserHandle,
    val icon: Drawable
)

/** Use launcher-visible activities. No QUERY_ALL_PACKAGES permission. */
class AppCatalog(context: Context) {
    private val launcher = context.getSystemService(LauncherApps::class.java)

    fun load(): List<AppItem> = runCatching {
        launcher.profiles.flatMap { profile ->
            launcher.getActivityList(null, profile).map { activity ->
                AppItem(
                    id = "${profile.hashCode()}:${activity.componentName.flattenToString()}",
                    name = activity.label.toString(),
                    component = activity.componentName,
                    profile = profile,
                    icon = activity.getIcon(0)
                )
            }
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }.getOrDefault(emptyList())

    fun launch(app: AppItem): Boolean = runCatching {
        launcher.startMainActivity(app.component, app.profile, null, null)
        true
    }.getOrDefault(false)
}
