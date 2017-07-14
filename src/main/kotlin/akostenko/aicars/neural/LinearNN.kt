package akostenko.aicars.neural

import akostenko.math.vector.Polar
import akostenko.aicars.plots.EmptyDriver
import akostenko.aicars.race.car.Car
import akostenko.aicars.track.DebugTrack


class LinearNN(override val name : String) : NeuralNet(name) {

    internal val nodeConnections : MutableList<MutableList<Double>> = mutableListOf()
    internal val inputNodes : MutableList<Double> = mutableListOf()
    internal val outputNodes : MutableList<Double> = mutableListOf()

    override val outputCount: Int = 2
    override var inputCount: Int = 0

    init {
        for (i in 0..outputCount) { outputNodes.add(0.0) }

        inputCount = normalizeCarParameters(Car(EmptyDriver(), DebugTrack())).size-1
        for (i in 0..inputCount) {
            inputNodes.add(0.0)
            nodeConnections.add(mutableListOf())
            for (o in 0..outputCount) {
                nodeConnections[i].add(0.0)
            }
        }


    }

    override fun output(index: Int): Double {
        return outputNodes[index]
    }

    override fun calculateOutput() {
        for (o in 0..outputCount) {
            outputNodes[o] = 0.0
            for (i in 0..inputCount) {
                outputNodes[o] += inputNodes[i] * nodeConnections[i][o]
            }
        }
    }

    override fun readCarParametersToInput(car: Car<*>) {

        val parameters = normalizeCarParameters(car)

        for (i in 0..inputCount) { inputNodes[i] = parameters[i] }
    }

    private val distanceToScan = 200

    private fun normalizeCarParameters(car: Car<*>): List<Double> {
        val trackScalars = listOf(car.track.width)

        val carScalars = listOf(car.carRotationSpeed)

        val carVectors = mutableListOf(car.heading,
                car.speed,
                car.breaking,
                car.steering,
                car.turningA,
                car.accelerationA,
                car.breakingA)

        car.apply {
            var wp = closestWP
            for (i in 1..distanceToScan) {
                wp = track.getNextWayPoint(wp)
                carVectors.add(wp.position - position)
            }
        }

        return trackScalars +
                carScalars +
                carVectors.flatMap { v -> if (v is Polar) listOf(v.r, v.d) else listOf(v.toDecart().x, v.toDecart().y) }
    }
}