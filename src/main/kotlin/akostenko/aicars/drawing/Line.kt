package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Vector

abstract class Line {
    abstract fun rotate(radians: Double, center: Vector): Line
}

data class StraightLine(val from: Decart, val to: Decart) : Line() {
    override fun rotate(radians: Double, center: Vector) = StraightLine(
            (from-center).rotate(radians) + center,
            (to-center).rotate(radians) + center)
}

data class ArcLine(val center: Decart, val radius: Double, val from: Double, val to: Double) : Line() {
    override fun rotate(radians: Double, center: Vector) = ArcLine(
            (this.center - center).rotate(radians) + center, radius,
            from + radians,
            to + radians)
}



