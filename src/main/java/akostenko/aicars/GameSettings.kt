package akostenko.aicars

import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import org.lwjgl.input.Keyboard.KEY_ESCAPE
import org.lwjgl.input.Keyboard.KEY_Q
import org.lwjgl.input.Keyboard.KEY_R

import akostenko.aicars.keyboard.ComboKeyAction
import akostenko.aicars.keyboard.GameAction
import akostenko.aicars.menu.Mode
import akostenko.aicars.track.Track
import akostenko.aicars.keyboard.KeyboardHelper
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.track.StraightTrack

import org.newdawn.slick.KeyListener

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList

class GameSettings {

    var globalListeners: Iterable<KeyListener>? = null
        private set
    private var track: Track = StraightTrack()
    private var mode: Mode = WithPlayer()

    fun save() {
        val content = ArrayList<String>()
        content.add(trackToken + track.title)
        content.add(modeToken + mode.title)
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

    fun getTrack(): Track {
        return track
    }

    fun setTrack(track: Track): GameSettings {
        this.track = track
        return this
    }

    fun setMode(mode: Mode): GameSettings {
        this.mode = mode
        return this
    }

    fun getMode(): Mode {
        return mode
    }

    companion object {

        private val settingsPath = Paths.get("settings.ini")
        private val trackToken = "track="
        private val modeToken = "mode="

        private var instance: GameSettings? = null

        fun get(): GameSettings {
            if (instance == null) {
                instance = restore()
            }
            return instance
        }

        private fun restore(): GameSettings {
            val gameSettings: GameSettings

            if (!Files.exists(settingsPath)) {
                gameSettings = defaultSettings
                gameSettings.save()
            } else {
                gameSettings = fromFile(settingsPath)
            }

            return gameSettings
        }

        private fun fromFile(settingsPath: Path): GameSettings {
            val settings = defaultSettings
            try {
                Files.readAllLines(settingsPath)
                        .forEach { line ->
                            if (line.startsWith(trackToken)) {
                                settings.track = Track.forName(line.split(trackToken.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1])
                            }
                            if (line.startsWith(modeToken)) {
                                settings.mode = Mode.forName(line.split(modeToken.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1])
                            }
                        }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            return settings
        }

        private val defaultSettings: GameSettings
            get() {
                val defaults = GameSettings()
                val defaultBindings = ArrayList<KeyListener>()
                defaultBindings.add(SingleKeyAction({ v -> Game.get().noticeAction(GameAction.QUIT) }, KEY_ESCAPE))
                defaultBindings.addAll(ComboKeyAction({ v -> Game.get().noticeAction(GameAction.QUIT) }, KEY_Q) { v -> KeyboardHelper.isCtrlDown }
                        .listeners())
                defaultBindings.addAll(ComboKeyAction({ v -> Game.get().noticeAction(GameAction.RESTART) }, KEY_R) { v -> KeyboardHelper.isCtrlDown }
                        .listeners())

                defaults.globalListeners = defaultBindings

                return defaults
            }
    }
}
