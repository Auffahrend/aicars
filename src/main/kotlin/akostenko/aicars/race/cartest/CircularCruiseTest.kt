package akostenko.aicars.race.cartest

import akostenko.aicars.model.CarModel
import akostenko.aicars.race.Driver

class CircularCruiseTest : Driver() {
    override fun accelerating(): Double {
        return 1.0
    }

    override fun breaking(): Double {
        return 0.0
    }

    override fun steering(): Double {
        return CarModel.peakLateralForceAngle
    }

    override val name: String
        get() = "Max speed turning"

    override fun update(dTime: Double) {

    }
}
