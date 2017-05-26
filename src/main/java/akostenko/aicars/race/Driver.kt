package akostenko.aicars.race

import akostenko.aicars.race.car.Car

abstract class Driver {
    var car: Car<*>? = null


    abstract fun accelerating(): Double
    abstract fun breaking(): Double

    /**
     * @return driver's desire to turn. Values are [-1,1] where -1 is full lock to left, +1 is full lock to right
     */
    abstract fun steering(): Double

    abstract val name: String

    abstract fun update(dTime: Double)
}
