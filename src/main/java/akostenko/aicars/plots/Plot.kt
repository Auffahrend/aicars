package akostenko.aicars.plots

import java.lang.Double.max
import java.lang.Double.min
import java.lang.StrictMath.pow

import akostenko.aicars.math.Decart

import java.util.ArrayList
import java.util.function.Function

internal class Plot(private val name: String, private val xAxis: String, private val yAxis: String, private val fromInit: Double, private val toInit: Double, private val plotFunction: Function<Double, Double>, private val xPixels: Float, private val xPrecision: Int, private val yPrecision: Int) {

    private var from: Double = 0.toDouble()
    private var to: Double = 0.toDouble()
    private val plotData = ArrayList<Decart>()
    private var minY: Double = 0.toDouble()
    private var maxY: Double = 0.toDouble()

    init {
        from = fromInit
        to = toInit
    }

    fun moveInterval(change: Int) {
        val offset = (to - from) / 2
        from += change * offset
        to += change * offset
        recalculate()
    }

    fun zoom(change: Int) {
        val diapason = (to - from) / pow(2.0, change.toDouble())
        val center = (from + to) / 2
        from = center - diapason / 2
        to = center + diapason / 2
        recalculate()
    }

    fun reset() {
        from = fromInit
        to = toInit
        recalculate()
    }

    private fun recalculate() {
        val dx = (to - from) / xPixels
        plotData.clear()
        var x = from
        while (x <= to) {
            plotData.add(Decart(x, plotFunction.apply(x)))
            x += dx
        }

        minY = java.lang.Double.MAX_VALUE
        maxY = java.lang.Double.MIN_VALUE
        plotData.forEach { point ->
            minY = min(minY, point.y)
            maxY = max(maxY, point.y)
        }
    }

    fun name(): String {
        return name
    }

    fun xAxis(): String {
        return xAxis
    }

    fun yAxis(): String {
        return yAxis
    }

    fun getPlotData(): List<Decart> {
        return plotData
    }

    fun from(): Double {
        return from
    }

    fun to(): Double {
        return to
    }

    fun minY(): Double {
        return minY
    }

    fun maxY(): Double {
        return maxY
    }

    fun xPrecision(): Int {
        return xPrecision
    }

    fun yPrecision(): Int {
        return yPrecision
    }
}
