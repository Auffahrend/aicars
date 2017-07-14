package akostenko.math.vector

import akostenko.math.vector.Vector.Companion.PRECISION
import org.junit.Assert.assertEquals
import org.junit.Test
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.sqrt

class PolarTest {

    @Test
    @Throws(Exception::class)
    fun dot_orthogonal() {
        val a = Cartesian(1.0, 0.0)
        val b = Cartesian(0.0, 1.0)
        val result = a.dot(b)
        assertEquals(0.0, result, PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun dot_collinear() {
        val a = Cartesian(2.0, 0.0)
        val b = Cartesian(-3.0, 0.0)
        val result = a.dot(b)
        assertEquals(-6.0, result, PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun dot_commutative() {
        val a = Polar(2.0, PI + PI / 4)
        val b = Polar(3.0, PI + PI / 2)
        val result1 = a.dot(b)
        val result2 = b.dot(a)

        assertEquals(3 * sqrt(2.0), result1, PRECISION)
        assertEquals(3 * sqrt(2.0), result2, PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun cross_orthogonal() {
        val a = Cartesian(2.0, 0.0)
        val b = Cartesian(0.0, -3.0)
        val result = a.cross(b)
        assertEquals(-6.0, result, PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun cross_collinear() {
        val a = Cartesian(2.0, 0.0)
        val b = Cartesian(-3.0, 0.0)
        val result = a.cross(b)
        assertEquals(0.0, result, PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun cross_antiCommutative() {
        val a = Polar(2.0, PI + PI / 4)
        val b = Polar(3.0, PI + PI / 2)
        val result1 = a.cross(b)
        val result2 = b.cross(a)

        assertEquals(result1, -result2, PRECISION)
    }

}