package main.kotlin.akostenko.aicars.plots

import akostenko.math.vector.Decart
import java.lang.Double.max
import java.lang.Double.min
import java.lang.StrictMath.pow

internal class Plot(val name: String,
                    val xAxis: String,
                    val yAxis: String,
                    private val fromInit: Double,
                    private val toInit: Double,
                    private val plotFunction: (Double) -> Double,
                    private val xPixels: Float,
                    val xPrecision: Int,
                    val yPrecision: Int) {

    var from: Double = 0.0
        private set
    var to: Double = 0.0
        private set
    val plotData = mutableListOf<Decart>()
    var minY: Double = 0.0
        private set
    var maxY: Double = 0.0
        private set

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
            plotData.add(Decart(x, plotFunction(x)))
            x += dx
        }

        minY = java.lang.Double.MAX_VALUE
        maxY = java.lang.Double.MIN_VALUE
        plotData.forEach { point ->
            minY = min(minY, point.y)
            maxY = max(maxY, point.y)
        }
    }
}
