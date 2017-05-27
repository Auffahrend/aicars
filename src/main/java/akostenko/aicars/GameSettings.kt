package akostenko.aicars

import akostenko.aicars.keyboard.ComboKeyAction
import akostenko.aicars.keyboard.GameAction
import akostenko.aicars.keyboard.KeyboardHelper
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.menu.Mode
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.track.StraightTrack
import akostenko.aicars.track.Track
import org.lwjgl.input.Keyboard.KEY_ESCAPE
import org.lwjgl.input.Keyboard.KEY_Q
import org.lwjgl.input.Keyboard.KEY_R
import org.newdawn.slick.KeyListener
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE

class GameSettings {

    var globalListeners: List<KeyListener> = emptyList()
        private set
    var track: Track = StraightTrack()
    var mode: Mode = WithPlayer()

    fun save() {
        val content = listOf(trackToken + track.title, modeToken + mode.title)
        try {
            if (Files.exists(settingsPath)) {
                Files.write(settingsPath, content, TRUNCATE_EXISTING, WRITE)
            } else {
                Files.write(settingsPath, content, CREATE_NEW, WRITE)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    companion object {

        private val settingsPath = Paths.get("settings.ini")
        private val trackToken = "track="
        private val modeToken = "mode="

        val instance: GameSettings by lazy { restore() }

        private fun restore(): GameSettings {
            val gameSettings: GameSettings

            if (!Files.exists(settingsPath)) {
                gameSettings = defaultSettings()
                gameSettings.save()
            } else {
                gameSettings = fromFile(settingsPath)
            }

            return gameSettings
        }

        private fun fromFile(settingsPath: Path): GameSettings {
            val settings = defaultSettings()
            try {
                Files.readAllLines(settingsPath)
                        .forEach { line ->
                            if (line.startsWith(trackToken)) {
                                settings.track = Track.forName(line.split(trackToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
                            }
                            if (line.startsWith(modeToken)) {
                                settings.mode = Mode.forName(line.split(modeToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
                            }
                        }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            return settings
        }

        private fun defaultSettings(): GameSettings {
            val defaults = GameSettings()

            defaults.globalListeners = listOf(
                    SingleKeyAction({ -> Game.get().noticeAction(GameAction.QUIT)}, KEY_ESCAPE),
                    *ComboKeyAction({ -> Game.get().noticeAction(GameAction.QUIT) }, KEY_Q, predicate = {KeyboardHelper.isCtrlDown}).listeners(),
                    *ComboKeyAction({ -> Game.get().noticeAction(GameAction.RESTART) }, KEY_R, predicate = {KeyboardHelper.isCtrlDown}).listeners())

            return defaults
        }
    }
}
