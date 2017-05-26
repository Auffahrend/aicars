package akostenko.aicars.race.car

import akostenko.aicars.math.MathUtils.linear
import java.util.Comparator.comparing

import akostenko.aicars.math.Decart

import java.util.ArrayList
import java.util.Arrays
import java.util.function.Function

class TorqueMap
/** points' Xs are RPS and Ys are torque.  */
(vararg points: Decart) {
    internal val rpsPoints: MutableList<Double> = ArrayList()
    internal val torqueApproximations: MutableList<Function<Double, Double>> = ArrayList()

    init {
        Arrays.sort(points, comparing(Function<Decart, Double> { it.getX() }))

        if (points.size < 1) {
            throw IllegalArgumentException("Torque map must contain at least 1 point!")
        }

        if (points.size == 1) {
            // constant torque
            addApproximation(0.0, { rps -> points[0].y })
        }

        for (i in 0..points.size - 1 - 1) {
            val _1 = points[i]
            val _2 = points[i + 1]
            addApproximation(_1.x, linear(_1.x, _1.y, _2.x, _2.y))
        }
    }

    private fun addApproximation(rps: Double, torqueApproximation: Function<Double, Double>) {
        rpsPoints.add(rps)
        torqueApproximations.add(torqueApproximation)
    }

    operator fun get(rps: Double): Double {
        var approximation = torqueApproximations[0]
        if (rps < rpsPoints[0]) {
            approximation = torqueApproximations[0]
        } else if (rps > rpsPoints[rpsPoints.size - 1]) {
            approximation = torqueApproximations[torqueApproximations.size - 1]
        } else {
            for (i in 0..rpsPoints.size - 1 - 1) {
                if (rps >= rpsPoints[i] && rps < rpsPoints[i + 1]) {
                    approximation = torqueApproximations[i]
                }
            }
        }

        return approximation.apply(rps)
    }
}
