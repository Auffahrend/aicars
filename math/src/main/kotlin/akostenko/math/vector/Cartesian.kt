package akostenko.math.vector

import org.apache.commons.math3.util.FastMath.hypot
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.abs
import org.apache.commons.math3.util.FastMath.atan
import org.apache.commons.math3.util.FastMath.cos
import org.apache.commons.math3.util.FastMath.sin
import org.apache.commons.math3.util.FastMath.pow

class Cartesian(val x: Double, val y: Double) : Vector {

    internal var polar : Polar? = null

    override fun asPolar(): Polar {
        if (polar == null) {
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
            polar = Polar(hypot(x, y), d)
            polar!!.cartesian = this
        }
        return polar!!
    }

    override fun asCartesian() : Cartesian = this

    override fun plus(v: Vector) : Cartesian = addCartesian(v.asCartesian())

    internal fun addCartesian(v: Cartesian) : Cartesian = Cartesian(x + v.x, y + v.y)

    override fun minus(v: Vector) : Cartesian = plus(-v)

    override fun unaryMinus(): Cartesian = times(-1.0)

    override fun times(k: Double): Cartesian = Cartesian(x * k, y * k)

    override fun div(k: Double): Cartesian = times(1.0 / k)

    override fun rotate(radians: Double): Cartesian {
        val cos = cos(radians)
        val sin = sin(radians)
        return Cartesian(x * cos - y * sin, x * sin + y * cos)
    }

    override fun module(): Double = hypot(x, y)

    override fun moduleSqr(): Double = pow(module(), 2.0)

    override fun dot(v: Vector): Double {
        val other = v.asCartesian()
        return x * other.x + y * other.y
    }

    override fun cross(v: Vector): Double {
        val other = v.asCartesian()
        return x * other.y - y * other.x
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is Vector) {
            val otherCartesian = other.asCartesian()
            return abs(otherCartesian.x - x) < Vector.PRECISION && abs(otherCartesian.y - y) < Vector.PRECISION
        } else return false
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString() : String = "Cartesian(x=$x, y=$y)"

    companion object {
        val ZERO = Cartesian(0.0, 0.0)
    }
}

