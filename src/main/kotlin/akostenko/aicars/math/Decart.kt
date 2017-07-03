package akostenko.aicars.math

import java.lang.StrictMath.PI
import java.lang.StrictMath.abs
import java.lang.StrictMath.atan
import java.lang.StrictMath.hypot
import java.lang.StrictMath.pow

class Decart(val x: Double, val y: Double) : Vector {

    private val polar : Polar by lazy {
        var d: Double
        if (x != 0.0) {
            d = atan(y / x)
            if (d < 0) {
                d += PI
            } else if (y == 0.0 && x < 0) {
                d += PI
            }
            if (y < 0) {
                d += PI
            }
        } else if (y != 0.0) {
            d = if (y > 0) PI / 2 else 3 * PI / 2
        } else {
            d = 0.0
        }
        Polar(hypot(x, y), d)
    }

    override fun toPolar(): Polar = polar

    override fun toDecart() : Decart = this

    override fun plus(v: Vector) : Decart = addDecart(v.toDecart())

    internal fun addDecart(v: Decart) : Decart = Decart(x + v.x, y + v.y)

    override fun minus(v: Vector) : Decart = plus(-v)

    override fun unaryMinus(): Decart = times(-1.0)

    override fun times(k: Double): Decart = Decart(x * k, y * k)

    override fun div(k: Double): Decart = times(1.0 / k)

    override fun rotate(radians: Double): Decart = toPolar().rotate(radians).toDecart()

    override fun module(): Double = hypot(x, y)

    override fun moduleSqr(): Double = pow(module(), 2.0)

    override fun dot(v: Vector): Double = toPolar().dot(v.toPolar())

    override fun cross(v: Vector): Double = toPolar().cross(v.toPolar())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is Vector) {
            val otherDecart = other.toDecart()
            return abs(otherDecart.x - x) < Vector.PRECISION && abs(otherDecart.y - y) < Vector.PRECISION
        } else return false
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString() : String = "Decart(x=$x, y=$y)"

    companion object {
        val ZERO = Decart(0.0, 0.0)
    }
}
