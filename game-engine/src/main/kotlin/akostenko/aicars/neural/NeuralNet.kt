package akostenko.aicars.neural

import akostenko.aicars.race.Driver
import akostenko.aicars.race.car.Car
import java.util.*

abstract class NeuralNet(open val name : String) {

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

    abstract val outputCount : Int
    abstract val inputCount : Int

    abstract internal fun readCarParametersToInput(car: Car<*>)

    abstract internal fun calculateOutput()

    abstract internal fun output(index: Int): Double

    fun accelerating(): Double {
        return output(accelerationOutput) / maxOutput * maxAcceleration
    }

    fun breaking(): Double {
        return output(brakingOutput) / maxOutput * maxBreaking
    }

    fun steering(): Double {
        return output(steeringOutput) / maxOutput * maxSteering
    }

    companion object {
        fun bestDrivers() : List<Driver> {
            return IntRange(1, 100)
                    .map { LinearNN("Linear #$it") }
                    .map { setRandomConnections(it) }
                    .map { NNDriver(it) }
        }

        private fun setRandomConnections(net: LinearNN): LinearNN {
            val random = Random()
            for (i in 0..net.inputCount) {
                for (o in 0..net.outputCount) {
                    net.nodeConnections[i][o] = random.nextDouble()
                }
            }
            return net
        }
    }

}


