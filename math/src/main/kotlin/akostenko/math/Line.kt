package akostenko.math

import akostenko.math.vector.Cartesian
import akostenko.math.MathUtils.linear
import akostenko.math.vector.Vector
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.abs
import org.apache.commons.math3.util.FastMath.sqrt
import java.lang.IllegalArgumentException

sealed class Line(open val collidable: Boolean) {
    abstract fun rotate(radians: Double, center: Vector): Line
}

data class StraightLine(val from: Cartesian, val to: Cartesian, override val collidable: Boolean = true) : Line(collidable) {
    override fun rotate(radians: Double, center: Vector) = StraightLine(
            (from - center).rotate(radians) + center,
            (to - center).rotate(radians) + center,
            collidable)

    val direction by lazy { (to-from).asPolar().d }
    val isVertical by lazy {
        abs(this.direction % Math.PI - Math.PI / 2) < Vector.PRECISION
                || abs(this.direction % Math.PI + Math.PI / 2) < Vector.PRECISION }
    val yFunction by lazy {
        if (!isVertical) linear(this)
        else throw IllegalArgumentException("Not applicable") }
    val left = if (from.x < to.x) from else if (to.x < from.x) to else if (from.y < to.y) from else to
    val right = if (from == left) to else from
    fun contains(point: Vector): Boolean {
        val cartesian = point.asCartesian()
        if (isVertical) {
            return cartesian.x == from.x && left.y <= cartesian.y && cartesian.y <= right.y
        } else {
            return left.x <= cartesian.x && cartesian.x <= right.x && abs(yFunction(cartesian.x) - cartesian.y) <= Vector.PRECISION
        }
    }
}

data class Circle(val center: Cartesian, val radius: Double) : Line(true) {
    override fun rotate(radians: Double, center: Vector) = Circle(
            (this.center - center).rotate(radians) + center, radius)

    val innerY1Function : (Double) -> Double = { x ->   sqrt(radius*radius - x*x) }
    val innerY2Function : (Double) -> Double = { x -> - sqrt(radius*radius - x*x) }
    fun contains(point: Vector): Boolean {
        return point.asCartesian().x in center.x-abs(radius) .. center.x+abs(radius)
                && (abs(innerY1Function(point.asCartesian().x-center.x) - (point.asCartesian().y - center.y)) <= Vector.PRECISION
                || abs(innerY2Function(point.asCartesian().x-center.x) - (point.asCartesian().y - center.y)) <= Vector.PRECISION)
    }
}

data class ArcLine(val center: Cartesian, val radius: Double, val from: Double, val to: Double) : Line(true) {
    val check = {if (from > to) throw IllegalArgumentException("'from' angle must be less then 'to' angle") }

    override fun rotate(radians: Double, center: Vector) = ArcLine(
            (this.center - center).rotate(radians) + center, radius,
            from + radians,
            to + radians)
    val circle by lazy { Circle(center, radius) }
    fun contains(point: Vector): Boolean {
        var pointDirection = (point - center).asPolar().d
        if (radius < 0) pointDirection -= PI
        while (pointDirection < from) pointDirection += 2*PI
        while (pointDirection > from && pointDirection > to) pointDirection -= 2*PI
        return circle.contains(point)
                && from <= pointDirection
                && pointDirection <= to
    }
}



