package akostenko.aicars.race

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.GraphicsGameState
import akostenko.aicars.drawing.Arrow
import akostenko.aicars.drawing.CarImg
import akostenko.aicars.drawing.Scale
import akostenko.aicars.keyboard.IsKeyDownListener
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.menu.CarPerformanceTests
import akostenko.aicars.menu.NeuralNetDemo
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.race.car.Car
import akostenko.aicars.race.car.CarTelemetry.Companion.accelerationColor
import akostenko.aicars.race.car.CarTelemetry.Companion.breakingColor
import akostenko.aicars.race.car.CarTelemetry.Companion.textColor
import akostenko.aicars.race.car.CarTelemetryScalar
import akostenko.aicars.race.car.CarTelemetryVector
import akostenko.aicars.track.Track
import akostenko.aicars.track.TrackSection
import akostenko.math.StraightLine
import akostenko.math.vector.Cartesian
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.pow
import org.lwjgl.input.Keyboard.KEY_ADD
import org.lwjgl.input.Keyboard.KEY_DOWN
import org.lwjgl.input.Keyboard.KEY_LEFT
import org.lwjgl.input.Keyboard.KEY_RIGHT
import org.lwjgl.input.Keyboard.KEY_SUBTRACT
import org.lwjgl.input.Keyboard.KEY_UP
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.SlickException
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.StateBasedGame
import org.slf4j.LoggerFactory
import java.awt.Font
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

class RaceState : GraphicsGameState() {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var cars: MutableCollection<Car<*>>
    private var playerCar: Car<Player>? = null
    private lateinit var track: Track
    private var msSinceLastCarUpdates = 0

    private val msBetweenCarUpdates = 1
    private lateinit var listeners : List<KeyListener>
    private val accelerateListener = IsKeyDownListener(KEY_UP)
    private val brakeListener = IsKeyDownListener(KEY_DOWN)
    private val turnLeftListener = IsKeyDownListener(KEY_LEFT)
    private val turnRightListener = IsKeyDownListener(KEY_RIGHT)
    private val telemetryTextSize = 14
    private val lineWidth = 3f
    private val fatLineWidth = 5f
    private val telemetryFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, telemetryTextSize), true)
    private var scale = Scale(1.0, 5f)
    private val trackBorder = Color(100, 100, 100)

    private val executor = ForkJoinPool.commonPool()

    override fun getID(): Int {
        return GameStateIds.getId(this::class)
    }

    @Throws(SlickException::class)
    override fun enter(container: GameContainer?, game: StateBasedGame?) {
        super.enter(container, game)
        listeners.forEach { listener -> container!!.input.addKeyListener(listener) }
        reset()
        debugTrackSections()
    }

    private fun debugTrackSections() {
        val firstWP = track.sections.first().wayPoints.first().position
        logger.debug("Distance between start and finish: ${(firstWP - track.sections.last().wayPoints.last().position).asPolar()}")
        logger.debug("Minimal distance between start and last section: ${track.sections.last().wayPoints.map{ (firstWP - it.position).module()}.min()}")
        logger.debug("Total track distance: ${track.sections.flatMap { it.wayPoints }.size}")
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
            } else if (mode is NeuralNetDemo) {
                (mode as NeuralNetDemo).drivers
                        .forEach { driver -> cars.add(Car(driver, track)) }
            } else {}
        }
        scale = GameSettings.instance.scale

        val trackStart = track.sections.first()
        cars.forEach { car -> car.turn(trackStart.heading).move(trackStart.start) }
    }

    @Throws(SlickException::class)
    override fun init(container: GameContainer, game: StateBasedGame) {
        listeners = listOf(accelerateListener, brakeListener, turnLeftListener, turnRightListener,
                SingleKeyAction({ changeScale(+1) }, KEY_ADD), SingleKeyAction({ changeScale(-1) }, KEY_SUBTRACT))

        container.setTargetFrameRate(100)
        cameraOffset = Cartesian((Game.screenWidth / 2).toDouble(), (Game.screenHeight / 2).toDouble())
    }

    private fun changeScale(step: Int) {
        scale = Scale(scale.size, scale.pixels * pow(1.5, step.toDouble()).toFloat())
        if (scale.pixels < 2) scale = Scale(scale.size * 2, scale.pixels * 2)
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

    private fun drawCar(g: Graphics, car: Car<*>, camera: Car<*>) {
        CarImg.build(car)
                .forEach { line -> drawRealLine(g, line, camera.position, scale, textColor, 3f) }
        val lineToClosestTrackMarker = StraightLine(car.position, car.closestWP.position.asCartesian(), false)
        drawRealLine(g, lineToClosestTrackMarker, camera.position, scale, breakingColor, 2f)
    }

    private fun drawTrack(g: Graphics, focused: Car<*>, track: Track) {
        track.sections
                .forEach { drawTrackSection(g, focused, it) }

        track.markers
                .forEach { drawTrackMarker(g, it, focused.position, scale, Color.white, 2.5f) }
    }

    private var visibilityRadius = scale.from(Game.screenWidth)
    private fun isVisible(section: TrackSection, camera: Cartesian): Boolean {
        return section.wayPoints.any { (it.position-camera).module() < visibilityRadius }
    }

    private fun drawTrackSection(g: Graphics, focused: Car<*>, section: TrackSection) {
        section.borders
                .forEach { line -> drawRealLine(g, line, focused.position, scale, trackBorder, 3f) }
    }

    private fun drawDriverPositions(g: Graphics) {
        g.color = textColor
        g.lineWidth = lineWidth
        g.font = telemetryFont

        val carPositions = cars.sortedByDescending { getPositionOnTrack(it) }
        carPositions.indices.forEach {
            i -> g.drawString(
                String.format("%d. %s%n", i + 1, carPositions[i].driver.name),
                Game.screenWidth - telemetryLeftMargin - telemetryNameWidth * 1.5f,
                telemetryTopMargin + i * (telemetryTextSize + telemetrySpacing))
        }
    }

    private fun getPositionOnTrack(car: Car<*>): Double {
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

    private fun drawTelemetryVector(g: Graphics, car: Car<*>, camera: Cartesian, item: CarTelemetryVector) {
        val from = scale.to(car.position + item.appliedTo - camera).asCartesian() + cameraOffset
        val centerOffset = item.scale.to(item.vector * 0.5)
        Arrow.build(from + centerOffset, item.scale.to(item.vector.module()), item.vector.asPolar().d, lineWidth)
                .forEach { line -> drawUILine(g, line, item.color, lineWidth) }
    }

    private val arrowSize = 30.0 //px
    private val arrowSpace = 3f //px
    private val grey = Color(40, 40, 40)
    private val arrowsBlock = Cartesian((Game.screenWidth - arrowSize * 4), (Game.screenHeight - arrowSize * 3))
    private val upArrowCenter = arrowsBlock + Cartesian((arrowSize * 3 / 2), (arrowSize / 2))
    private val downArrowCenter = arrowsBlock + Cartesian((arrowSize * 3 / 2), (arrowSize * 3 / 2))
    private val leftArrowCenter = arrowsBlock + Cartesian((arrowSize * 1 / 2), (arrowSize * 3 / 2))
    private val rightArrowCenter = arrowsBlock + Cartesian((arrowSize * 5 / 2), (arrowSize * 3 / 2))

    private fun drawDriverInput(g: Graphics, driver: Driver) {
        val accelerationLinesWidth = if (driver.accelerating() > 0) fatLineWidth else lineWidth
        Arrow.build(upArrowCenter, arrowSize.toFloat() - arrowSpace * 2, -PI / 2, accelerationLinesWidth)
                .forEach { line -> drawUILine(g, line,
                        if (driver.accelerating() > 0) accelerationColor else grey,
                        accelerationLinesWidth) }

        val breakingLinesWidth = if (driver.breaking() > 0) fatLineWidth else lineWidth
        Arrow.build(downArrowCenter, arrowSize.toFloat() - arrowSpace * 2, PI / 2, breakingLinesWidth)
                .forEach { line -> drawUILine(g, line,
                        if (driver.breaking() > 0) breakingColor else grey,
                        breakingLinesWidth) }

        val leftLinesWidth = if (turnLeftListener.isDown || driver.steering() < 0) fatLineWidth else lineWidth
        Arrow.build(leftArrowCenter, arrowSize.toFloat(), PI, leftLinesWidth)
                .forEach { line -> drawUILine(g, line,
                        if (turnLeftListener.isDown || driver.steering() < 0) textColor else grey,
                        leftLinesWidth) }

        val rightLinesWidth = if (turnRightListener.isDown || driver.steering() > 0) fatLineWidth else lineWidth
        Arrow.build(rightArrowCenter, arrowSize.toFloat(), 0.0, rightLinesWidth)
                .forEach { line -> drawUILine(g, line,
                        if (turnRightListener.isDown || driver.steering() > 0) textColor else grey,
                        rightLinesWidth) }
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

        cars.map {car -> executor.submit({ car.update(msSinceLastCarUpdates) } )}
                .forEach { it.join()}
        msSinceLastCarUpdates = 0
    }

    private fun processInput(player: Player, delta: Int) {
        player.accelerate(accelerateListener.isDown, delta.toDouble())
        player.breaks(brakeListener.isDown, delta.toDouble())
        player.turn(turnLeftListener.isDown, turnRightListener.isDown, delta.toDouble())
    }

}
