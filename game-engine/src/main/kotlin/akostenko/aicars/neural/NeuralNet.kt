package akostenko.aicars.neural

import akostenko.aicars.race.car.Car
import java.util.*

abstract class NeuralNet {

    companion object {
        private val typeDelimiter = "#"
        private val typeDelimiterRegex = typeDelimiter.toRegex()

        fun serializePopulation(population: List<NNDriver>): List<String> {
            // each line contains a single net
            return population.map { "${typeOf(it)}$typeDelimiter${it.neural.serialize()}" }

        }

        private fun typeOf(driver: NNDriver): String {
            return when (driver.neural) {
                is LinearNN -> LinearNN.type
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
                else -> {
                    throw IllegalArgumentException("Unknown neural type $type")
                }
            }
        }

        fun generatePopulation(): List<NNDriver> {
            return IntRange(1, 100)
                    .map { LinearNN(0, 0, 0)}
                    .map { setRandomConnections(it) }
                    .map { NNDriver(it) }
        }

        private fun setRandomConnections(net: LinearNN): LinearNN {
            val random = Random()
            for (i in 0..net.inputCount-1) {
                for (o in 0..net.outputCount-1) {
                    net.nodeConnections[i][o] = random.nextDouble() * 2 - 1
                }
            }
            return net
        }
    }

    private val maxOutput = 1.0
    private val maxAcceleration = 1.0
    private val maxBreaking = 1.0

    private val maxSteering = 1.0
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
    abstract val outputCount: Int

    abstract val inputCount: Int

    abstract internal fun readCarParametersToInput(car: Car<*>)

    abstract internal fun calculateOutput()

    /** output value must be within [0, 1] */
    abstract internal fun output(index: Int): Double

    abstract fun serialize(): String

    abstract fun copy(isMutant: Boolean, isCrossingover: Boolean): NeuralNet

    fun accelerating(): Double {
        return output(accelerationOutput) / maxOutput * maxAcceleration
    }

    fun breaking(): Double {
        return output(brakingOutput) / maxOutput * maxBreaking
    }

    fun steering(): Double {
        return (output(steeringOutput) / maxOutput - 0.5) * 2 * maxSteering
    }

    abstract fun copyAndMutate(mutationsAmount: Double): NeuralNet
    abstract fun <N : NeuralNet> breed(second: N): N

}


