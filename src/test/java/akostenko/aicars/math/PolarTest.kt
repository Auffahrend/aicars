package akostenko.aicars.math

import akostenko.aicars.math.Vector.PRECISION
import java.lang.StrictMath.PI
import java.lang.StrictMath.sqrt
import org.junit.Assert.*

import org.junit.Test

class PolarTest {

    @Test
    @Throws(Exception::class)
    fun dot_orthogonal() {
        val a = Decart(1.0, 0.0)
        val b = Decart(0.0, 1.0)
        val result = a.dot(b)
        assertEquals(0, result, Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun dot_collinear() {
        val a = Decart(2.0, 0.0)
        val b = Decart(-3.0, 0.0)
        val result = a.dot(b)
        assertEquals(-6, result, Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun dot_commutative() {
        val a = Polar(2.0, PI + PI / 4)
        val b = Polar(3.0, PI + PI / 2)
        val result1 = a.dot(b)
        val result2 = b.dot(a)

        assertEquals(3 * sqrt(2.0), result1, Companion.getPRECISION())
        assertEquals(3 * sqrt(2.0), result2, Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun cross_orthogonal() {
        val a = Decart(2.0, 0.0)
        val b = Decart(0.0, -3.0)
        val result = a.cross(b)
        assertEquals(-6, result, Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun cross_collinear() {
        val a = Decart(2.0, 0.0)
        val b = Decart(-3.0, 0.0)
        val result = a.cross(b)
        assertEquals(0, result, Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun cross_antiCommutative() {
        val a = Polar(2.0, PI + PI / 4)
        val b = Polar(3.0, PI + PI / 2)
        val result1 = a.cross(b)
        val result2 = b.cross(a)

        assertEquals(result1, -result2, Companion.getPRECISION())
    }

}