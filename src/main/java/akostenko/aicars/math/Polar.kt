package akostenko.aicars.math

import java.lang.StrictMath.PI
import java.lang.StrictMath.abs
import java.lang.StrictMath.cos
import java.lang.StrictMath.sin

class Polar(
        /** length of vector, always >= 0  */
        val r: Double, d: Double) : Vector {
    /** direction, *radians*, measured from X axis towards Y axis, always within [0, 2*PI)  */
    val d: Double

    init {
        var d = d
        if (r < 0) {
            d += PI
        }
        while (d < -PI) d += 2 * PI
        while (d > PI) d -= 2 * PI
        this.d = d
    }

    override fun toPolar(): Polar {
        return this
    }

    override fun toDecart(): Decart {
        return Decart(r * cos(d), r * sin(d))
    }

    override fun plus(v: Vector): Polar {
        return toDecart().plus(v.toDecart()).toPolar()
    }

    override fun minus(v: Vector): Polar {
        return plus(v.negative())
    }

    override fun negative(): Polar {
        return multi(-1.0)
    }

    override fun multi(k: Double): Polar {
        if (k == 0.0) {
            return ZERO
        } else {
            return Polar(r * abs(k), d + if (k > 0) 0 else PI)
        }
    }

    override fun div(k: Double): Polar {
        return multi(1.0 / k)
    }

    override fun rotate(radians: Double): Polar {
        return Polar(r, d + radians)
    }

    override fun module(): Double {
        return r
    }

    override fun moduleSqr(): Double {
        return r * r
    }

    override fun dot(v: Vector): Double {
        val b = v.toPolar()
        return r * b.r * cos(d - b.d)
    }

    override fun cross(v: Vector): Double {
        val b = v.toPolar()
        return r * b.r * sin(b.d - d)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        val polar: Polar
        if (o is Vector) {
            polar = o.toPolar()
        } else
            return false

        return polar.r - r < Vector.PRECISION && polar.d - d < Vector.PRECISION
    }

    override fun hashCode(): Int {
        var result: Int
        var temp: Long
        temp = java.lang.Double.doubleToLongBits(r)
        result = (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(d)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun toString(): String {
        return "Polar{" +
                "r=" + r +
                ", d=" + d +
                '}'
    }

    companion object {
        val ZERO = Polar(0.0, 0.0)
    }
}
