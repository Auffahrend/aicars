package akostenko.aicars.neural

import akostenko.aicars.race.car.Car

class NeuralNet {
    lateinit var name: String
    private val maxOutput = 1.0
    private val maxAcceleration = 1.0
    private val maxBreaking = 1.0
    private val maxSteering = 1.0

    private val accelerationOutput = 0
    private val brakingOutput = 1
    private val steeringOutput = 2

    fun updateFromCar(car: Car<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        readCarParametersToInput(car)
        calculateOutput()
    }

    private fun calculateOutput() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun readCarParametersToInput(car: Car<*>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun accelerating(): Double {
        return output(accelerationOutput) / maxOutput * maxAcceleration
    }

    fun breaking(): Double {
        return output(brakingOutput) / maxOutput * maxBreaking
    }

    fun steering(): Double {
        return output(steeringOutput) / maxOutput * maxSteering
    }

    private fun output(index: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
