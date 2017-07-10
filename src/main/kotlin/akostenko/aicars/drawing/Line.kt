package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.MathUtils.linear
import akostenko.aicars.math.Vector
import java.lang.StrictMath.PI
import java.lang.StrictMath.abs
import java.lang.StrictMath.sqrt

sealed class Line(open val collidable: Boolean) {
    abstract fun rotate(radians: Double, center: Vector): Line
}

data class StraightLine(val from: Decart, val to: Decart, override val collidable: Boolean = true) : Line(collidable) {
    override fun rotate(radians: Double, center: Vector) = StraightLine(
            (from-center).rotate(radians) + center,
            (to-center).rotate(radians) + center,
            collidable)

    val direction by lazy { (to-from).toPolar().d }
    val isVertical by lazy {
        abs(this.direction % Math.PI - Math.PI / 2) < Vector.PRECISION
                || abs(this.direction % Math.PI + Math.PI / 2) < Vector.PRECISION }
    val yFunction by lazy {
        if (!isVertical) linear(this)
        else throw IllegalArgumentException("Not applicable") }
    val left = if (from.x < to.x) from else if (to.x < from.x) to else if (from.y < to.y) from else to
    val right = if (from == left) to else from
    fun contains(point: Vector): Boolean {
        val decart = point.toDecart()
        if (isVertical) {
            return decart.x == from.x && left.y <= decart.y && decart.y <= right.y
        } else {
            return left.x <= decart.x && decart.x <= right.x && abs(yFunction(decart.x) - decart.y) <= Vector.PRECISION
        }
    }
}

data class Circle(val center: Decart, val radius: Double) : Line(true) {
    override fun rotate(radians: Double, center: Vector) = Circle(
            (this.center - center).rotate(radians) + center, radius)

    val innerY1Function : (Double) -> Double = { x ->   sqrt(radius*radius - x*x) }
    val innerY2Function : (Double) -> Double = { x -> - sqrt(radius*radius - x*x) }
    fun contains(point: Vector): Boolean {
        return point.toDecart().x in center.x-abs(radius) .. center.x+abs(radius)
                && (abs(innerY1Function(point.toDecart().x-center.x) - (point.toDecart().y - center.y)) <= Vector.PRECISION
                || abs(innerY2Function(point.toDecart().x-center.x) - (point.toDecart().y - center.y)) <= Vector.PRECISION)
    }
}

data class ArcLine(val center: Decart, val radius: Double, val from: Double, val to: Double) : Line(true) {
    val check = {if (from > to) throw IllegalArgumentException("'from' angle must be less then 'to' angle") }

    override fun rotate(radians: Double, center: Vector) = ArcLine(
            (this.center - center).rotate(radians) + center, radius,
            from + radians,
            to + radians)
    val circle by lazy { Circle(center, radius) }
    fun contains(point: Vector): Boolean {
        var pointDirection = (point - center).toPolar().d
        if (radius < 0) pointDirection -= PI
        while (pointDirection < from) pointDirection += 2*PI
        while (pointDirection > from && pointDirection > to) pointDirection -= 2*PI
        return circle.contains(point)
                && from <= pointDirection
                && pointDirection <= to
    }
}



