package akostenko.aicars

import akostenko.aicars.drawing.Scale
import akostenko.aicars.keyboard.ComboKeyAction
import akostenko.aicars.keyboard.GameAction
import akostenko.aicars.keyboard.KeyboardHelper
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.menu.CollisionsMode
import akostenko.aicars.menu.DebugMode
import akostenko.aicars.menu.Mode
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.track.MonzaTrack
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
    var track: Track = MonzaTrack()
    var mode: Mode = WithPlayer()
    var debug: DebugMode = DebugMode.defaultMode
    var collisions: CollisionsMode = CollisionsMode.defaultMode
    var scale: Scale = Scale(1.0, 5f)

    fun save() {
        GameSettings.save(this)
    }

    companion object {

        private val settingsPath = Paths.get("settings.ini")
        private val populationPath = Paths.get("last.population")
        private val trackToken = "track="
        private val modeToken = "mode="
        private val debugToken = "debug="
        private val collisionsToken = "collisions="
        private val scaleToken = "scale="

        val instance: GameSettings by lazy { restore() }

        private fun save(settings: GameSettings) {
            with(settings) {
                val content = listOf(trackToken + track.title, modeToken + mode.title,
                        debugToken + debug.title, collisionsToken + collisions.title,
                        scaleToken + Scale.serialize(scale))
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
        }

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
                            if (line.startsWith(debugToken)) {
                                settings.debug = DebugMode.forName(line.split(debugToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
                            }
                            if (line.startsWith(collisionsToken)) {
                                settings.collisions = CollisionsMode.forName(line.split(collisionsToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
                            }
                            if (line.startsWith(scaleToken)) {
                                settings.scale = Scale.deserialize(line.split(collisionsToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
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
                    SingleKeyAction({ -> Game.get().noticeAction(GameAction.QUIT) }, KEY_ESCAPE),
                    *ComboKeyAction({ -> Game.get().noticeAction(GameAction.QUIT) }, KEY_Q,
                            predicate = { -> KeyboardHelper.isCtrlDown() }).listeners().toTypedArray(),
                    *ComboKeyAction({ -> Game.get().noticeAction(GameAction.RESTART) }, KEY_R,
                            predicate = { -> KeyboardHelper.isCtrlDown() }).listeners().toTypedArray())
            return defaults
        }
    }
}

