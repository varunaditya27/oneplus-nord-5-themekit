package dev.atelier.domain

/** No arbitrary shell execution. Only supported ATELIER actions can be dispatched. */
sealed interface Command {
    data class Launch(val query: String) : Command
    data object FocusOn : Command
    data object FocusOff : Command
    data object OpenSettings : Command
    data object OpenDrawer : Command
    data object OpenWallpaper : Command
    data object ToggleClock : Command
    data object Unknown : Command
}

data class CommandSuggestion(val trigger: String, val label: String, val description: String)

object CommandEngine {
    val catalog = listOf(
        CommandSuggestion("/focus on", "Enter focus", "Reduce distractions"),
        CommandSuggestion("/focus off", "Exit focus", "Restore your workspace"),
        CommandSuggestion("/apps", "Application library", "Find any installed application"),
        CommandSuggestion("/settings", "ATELIER settings", "Material and motion"),
        CommandSuggestion("/wallpaper", "Choose Silkfield", "Open the wallpaper selector"),
        CommandSuggestion("/clock", "Toggle clock style", "Signature or compact")
    )

    fun parse(input: String): Command {
        val text = input.trim().lowercase()
        return when (text) {
            "/focus on", "focus on" -> Command.FocusOn
            "/focus off", "focus off" -> Command.FocusOff
            "/settings", "settings" -> Command.OpenSettings
            "/apps", "apps", "/" -> Command.OpenDrawer
            "/wallpaper", "wallpaper" -> Command.OpenWallpaper
            "/clock", "clock" -> Command.ToggleClock
            "" -> Command.Unknown
            else -> if (text.startsWith("/")) Command.Unknown
                    else Command.Launch(text.removePrefix(">").trim())
        }
    }

    fun suggest(input: String): List<CommandSuggestion> {
        val q = input.trim().lowercase().removePrefix("/")
        return if (q.isEmpty()) catalog else catalog.filter {
            it.trigger.lowercase().contains(q) || it.label.lowercase().contains(q)
        }
    }
}
