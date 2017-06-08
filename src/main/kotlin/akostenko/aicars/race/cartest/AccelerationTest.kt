package akostenko.aicars.race.cartest

import akostenko.aicars.race.Driver

class AccelerationTest(private val targetSpeed: Double // km/h
) : Driver() {
    private var time = 0.0
    private var targetReached: Boolean = false

    override val name: String
        get() = String.format("0-%d kph", targetSpeed.toInt()) + if (targetReached) String.format(" %.3fs", time) else ""

    override fun accelerating(): Double {
        return (if (!targetReached) 1 else 0).toDouble()
    }

    override fun breaking(): Double {
        return (if (targetReached) 1 else 0).toDouble()
    }

    override fun steering(): Double {
        return 0.0
    }

    override fun update(dTime: Double) {
        if (!targetReached) {
            targetReached = car.speed.module() * 3.6 >= targetSpeed
            time += dTime
        }
    }
}
