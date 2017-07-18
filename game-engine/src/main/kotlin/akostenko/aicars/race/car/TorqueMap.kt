package akostenko.aicars.race.car

import akostenko.math.vector.Cartesian
import akostenko.math.MathUtils.linear

/** points' Xs are RPS and Ys are torque.  */
class TorqueMap(vararg points: Cartesian) {
    internal val rpsPoints = mutableListOf<Double>()
    internal val torqueApproximations = mutableListOf<(Double) -> Double>()

    init {
        val sortedPoints = points.toList().sortedBy { it.x }

        if (sortedPoints.isEmpty()) {
            throw IllegalArgumentException("Torque map must contain at least 1 point!")
        }

        if (sortedPoints.size == 1) {
            // constant torque
            addApproximation(0.0, { _ -> sortedPoints.single().y })
        }

        for (i in 0..sortedPoints.size - 1 - 1) {
            val from = sortedPoints[i]
            val to = sortedPoints[i + 1]
            addApproximation(from.x, linear(from.x, from.y, to.x, to.y))
        }
    }

    private fun addApproximation(rps: Double, torqueApproximation: (Double) -> Double) {
        rpsPoints.add(rps)
        torqueApproximations.add(torqueApproximation)
    }

    fun get(rps: Double): Double {
        var approximation = torqueApproximations[0]
        if (rps < rpsPoints[0]) {
            approximation = torqueApproximations[0]
        } else if (rps > rpsPoints[rpsPoints.size - 1]) {
            approximation = torqueApproximations[torqueApproximations.size - 1]
        } else {
            (0..rpsPoints.size - 1 - 1)
                    .filter { rps >= rpsPoints[it] && rps < rpsPoints[it + 1] }
                    .forEach { approximation = torqueApproximations[it] }
        }

        return approximation(rps)
    }
}
