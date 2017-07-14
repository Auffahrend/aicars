package main.kotlin.akostenko.aicars.neural

import main.kotlin.akostenko.aicars.race.Driver


class NNDriver(val neural : NeuralNet)
    : Driver() {

    override fun accelerating(): Double {
        return neural.accelerating()
    }

    override fun breaking(): Double {
        return neural.breaking()
    }

    override fun steering(): Double {
        return neural.steering()
    }

    override val name: String
        get() = neural.name

    override fun update(dTime: Double) {
        neural.updateFromCar(car)
    }
}

