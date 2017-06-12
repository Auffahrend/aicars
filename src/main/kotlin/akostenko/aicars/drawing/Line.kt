package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Vector
import org.newdawn.slick.Color

abstract class Line {
    abstract fun rotate(radians: Double, center: Vector): Line
}

data class StraightLine(val from: Decart, val to: Decart, val color: Color, val width: Float) : Line() {
    override fun rotate(radians: Double, center: Vector) = StraightLine(
            (from-center).rotate(radians) + center,
            (to-center).rotate(radians) + center,
            color, width)
}

data class ArcLine(val center: Decart, val radius: Double, val from: Double, val to: Double, val color: Color, val width: Float) : Line() {
    override fun rotate(radians: Double, center: Vector) = ArcLine(
            (this.center - center).rotate(radians) + center, radius,
            from + radians,
            to + radians,
            color, width)
}

