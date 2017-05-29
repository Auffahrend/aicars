package akostenko.aicars.race

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.GraphicsGameState
import akostenko.aicars.drawing.Arrow
import akostenko.aicars.drawing.CarImg
import akostenko.aicars.drawing.Line
import akostenko.aicars.drawing.Scale
import akostenko.aicars.drawing.TrackSectionImg
import akostenko.aicars.keyboard.IsKeyDownListener
import akostenko.aicars.keyboard.KeyboardHelper
import akostenko.aicars.math.Decart
import akostenko.aicars.menu.CarPerformanceTests
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.race.car.Car
import akostenko.aicars.race.car.CarTelemetry.Companion.accelerationColor
import akostenko.aicars.race.car.CarTelemetry.Companion.breakingColor
import akostenko.aicars.race.car.CarTelemetry.Companion.textColor
import akostenko.aicars.race.car.CarTelemetryScalar
import akostenko.aicars.race.car.CarTelemetryVector
import akostenko.aicars.track.Track
import akostenko.aicars.track.TrackSection
import org.lwjgl.input.Keyboard.KEY_DOWN
import org.lwjgl.input.Keyboard.KEY_LEFT
import org.lwjgl.input.Keyboard.KEY_RIGHT
import org.lwjgl.input.Keyboard.KEY_UP
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.SlickException
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.StateBasedGame
import java.awt.Font
import java.lang.Math.PI
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class RaceState : GraphicsGameState() {

    private lateinit var cars: MutableCollection<Car<*>>
    private var playerCar: Car<Player>? = null
    private lateinit var track: Track
    private var msSinceLastCollisionDetection = 0
    private var msSinceLastCarUpdates = 0

    private val msBetweenCollisionDetections = 1
    private val msBetweenCarUpdates = 10
    private lateinit var listeners : List<KeyListener>
    private val accelerateListener = IsKeyDownListener(KEY_UP)
    private val brakeListener = IsKeyDownListener(KEY_DOWN)
    private val turnLeftListener = IsKeyDownListener(KEY_LEFT)
    private val turnRightListener = IsKeyDownListener(KEY_RIGHT)
    private val telemetryTextSize = 14
    private val lineWidth = 3f
    private val fatLineWidth = 5f
    private val telemetryFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, telemetryTextSize), true)
    private val scale = Scale(1f, 20f)
    private val trackBorder = Color(100, 100, 100)

    private val executor = Executors.newSingleThreadExecutor()

    override fun getID(): Int {
        return GameStateIds.getId(this::class)
    }

    @Throws(SlickException::class)
    override fun enter(container: GameContainer?, game: StateBasedGame?) {
        super.enter(container, game)
        listeners.forEach { listener -> container!!.input.addKeyListener(listener) }
        reset()
    }

    private fun reset() {
        cars = mutableListOf()
        playerCar = null
        this.track = GameSettings.instance.track
        with(GameSettings.instance) {
            if (mode is WithPlayer) {
                playerCar = Car(Player(), track)
                cars.add(playerCar!!)
            } else if (mode is CarPerformanceTests) {
                (mode as CarPerformanceTests).drivers
                        .forEach { driver -> cars.add(Car(driver, track)) }
            } else {}
        }

        val trackStart = track.sections.first()
        cars.forEach { car -> car.turn(trackStart.heading).move(trackStart.start) }
    }

    @Throws(SlickException::class)
    override fun init(container: GameContainer, game: StateBasedGame) {
        listeners = listOf(accelerateListener, brakeListener, turnLeftListener, turnRightListener)

        container.setTargetFrameRate(100)
        cameraOffset = Decart((Game.screenWidth / 2).toDouble(), (Game.screenHeight / 2).toDouble())
    }

    @Throws(SlickException::class)
    override fun leave(container: GameContainer?, game: StateBasedGame?) {
        super.leave(container, game)
        listeners.forEach { listener -> container!!.input.removeKeyListener(listener) }
    }

    @Throws(SlickException::class)
    override fun render(container: GameContainer, game: StateBasedGame, g: Graphics) {
        val focused = focusedCar()

        drawTrack(g, focused, track)
        cars.forEach { car -> drawCar(g, car, focused) }
        drawCarTelemetry(g, focused)

        if (cars.size > 1) {
            drawDriverPositions(g)
        }
    }

    private fun drawCar(g: Graphics, car: Car<*>, focused: Car<*>) {
        CarImg.build(car, focused.position, textColor, scale)
                .forEach { line -> drawLine(g, line) }
    }

    private fun drawTrack(g: Graphics, focused: Car<*>, track: Track) {
        track.sections.forEach { section -> drawTrackSection(g, focused, section, track.width) }
    }

    private fun drawTrackSection(g: Graphics, focused: Car<*>, section: TrackSection, width: Double) {
        TrackSectionImg.build(section, width, scale, trackBorder, focused.position)
                .forEach { line -> drawLine(g, line) }
    }


    private fun drawDriverPositions(g: Graphics) {
        g.color = textColor
        g.lineWidth = lineWidth
        g.font = telemetryFont

        val carPositions = cars.sortedByDescending { car -> getPositionOnTrack(car, track) }
        carPositions.indices.forEach {
            i -> g.drawString(
                String.format("%d. %s%n", i + 1, carPositions[i].driver.name),
                Game.screenWidth - telemetryLeftMargin - telemetryNameWidth * 1.5f,
                telemetryTopMargin + i * (telemetryTextSize + telemetrySpacing))
        }
    }

    private fun getPositionOnTrack(car: Car<*>, track: Track): Double {
        return car.position.module()
    }

    private val telemetryLeftMargin = 50f
    private val telemetryTopMargin = 50f
    private val telemetryNameWidth = 100f
    private val telemetrySpacing = 5f
    private val telemetryNameX = telemetryLeftMargin + telemetrySpacing
    private val telemetryValueX = telemetryNameX + telemetryNameWidth + telemetrySpacing
    private fun drawCarTelemetry(g: Graphics, car: Car<*>) {
        drawTelemetry(g, car)
        drawDriverInput(g, car.driver)
    }

    private fun drawTelemetry(g: Graphics, car: Car<*>) {
        g.font = telemetryFont
        g.lineWidth = lineWidth

        val currentY = AtomicReference(telemetryTopMargin + telemetrySpacing)

        val telemetry = car.telemetry
        telemetry.scalars.forEach { item -> drawTelemetryScalar(g, item, currentY) }
        telemetry.vectors.forEach { vector -> drawTelemetryVector(g, car, car.position, vector) }

        g.color = textColor
        g.drawRect(telemetryLeftMargin, telemetryTopMargin,
                telemetryValueX + telemetryNameWidth, currentY.get() - telemetrySpacing - telemetryTextSize.toFloat())
    }

    private fun drawTelemetryScalar(g: Graphics, value: CarTelemetryScalar, currentY: AtomicReference<Float>) {
        g.color = value.color
        g.drawString(value.name, telemetryNameX, currentY.get())
        g.drawString(value.textValue(), telemetryValueX, currentY.get())
        currentY.accumulateAndGet(telemetryTextSize + telemetrySpacing, { a, b -> a + b })
    }

    private fun drawTelemetryVector(g: Graphics, car: Car<*>, camera: Decart, item: CarTelemetryVector) {
        val from = (car.position + item.appliedTo - camera) * (scale.pixels / scale.size).toDouble()
        val centerOffset = item.vector * 0.5 * (item.scale.pixels / item.scale.size).toDouble()
        Arrow.build(from + centerOffset,
                item.vector.module().toFloat() * item.scale.pixels / item.scale.size,
                item.vector.toPolar().d, item.color, lineWidth)
                .forEach { line -> drawLine(g, line) }
    }

    private val arrowSize = 30f //px
    private val arrowSpace = 3f //px
    private val grey = Color(40, 40, 40)
    private val arrowsBlock = Decart((Game.screenWidth - arrowSize * 4).toDouble(), (Game.screenHeight - arrowSize * 3).toDouble())
    private val upArrowCenter = arrowsBlock + Decart((arrowSize * 3 / 2).toDouble(), (arrowSize / 2).toDouble())
    private val downArrowCenter = arrowsBlock + Decart((arrowSize * 3 / 2).toDouble(), (arrowSize * 3 / 2).toDouble())
    private val leftArrowCenter = arrowsBlock + Decart((arrowSize * 1 / 2).toDouble(), (arrowSize * 3 / 2).toDouble())
    private val rightArrowCenter = arrowsBlock + Decart((arrowSize * 5 / 2).toDouble(), (arrowSize * 3 / 2).toDouble())

    private fun drawDriverInput(g: Graphics, driver: Driver) {
        Arrow.build(upArrowCenter, arrowSize - arrowSpace * 2, -PI / 2,
                if (driver.accelerating() > 0) accelerationColor else grey,
                if (driver.accelerating() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.build(downArrowCenter, arrowSize - arrowSpace * 2, PI / 2,
                if (driver.breaking() > 0) breakingColor else grey,
                if (driver.breaking() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.build(leftArrowCenter, arrowSize, PI,
                if (turnLeftListener.isDown || driver.steering() < 0) textColor else grey,
                if (turnLeftListener.isDown || driver.steering() < 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.build(rightArrowCenter, arrowSize, 0.0,
                if (turnRightListener.isDown || driver.steering() > 0) textColor else grey,
                if (turnRightListener.isDown || driver.steering() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
    }

    private fun drawUILine(g: Graphics, line: Line) {
        g.lineWidth = line.width
        g.color = line.color
        g.drawLine(
                line.from.x.toFloat(), line.from.y.toFloat(),
                line.to.x.toFloat(), line.to.y.toFloat())
    }

    private fun focusedCar(): Car<*> {
            if (playerCar != null) {
                return playerCar!!
            } else {
                return cars.maxBy { it.trackDistance }!!
            }
        }

    @Throws(SlickException::class)
    override fun update(container: GameContainer, game: StateBasedGame, delta: Int) {
        if (playerCar != null) {
            processInput(playerCar!!.driver, delta)
        }

        msSinceLastCarUpdates += delta
        msSinceLastCollisionDetection += delta

        if (msSinceLastCarUpdates >= msBetweenCarUpdates) {
            cars.forEach { car -> car.update(msSinceLastCarUpdates) }
            //            executor.submit(() -> cars.forEach(car -> car.update(msSinceLastCarUpdates)));
            msSinceLastCarUpdates = 0
        }

        if (msSinceLastCollisionDetection >= msBetweenCollisionDetections) {
            //            executor.submit(() -> cars.forEach(car -> detectCollision(car, msSinceLastCollisionDetection)));
            cars.forEach { car -> detectCollision(car, msSinceLastCollisionDetection) }
            msSinceLastCollisionDetection = 0
        }
    }

    private fun detectCollision(car: Car<*>, msDelta: Int) {

    }

    private fun processInput(player: Player, delta: Int) {
        player.accelerate(accelerateListener.isDown, delta.toDouble())
        player.breaks(brakeListener.isDown, delta.toDouble())
        player.turn(turnLeftListener.isDown, turnRightListener.isDown, delta.toDouble())
    }

}
