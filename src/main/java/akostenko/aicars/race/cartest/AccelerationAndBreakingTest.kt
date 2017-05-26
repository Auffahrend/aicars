package akostenko.aicars.race.cartest

import akostenko.aicars.race.Driver

class AccelerationAndBreakingTest(private val targetSpeed: Double // m/s
) : Driver() {
    private var time = 0.0
    private var speedReached: Boolean = false
    private var targetReached: Boolean = false

    override val name: String
        get() = String.format("0-%d-0 kph", targetSpeed.toInt()) + if (targetReached) String.format(" %.3fs", time) else ""

    override fun accelerating(): Double {
        return (if (!speedReached) 1 else 0).toDouble()
    }

    override fun breaking(): Double {
        return (if (speedReached) 1 else 0).toDouble()
    }

    override fun steering(): Double {
        return 0.0
    }

    override fun update(dTime: Double) {
        if (!targetReached) {
            speedReached = speedReached || car.speed().module() * 3.6 >= targetSpeed
            targetReached = speedReached && car.speed().module() <= 0.1
            time += dTime
        }
    }
}
