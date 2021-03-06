package akostenko.aicars.race.car

import akostenko.aicars.model.CarModel
import akostenko.aicars.model.CarModel.tyreRadius
import org.apache.commons.math3.util.FastMath.PI

class Gearbox(private val car: Car<*>) {
    private val gears = mutableListOf<Gear>()
    internal var current = 0

    init {
        with(gears) {
            add(Gear(60.0 / 3.6, 10000.0, tyreRadius))
            add(Gear(125.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(155.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(190.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(225.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(260.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(295.0 / 3.6, 12000.0, tyreRadius))
            add(Gear(330.0 / 3.6, 12000.0, tyreRadius))
        }
    }

    fun maxSpeed(tyreRadius: Double): Double = CarModel.max_rpm/60 / gears.last().ratio * (2.0 * PI * tyreRadius)

    fun update() {
        current = chooseCurrentGear()
    }

    private fun chooseCurrentGear(): Int {
        val shaftRPS = car.speed.module() / (2.0 * PI * tyreRadius)
        for (i in gears.indices) {
            val gearRPS = gears[i].ratio * shaftRPS
            if (gearRPS < CarModel.max_rpm / 60) {
                return i
            }
        }

        return gears.size - 1 // over RPS on highest gear
    }

    internal val ratio: Double
        get() = gears[current].ratio
    private class Gear internal constructor(maxSpeed: Double, rpm: Double, tyreRadius: Double) {

        internal val ratio: Double
        init {
            if (rpm <= 0 || maxSpeed <= 0 || tyreRadius <= 0) {
                throw IllegalArgumentException("Must be > 0")
            }

            ratio = rpm / 60 / (maxSpeed / (2.0 * PI * tyreRadius))
        }

    }
}
