package akostenko.aicars.neural

import akostenko.aicars.plots.EmptyDriver
import akostenko.aicars.race.car.Car
import akostenko.aicars.track.DebugTrack
import akostenko.math.vector.Polar
import org.apache.commons.math3.util.FastMath.max
import org.apache.commons.math3.util.FastMath.min
import org.slf4j.LoggerFactory


class LinearNN(override val generation: Int,
               override val mutations: Int,
               override val crosses: Int) : NeuralNet() {
    /** all connections are [-1, 1] */
    internal var nodeConnections : MutableList<MutableList<Double>> = mutableListOf()

    internal val inputNodes : MutableList<Double> = mutableListOf()
    internal val outputNodes : MutableList<Double> = mutableListOf()
    override val outputCount: Int = 3

    override var inputCount: Int = 0
    override val name = "LG${generation}C${crosses}M${mutations}"

    init {
        for (i in 1..outputCount) { outputNodes.add(0.0) }

        inputCount = normalizeCarParameters(Car(EmptyDriver(), DebugTrack())).size
        for (i in 0..inputCount-1) {
            inputNodes.add(0.0)
            nodeConnections.add(mutableListOf())
            for (o in 1..outputCount) {
                nodeConnections[i].add(0.0)
            }
        }
    }

    override fun output(index: Int): Double {
        return min(max(outputNodes[index], 0.0), 1.0)
    }

    override fun calculateOutput() {
        for (o in 0..outputCount-1) {
            outputNodes[o] = 0.0
            for (i in 0..inputCount-1) {
                outputNodes[o] += inputNodes[i] * nodeConnections[i][o]
            }
        }
    }

    override fun readCarParametersToInput(car: Car<*>) {

        val parameters = normalizeCarParameters(car)

        for (i in 0..inputCount-1) { inputNodes[i] = parameters[i] }
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
                carVectors.flatMap { v -> if (v is Polar) listOf(v.r, v.d) else listOf(v.asCartesian().x, v.asCartesian().y) }
    }

    override fun applyMutations(mutationsAmount: Double): LinearNN {
        for (i in 1..(mutationsAmount * inputCount * outputCount).toInt()) {
            
        }
        return this
    }

    override fun serialize(): String {
        return LinearNN.serialize(this)
    }

    override fun copy(isMutant: Boolean, isCrossingover: Boolean): LinearNN {
        val copy = LinearNN(generation+1, mutations + if (isMutant) 1 else 0, crosses + if (isCrossingover) 1 else 0)
        copy.nodeConnections = nodeConnections.map { row -> row.toMutableList() }.toMutableList()
        return copy
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(this.javaClass)

        val type = "Linear"

        private val nameDelimiter = ":"
        private val valueDelimiter = ","
        private val rowDelimiter = "~~~"

        fun serialize(net: LinearNN): String {
            return buildString {
                append(net.generation).append(nameDelimiter)
                append(net.mutations).append(nameDelimiter)
                append(net.crosses).append(nameDelimiter)

                net.nodeConnections.forEach { row ->
                    row.forEach { value -> append(value.toString()).append(valueDelimiter) }
                    append(rowDelimiter)
                }
            }
        }

        fun deserialize(line: String): LinearNN {
            val nameParts = line.split(nameDelimiter.toRegex())
            val net: LinearNN
            if (nameParts.size >= 3) {
                net = LinearNN(nameParts[0].toInt(), nameParts[1].toInt(), nameParts[2].toInt())
            } else {
                logger.warn("Invalid format, there is only ${nameParts.size} parts for name")
                net = LinearNN(0, 0, 0)
            }

            net.nodeConnections = with(nameParts.last()) {
                split(rowDelimiter.toRegex())
                        .takeWhile { it.isNotEmpty() }
                        .map {
                            it.split(valueDelimiter.toRegex())
                                    .takeWhile { it.isNotEmpty() }
                                    .map { it.toDouble() }
                                    .toMutableList()
                        }.toMutableList()
            }

            return net
        }
    }
}