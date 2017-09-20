package akostenko.aicars.neural

import akostenko.aicars.plots.EmptyDriver
import akostenko.aicars.race.car.Car
import akostenko.aicars.track.DebugTrack
import akostenko.math.vector.Polar
import org.apache.commons.math3.util.FastMath
import org.apache.commons.math3.util.FastMath.PI
import java.util.*

abstract class NeuralNet {

    companion object {
        val outputCount: Int = 3 // acceleration, braking, steering
        val inputCount: Int by lazy {
            LinearNN(0,0,0)
                    .normalizeCarParameters(Car(EmptyDriver(), DebugTrack())).size
        }

        private val populationSize = 500

        private val typeDelimiter = "#"
        private val typeDelimiterRegex = typeDelimiter.toRegex()

        fun serializePopulation(population: List<NNDriver>): List<String> {
            // each line contains a single net
            return population.map { "${typeOf(it)}$typeDelimiter${it.neural.serialize()}" }

        }

        private fun typeOf(driver: NNDriver): String {
            return when (driver.neural) {
                is LinearNN -> LinearNN.type
                is TanHyperWith2LayersNN -> TanHyperWith2LayersNN.type
                else -> {
                    throw IllegalArgumentException("Unknown neural type ${driver.neural}")
                }
            }
        }

        fun deserializePopulation(nets: List<String>): List<NNDriver> {
            return nets
                    .map { it.split(typeDelimiterRegex) }
                    .map { restoreNetOfType(it.first(), it[1]) }
        }

        private fun restoreNetOfType(type: String, content: String): NNDriver {
            return when (type) {
                LinearNN.type -> NNDriver(LinearNN.deserialize(content))
                TanHyperWith2LayersNN.type -> NNDriver(TanHyperWith2LayersNN.deserialize(content))
                else -> {
                    throw IllegalArgumentException("Unknown neural type $type")
                }
            }
        }

        fun generatePopulation(): List<NNDriver> {
            return IntRange(1, populationSize)
                    .map { LinearNN(0, 0, 0) }
                    .map { setRandomConnections(it) }
                    .map { NNDriver(it) }
        }

        private fun setRandomConnections(net: LinearNN): LinearNN {
            val random = Random()
            for (i in 0 until inputCount) {
                for (o in 0 until outputCount) {
                    net.nodeConnections[i][o] = random.nextDouble() * 2 - 1
                }
            }
            return net
        }
    }

    private val accelerationOutput = 0
    private val brakingOutput = 1
    private val steeringOutput = 2

    fun updateFromCar(car: Car<*>) {
        readCarParametersToInput(car)
        calculateOutput()
    }

    abstract val name: String
    abstract val generation: Int
    abstract val mutations: Int

    abstract val crosses: Int

    abstract var fitness: Double

    abstract internal fun readCarParametersToInput(car: Car<*>)

    abstract internal fun calculateOutput()

    /** output value must be within [0, 1] */
    abstract internal fun output(index: Int): Double

    abstract fun serialize(): String

    abstract fun copy(isMutant: Boolean, isCrossOver: Boolean): NeuralNet

    fun accelerating(): Double {
        return output(accelerationOutput)
    }

    fun breaking(): Double {
        return output(brakingOutput)
    }

    fun steering(): Double {
        return 2 * output(steeringOutput) - 1
    }

    abstract fun copyAndMutate(mutationsFactor: Double): NeuralNet
    abstract fun <N : NeuralNet> breed(second: N): N
    abstract val genomeSize: Int

    private val distanceToScan = 200
    private val intervalBetweenWP = 4

    protected fun normalizeCarParameters(car: Car<*>): List<Double> {
        val trackScalars = listOf(car.track.width)

        val carScalars = listOf(car.carRotationSpeed / PI)

        val carVectors = mutableListOf(
                car.heading,
                car.speed / 100.0,
                car.steering,
                car.turningA,
                car.accelerationA,
                car.breakingA)

        car.apply {
            var wp = closestWP
            for (i in 1..distanceToScan) {
                wp = track.getNextWayPoint(wp)
                if (i % intervalBetweenWP == 0) {
                    carVectors.add( (wp.position - position) / distanceToScan.toDouble() )
                }
            }
        }

        return trackScalars +
                carScalars +
                carVectors.flatMap { v -> if (v is Polar) listOf(v.r, v.d / PI) else listOf(v.asCartesian().x, v.asCartesian().y) }
    }

}


