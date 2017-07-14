package main.kotlin.akostenko.aicars.plots

import main.kotlin.akostenko.aicars.race.Driver

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
