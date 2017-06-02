package akostenko.aicars.plots

import akostenko.aicars.Game.Companion.screenHeight
import akostenko.aicars.Game.Companion.screenWidth
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.GraphicsGameState
import akostenko.aicars.drawing.Arrow
import akostenko.aicars.drawing.StraightLine
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.math.Decart
import akostenko.aicars.math.MathUtils
import akostenko.aicars.math.Polar
import akostenko.aicars.model.CarModel
import akostenko.aicars.model.CarModel.maxSteering
import akostenko.aicars.model.EnvironmentModel.SECONDS_PER_MINUTE
import akostenko.aicars.model.EnvironmentModel.g
import org.lwjgl.input.Keyboard.KEY_0
import org.lwjgl.input.Keyboard.KEY_ADD
import org.lwjgl.input.Keyboard.KEY_DOWN
import org.lwjgl.input.Keyboard.KEY_EQUALS
import org.lwjgl.input.Keyboard.KEY_LEFT
import org.lwjgl.input.Keyboard.KEY_MINUS
import org.lwjgl.input.Keyboard.KEY_NUMPAD0
import org.lwjgl.input.Keyboard.KEY_RIGHT
import org.lwjgl.input.Keyboard.KEY_SPACE
import org.lwjgl.input.Keyboard.KEY_SUBTRACT
import org.lwjgl.input.Keyboard.KEY_UP
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.SlickException
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.StateBasedGame
import java.awt.Font
import java.lang.Math.abs
import java.lang.StrictMath.PI
import java.lang.StrictMath.log10
import java.lang.StrictMath.max
import java.lang.StrictMath.min

class CarPlotsState : GraphicsGameState() {
    private val font = TrueTypeFont(Font(Font.SANS_SERIF, Font.PLAIN, textSize), true)
    private val headerFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, headerTextSize), true)
    private val gray = Color(150, 150, 150)
    private val darkGray = Color(50, 50, 50)
    private val white = Color.white

    private val listeners = mutableListOf<KeyListener>()
    private val plotWidthPx = screenWidth - 2 * marginPx
    private val plots: List<Plot> by lazy {
        listOf(getFrontSteeringForcePlotForSpeed(50, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(100, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(150, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(200, plotWidthPx),
                getRearSteeringForcePlotForSpeed(50, plotWidthPx),
                getRearSteeringForcePlotForSpeed(100, plotWidthPx),
                getRearSteeringForcePlotForSpeed(150, plotWidthPx),
                getRearSteeringForcePlotForSpeed(200, plotWidthPx),
                Plot("Torque vs RPM", "RPM", "Torque, N*m", CarModel.min_rpm - 100, CarModel.max_rpm + 1000,
                        { rpm -> car.getTorque(rpm / SECONDS_PER_MINUTE) }, plotWidthPx, 0, 0),
                Plot("RPM for speed", "Speed, kmh", "RPM", 0.0, 340.0,
                        { kmh -> car.setVelocity(Polar(kmh / MPS_TO_KMPH, 0.0)).rps * SECONDS_PER_MINUTE }, plotWidthPx, 0, 1),
                Plot("Downforce for speed", "Speed, kmh", "Downforce, g", 0.0, 340.0,
                        { kmh -> car.setVelocity(Polar(kmh / MPS_TO_KMPH, 0.0)).downforceA / g }, plotWidthPx, 0, 1)
        )}
    private var currentPlot = 0
    private var showZeroY = false
    private lateinit var car: SettableCar<*>

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
        currentPlot = 0
        car = SettableCar(EmptyDriver(), GameSettings.instance.track)
        resetPlot()
    }

    private fun getFrontSteeringForcePlotForSpeed(speed: Int, plotWidthPx: Float): Plot {
        return Plot("Front turning forces @ $speed km/h", "Steering, rad", "Force, g", -maxSteering, maxSteering,
                { steering ->
                    car.setVelocity(Polar(speed / MPS_TO_KMPH, 0.0))
                            .setSteering(steering)
                            .frontTurningForceA / g
                }, plotWidthPx, 2, 2)
    }

    private fun getRearSteeringForcePlotForSpeed(speed: Int, plotWidthPx: Float): Plot {
        return Plot("Rear turning forces @ $speed km/h", "Steering, rad", "Force, g", -maxSteering, maxSteering,
                { steering ->
                    car.setVelocity(Polar(speed / MPS_TO_KMPH, steering))
                            .rearTurningForceA / g
                }, plotWidthPx, 2, 2)
    }

    @Throws(SlickException::class)
    override fun leave(container: GameContainer?, game: StateBasedGame?) {
        super.leave(container, game)
        listeners.forEach { listener -> container!!.input.removeKeyListener(listener) }
    }

    @Throws(SlickException::class)
    override fun init(container: GameContainer, game: StateBasedGame) {
        container.setTargetFrameRate(30)
        listeners.add(SingleKeyAction({ -> changePlot(-1) }, KEY_UP))
        listeners.add(SingleKeyAction({ -> changePlot(+1) }, KEY_DOWN))
        listeners.add(SingleKeyAction({ -> moveInterval(-1) }, KEY_LEFT))
        listeners.add(SingleKeyAction({ -> moveInterval(+1) }, KEY_RIGHT))
        listeners.add(SingleKeyAction({ -> zoom(+1) }, KEY_EQUALS))
        listeners.add(SingleKeyAction({ -> zoom(+1) }, KEY_ADD))
        listeners.add(SingleKeyAction({ -> zoom(-1) }, KEY_MINUS))
        listeners.add(SingleKeyAction({ -> zoom(-1) }, KEY_SUBTRACT))
        listeners.add(SingleKeyAction({ -> resetPlot() }, KEY_SPACE))
        listeners.add(SingleKeyAction({ -> toggleShowZeroY() }, KEY_0))
        listeners.add(SingleKeyAction({ -> toggleShowZeroY() }, KEY_NUMPAD0))
        cameraOffset = Decart(0.0, 0.0)
    }

    private fun changePlot(change: Int) {
        currentPlot = (currentPlot + change + plots.size) % plots.size
        plots[currentPlot].reset()
    }

    private fun moveInterval(change: Int) {
        plots[currentPlot].moveInterval(change)
    }

    private fun zoom(change: Int) {
        plots[currentPlot].zoom(change)
    }

    private fun toggleShowZeroY() {
        showZeroY = !showZeroY
    }

    private fun resetPlot() {
        plots[currentPlot].reset()
    }

    @Throws(SlickException::class)
    override fun render(container: GameContainer, game: StateBasedGame, g: Graphics) {
        g.clear()
        val currentPlot = plots[this.currentPlot]
        drawPlot(g, currentPlot)
    }

    private fun drawPlot(g: Graphics, plot: Plot) {
        drawGrid(g, plot.name,
                plot.xAxis, plot.from, plot.to,
                plot.yAxis, if (showZeroY) min(0.0, plot.minY) else plot.minY, if (showZeroY) max(0.0, plot.maxY) else plot.maxY,
                plot.xPrecision, plot.yPrecision)
        drawPlotData(g, plot.plotData, plot.from, plot.to,
                if (showZeroY) min(0.0, plot.minY) else plot.minY, if (showZeroY) max(0.0, plot.maxY) else plot.maxY)
    }

    private fun drawGrid(g: Graphics, name: String, xName: String, from: Double, to: Double,
                         yName: String, minY: Double, maxY: Double, xPrecision: Int, yPrecision: Int) {
        drawXAxisAndGrid(g, xName, from, to, minY, maxY, xPrecision)
        drawYAxisAndGrid(g, name, from, to, yName, minY, maxY, yPrecision)
    }

    private fun drawXAxisAndGrid(g: Graphics, xName: String, from: Double, to: Double, minY: Double, maxY: Double, xPrecision: Int) {
        g.font = font
        val xAxisLength = screenWidth - 2 * marginPx

        val xPxToXValue = MathUtils.linear(marginPx.toDouble(), from, (screenWidth - marginPx).toDouble(), to)
        val yValueToYPx = MathUtils.linear(minY, (screenHeight - marginPx).toDouble(), maxY, marginPx.toDouble())

        // x grid
        val xValueFormat = "%." + xPrecision + 'f'
        val xStep = xPxToXValue(gridStepPx.toDouble() + marginPx) - from
        val dataIncludesZeroY = minY <= 0 && maxY >= 0
        val xAxisYCoord = if (dataIncludesZeroY) yValueToYPx(0.0).toFloat() else screenHeight.toFloat() / 2
        var i = 0
        while (i <= (xAxisLength - marginPx) / gridStepPx) {
            val xPx = marginPx + i * gridStepPx
            drawLine(g, StraightLine(
                    Decart(xPx.toDouble(), marginPx.toDouble()),
                    Decart(xPx.toDouble(), (screenHeight - marginPx).toDouble()),
                    darkGray, 1f))
            g.color = white
            g.drawString(String.format(xValueFormat, from + i * xStep), xPx, xAxisYCoord + textSize / 2)
            i++
        }
        // x axis
        g.lineWidth = 2f
        Arrow.build(Decart((screenWidth / 2).toDouble(), xAxisYCoord.toDouble()), xAxisLength, 0.0, gray, 2f)
                .forEach { line -> drawLine(g, line) }
        g.color = white
        g.drawString(xName, screenWidth.toFloat() - marginPx - (xName.length * textSize / 2).toFloat(), xAxisYCoord - textSize * 3 / 2)
    }

    private fun drawYAxisAndGrid(g: Graphics, name: String, from: Double, to: Double, yName: String, minY: Double, maxY: Double, yPrecision: Int) {
        g.font = font
        val yAxisLength = screenHeight - 2 * marginPx

        val xValueToXPx = MathUtils.linear(from, marginPx.toDouble(), to, (screenWidth - marginPx).toDouble())
        val yPxToYValue = MathUtils.linear((screenHeight - marginPx).toDouble(), minY, marginPx.toDouble(), maxY)

        // y grid
        val yValueFormat = "%." + yPrecision + 'f'
        val yStep = yPxToYValue(screenHeight.toDouble() - gridStepPx.toDouble() - marginPx.toDouble()) - minY
        val dataIncludesZeroX = from <= 0 && to >= 0
        val yAxisXCoord = if (dataIncludesZeroX) xValueToXPx(0.0).toFloat() else marginPx
        var i = 0
        while (i <= (yAxisLength - marginPx) / gridStepPx) {
            val yPx = marginPx + i * gridStepPx
            drawLine(g, StraightLine(
                    Decart(marginPx.toDouble(), yPx.toDouble()),
                    Decart((screenWidth - marginPx).toDouble(), yPx.toDouble()),
                    darkGray, 1f))
            g.color = white
            g.drawString(String.format(yValueFormat, maxY - i * yStep), yAxisXCoord + textSize, yPx - textSize / 2)
            i++
        }
        // y axis
        Arrow.build(Decart(yAxisXCoord.toDouble(), (screenHeight / 2).toDouble()), screenHeight - 2 * marginPx, -PI / 2, gray, 2f)
                .forEach { line -> drawLine(g, line) }
        g.color = white
        g.drawString(yName, yAxisXCoord + (abs(log10(maxY)) + 1.0 + yPrecision.toDouble()).toFloat() * textSize, marginPx - textSize / 2)

        g.font = headerFont
        g.drawString(name, (screenWidth / 2 - name.length / 2 * headerTextSize / 3 * 2).toFloat(), marginPx / 2)
    }

    private fun drawPlotData(g: Graphics, plotData: Iterable<Decart>, from: Double, to: Double, minY: Double, maxY: Double) {
        val xAxisLength = screenWidth - 2 * marginPx
        val yAxisLength = screenHeight - 2 * marginPx
        val xToScreenX = { x: Double -> (marginPx + (x - from) * (xAxisLength / (to - from))) }
        val yToScreenY = { y: Double -> (screenHeight.toDouble() - marginPx.toDouble() - (y - minY) * (yAxisLength / (maxY - minY))) }

        plotData.forEach { point ->
            val screenCoordinates = Decart(xToScreenX(point.x), yToScreenY(point.y))
            drawLine(g, StraightLine(screenCoordinates, screenCoordinates, white, 1f))
        }
    }

    @Throws(SlickException::class)
    override fun update(container: GameContainer, game: StateBasedGame, delta: Int) {

    }

    companion object {

        private val MPS_TO_KMPH = 3.6
        private val marginPx = screenWidth * 0.02f
        private val textSize = 14
        private val headerTextSize = 18
        private val gridStepPx = 100
    }
}
