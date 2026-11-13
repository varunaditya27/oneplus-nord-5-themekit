package dev.atelier.domain

import org.junit.Assert.*
import org.junit.Test

class CommandEngineTest {
    @Test fun focusOnOff() {
        assertEquals(Command.FocusOn, CommandEngine.parse("/focus on"))
        assertEquals(Command.FocusOff, CommandEngine.parse("focus off"))
    }
    @Test fun ordinaryInputSearchesApps() {
        assertEquals(Command.Launch("github"), CommandEngine.parse(" GitHub "))
        assertEquals(Command.Launch("terminal"), CommandEngine.parse("> Terminal"))
    }
    @Test fun slashCommandsNeverBecomeShellCommands() {
        assertEquals(Command.Unknown, CommandEngine.parse("/rm -rf /"))
        assertEquals(Command.Unknown, CommandEngine.parse(""))
    }
    @Test fun suggestionsAreDiscoverable() {
        assertTrue(CommandEngine.suggest("focus").size >= 2)
        assertEquals(Command.OpenDrawer, CommandEngine.parse("/"))
    }
}
