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
import akostenko.aicars.neural.NNDriver
import akostenko.aicars.neural.NeuralNet
import akostenko.aicars.track.MonzaTrack
import akostenko.aicars.track.Track
import org.apache.commons.io.IOUtils
import org.lwjgl.input.Keyboard.KEY_ESCAPE
import org.lwjgl.input.Keyboard.KEY_Q
import org.lwjgl.input.Keyboard.KEY_R
import org.newdawn.slick.KeyListener
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.PosixFilePermission
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.BiPredicate
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class GameSettings {
    private val log  = LoggerFactory.getLogger(this.javaClass)
    private val timeFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
    private val linksSupported = System.getProperty("os.name")?.toLowerCase()?.contains("windows") == false

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

    fun readPopulation(): List<NNDriver> {
        if (Files.exists(populationsPath) && Files.isDirectory(populationsPath)) {
            val lastPopulation : Path? =
            if (linksSupported) {
                Files.readSymbolicLink(GameSettings.lastPopulation)
            } else {
                findLatestPopulation()
            }
            if (lastPopulation != null && Files.exists(lastPopulation)) {
                BufferedInputStream(FileInputStream(lastPopulation.toFile())).use {
                    return NeuralNet.deserializePopulation(IOUtils.readLines(it, StandardCharsets.UTF_8))
                }
            }
        } else {
            Files.createDirectories(populationsPath);
        }
        log.warn("Population not found under $populationsPath. It will be generated.")
        return NeuralNet.generatePopulation()
    }

    fun savePopulation(population: List<NNDriver>) {
        val path = populationsPath.resolve("p${timeFormat.format(LocalDateTime.now())}.zip")
        BufferedOutputStream(FileOutputStream(path.toFile())).use {
            IOUtils.writeLines(NeuralNet.serializePopulation(population), System.lineSeparator(), it, StandardCharsets.UTF_8)
        }

        if (linksSupported) {
            Files.deleteIfExists(lastPopulation)
            Files.createSymbolicLink(lastPopulation, path)
        }
    }

    private fun findLatestPopulation(): Path? {
        return Files.list(populationsPath)
                .filter {
                    it.run { getName(nameCount - 1).toString() }
                            .run { startsWith("p") && endsWith(".zip") }
                }
                .sorted(reverseOrder())
                .findFirst().orElse(null)
    }

    companion object {

        private val settingsPath = Paths.get("settings.ini")
        private val populationsPath = Paths.get("populations")
        private val lastPopulation = populationsPath.resolve("last")
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
                                settings.scale = Scale.deserialize(line.split(scaleToken.toRegex()).dropLastWhile({ it.isEmpty() })[1])
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

