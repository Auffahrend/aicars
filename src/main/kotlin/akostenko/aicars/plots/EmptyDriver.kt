package akostenko.aicars.plots

import akostenko.aicars.race.Driver

class EmptyDriver : Driver() {
    override fun accelerating(): Double {
        return 0.0
    }

    override fun breaking(): Double {
        return 0.0
    }

    override fun steering(): Double {
        return 0.0
    }

    override val name: String
        get() = "no name"

    override fun update(dTime: Double) {

    }
}
