package akostenko.aicars.math

import java.lang.StrictMath.PI
import java.lang.StrictMath.abs
import java.lang.StrictMath.cos
import java.lang.StrictMath.sin

class Polar(
        /** length of vector, always >= 0  */
        r: Double, d: Double) : Vector {
    /** direction, *radians*, measured from X axis towards Y axis, always within [-PI, +PI]  */
    val r: Double
    val d: Double
    // TODO reuse this into Decart.polar and vise versa
    private val decart : Decart by lazy { Decart(r * cos(d), r * sin(d)) }

    init {
        var d = d
        if (r < 0) {
            d += PI
        }
        while (d < -PI) d += 2 * PI
        while (d > PI) d -= 2 * PI
        this.d = d
        this.r = abs(r)
    }

    override fun toPolar() = this

    override fun toDecart() = decart

    override fun plus(v: Vector) = toDecart().plus(v.toDecart()).toPolar()

    override fun minus(v: Vector) = plus(-v)

    override fun unaryMinus() = times(-1.0)

    override fun times(k: Double): Polar {
        if (k == 0.0) {
            return ZERO
        } else {
            return Polar(r * abs(k), d + if (k > 0) 0.0 else StrictMath.PI)
        }
    }

    override fun div(k: Double) = times(1.0 / k)

    override fun rotate(radians: Double) = Polar(r, d + radians)

    override fun module() = r

    override fun moduleSqr() = r * r

    override fun dot(v: Vector): Double {
        val other = v.toPolar()
        return r * other.r * cos(d - other.d)
    }

    override fun cross(v: Vector): Double {
        val other = v.toPolar()
        return r * other.r * sin(other.d - d)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        val polar: Polar
        if (other is Vector) {
            polar = other.toPolar()
            return abs(polar.r - r) < Vector.PRECISION && abs(polar.d - d) < Vector.PRECISION
        } else return false
    }

    override fun hashCode(): Int {
        var result = r.hashCode()
        result = 31 * result + d.hashCode()
        return result
    }

    override fun toString(): String = "Polar(r=$r, d=$d)"

    companion object {
        val ZERO = Polar(0.0, 0.0)
    }
}
