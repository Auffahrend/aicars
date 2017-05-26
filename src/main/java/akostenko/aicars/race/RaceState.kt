package akostenko.aicars.race

import akostenko.aicars.race.car.CarTelemetry.accelerationColor
import akostenko.aicars.race.car.CarTelemetry.breakingColor
import akostenko.aicars.race.car.CarTelemetry.textColor
import java.lang.Math.PI
import java.util.Comparator.comparing
import java.util.Comparator.reverseOrder
import org.lwjgl.input.Keyboard.KEY_DOWN
import org.lwjgl.input.Keyboard.KEY_LEFT
import org.lwjgl.input.Keyboard.KEY_RIGHT
import org.lwjgl.input.Keyboard.KEY_UP

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
import akostenko.aicars.math.Decart
import akostenko.aicars.math.Vector
import akostenko.aicars.menu.CarPerformanceTests
import akostenko.aicars.menu.WithPlayer
import akostenko.aicars.race.car.Car
import akostenko.aicars.race.car.CarTelemetry
import akostenko.aicars.race.car.CarTelemetryScalar
import akostenko.aicars.race.car.CarTelemetryVector
import akostenko.aicars.track.Track
import akostenko.aicars.track.TrackSection
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.SlickException
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.StateBasedGame

import java.awt.*
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.IntStream

class RaceState : GraphicsGameState() {

    private var cars: MutableCollection<Car<*>>? = null
    private var playerCar: Car<Player>? = null
    private var track: Track? = null
    private var msSinceLastCollisionDetection = 0
    private var msSinceLastCarUpdates = 0

    private val msBetweenCollisionDetections = 1
    private val msBetweenCarUpdates = 10
    private val listeners = ArrayList<KeyListener>()
    private val accelerateListener = IsKeyDownListener(KEY_UP)
    private val brakeListener = IsKeyDownListener(KEY_DOWN)
    private val turnLeftListener = IsKeyDownListener(KEY_LEFT)
    private val turnRightListener = IsKeyDownListener(KEY_RIGHT)
    private val telemetryTextSize = 14
    private val lineWidth = 3
    private val fatLineWidth = 5
    private val telemetryFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, telemetryTextSize), true)
    private val scale = Scale(1f, 20f)
    private val trackBorder = Color(100, 100, 100)

    private val executor = Executors.newSingleThreadExecutor()

    override fun getID(): Int {
        return GameStateIds.getId(this.javaClass)
    }

    @Throws(SlickException::class)
    override fun enter(container: GameContainer?, game: StateBasedGame?) {
        super.enter(container, game)
        listeners.forEach { listener -> container!!.input.addKeyListener(listener) }
        reset()
    }

    private fun reset() {
        cars = ArrayList<Car<*>>()
        playerCar = null
        val settings = GameSettings.get()
        track = settings.track
        if (settings.mode is WithPlayer) {
            playerCar = Car(Player(), track)
            cars!!.add(playerCar)
        } else if (settings.mode is CarPerformanceTests) {
            (settings.mode as CarPerformanceTests).drivers
                    .forEach { driver -> cars!!.add(Car(driver, track)) }
        }

        val trackStart = track!!.sections()[0]
        cars!!.forEach { car -> car.turn(trackStart.heading).move(trackStart.start) }
    }

    @Throws(SlickException::class)
    override fun init(container: GameContainer, game: StateBasedGame) {
        listeners.add(accelerateListener)
        listeners.add(brakeListener)
        listeners.add(turnLeftListener)
        listeners.add(turnRightListener)

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
        val focused = focusedCar

        drawTrack(g, focused, track)
        cars!!.forEach { car -> drawCar(g, car, focused) }
        drawCarTelemetry(g, focused)

        if (cars!!.size > 1) {
            drawDriverPositions(g)
        }
    }

    private fun drawCar(g: Graphics, car: Car<*>, focused: Car<*>) {
        CarImg.get(car, focused.position, textColor, scale)
                .forEach { line -> drawLine(g, line) }
    }

    private fun drawTrack(g: Graphics, focused: Car<*>, track: Track) {
        track.sections()
                .forEach { section -> drawTrackSection(g, focused, section, track.width) }
    }

    private fun drawTrackSection(g: Graphics, focused: Car<*>, section: TrackSection, width: Double) {
        TrackSectionImg.get(section, width, scale, trackBorder, focused.position)
                .forEach { line -> drawLine(g, line) }
    }


    private fun drawDriverPositions(g: Graphics) {
        g.color = textColor
        g.lineWidth = lineWidth.toFloat()
        g.font = telemetryFont

        val carPositions = ArrayList(cars!!)
        carPositions.sort(comparing<Car<*>, Double>({ car -> getPositionOnTrack(car, track) }, reverseOrder<Double>()))
        IntStream.range(0, cars!!.size)
                .forEach { i ->
                    g.drawString(
                            String.format("%d. %s%n", i + 1, carPositions[i].driver.name),
                            Game.screenWidth.toFloat() - telemetryLeftMargin - telemetryNameWidth * 1.5f,
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
        g.lineWidth = lineWidth.toFloat()

        val currentY = AtomicReference(telemetryTopMargin + telemetrySpacing)

        val telemetry = car.telemetry
        telemetry.scalars.forEach { item -> drawTelemetryScalar(g, item, currentY) }
        telemetry.vectors.forEach { vector -> drawTelemetryVector(g, car, car.position, vector) }

        g.color = textColor
        g.drawRect(telemetryLeftMargin, telemetryTopMargin,
                telemetryValueX + telemetryNameWidth, currentY.get() - telemetrySpacing - telemetryTextSize.toFloat())
    }

    private fun drawTelemetryScalar(g: Graphics, value: CarTelemetryScalar, currentY: AtomicReference<Float>) {
        g.color = value.color()
        g.drawString(value.name(), telemetryNameX, currentY.get())
        g.drawString(value.value(), telemetryValueX, currentY.get())
        currentY.accumulateAndGet(telemetryTextSize + telemetrySpacing, BinaryOperator<Float> { a, b -> java.lang.Float.sum(a, b) })
    }

    private fun drawTelemetryVector(g: Graphics, car: Car<*>, camera: Decart, item: CarTelemetryVector) {
        val from = car.position.plus(item.appliedTo()).minus(camera).multi((scale.pixels / scale.size).toDouble())
        val centerOffset = item.vector().multi(0.5).multi((item.scale().pixels / item.scale().size).toDouble())
        Arrow.get(from.plus(centerOffset),
                item.vector().module().toFloat() * item.scale().pixels / item.scale().size,
                item.vector().toPolar().d, item.color(), lineWidth)
                .forEach { line -> drawLine(g, line) }
    }

    private val arrowSize = 30f //px
    private val arrowSpace = 3f //px
    private val grey = Color(40, 40, 40)
    private val arrowsBlock = Decart((Game.screenWidth - arrowSize * 4).toDouble(), (Game.screenHeight - arrowSize * 3).toDouble())
    private val upArrowCenter = arrowsBlock.plus(Decart((arrowSize * 3 / 2).toDouble(), (arrowSize / 2).toDouble()))
    private val downArrowCenter = arrowsBlock.plus(Decart((arrowSize * 3 / 2).toDouble(), (arrowSize * 3 / 2).toDouble()))
    private val leftArrowCenter = arrowsBlock.plus(Decart((arrowSize * 1 / 2).toDouble(), (arrowSize * 3 / 2).toDouble()))
    private val rightArrowCenter = arrowsBlock.plus(Decart((arrowSize * 5 / 2).toDouble(), (arrowSize * 3 / 2).toDouble()))

    private fun drawDriverInput(g: Graphics, driver: Driver) {
        Arrow.get(upArrowCenter, arrowSize - arrowSpace * 2, -PI / 2,
                if (driver.accelerating() > 0) accelerationColor else grey,
                if (driver.accelerating() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.get(downArrowCenter, arrowSize - arrowSpace * 2, PI / 2,
                if (driver.breaking() > 0) breakingColor else grey,
                if (driver.breaking() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.get(leftArrowCenter, arrowSize, PI,
                if (turnLeftListener.isDown || driver.steering() < 0) textColor else grey,
                if (turnLeftListener.isDown || driver.steering() < 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
        Arrow.get(rightArrowCenter, arrowSize, 0.0,
                if (turnRightListener.isDown || driver.steering() > 0) textColor else grey,
                if (turnRightListener.isDown || driver.steering() > 0) fatLineWidth else lineWidth)
                .forEach { line -> drawUILine(g, line) }
    }

    private fun drawUILine(g: Graphics, line: Line) {
        g.lineWidth = line.width.toFloat()
        g.color = line.color
        g.drawLine(
                line.from.x.toFloat(), line.from.y.toFloat(),
                line.to.x.toFloat(), line.to.y.toFloat())
    }

    private val focusedCar: Car<*>
        get() {
            if (playerCar != null) {
                return playerCar
            } else {
                return cars!!.stream()
                        .max(comparing<Car<*>, Int>(Function<Car<*>, Int> { it.trackDistance() }))
                        .orElseThrow { IllegalStateException("No cars in game") }
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
            cars!!.forEach { car -> car.update(msSinceLastCarUpdates) }
            //            executor.submit(() -> cars.forEach(car -> car.update(msSinceLastCarUpdates)));
            msSinceLastCarUpdates = 0
        }

        if (msSinceLastCollisionDetection >= msBetweenCollisionDetections) {
            //            executor.submit(() -> cars.forEach(car -> detectCollision(car, msSinceLastCollisionDetection)));
            cars!!.forEach { car -> detectCollision(car, msSinceLastCollisionDetection) }
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
