package akostenko.math

import akostenko.math.vector.Cartesian
import org.junit.Assert.assertEquals
import org.junit.Test
import org.apache.commons.math3.util.FastMath.PI

class MathUtilsTest {

    @Test
    fun findIntersection_StraightToStraight1() {
        val line1 = StraightLine(Cartesian(0.0, 0.0), Cartesian(1.0, 1.0))
        val line2 = StraightLine(Cartesian(1.0, 0.0), Cartesian(0.0, 1.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(0.5, 0.5))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight2() {
        val line1 = StraightLine(Cartesian(0.0, 0.0), Cartesian(1.0, 2.0))
        val line2 = StraightLine(Cartesian(0.75, 0.0), Cartesian(0.75, 1000.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(0.75, 0.75 * 2))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight3() {
        val line1 = StraightLine(Cartesian(0.0, 0.0), Cartesian(1.0, 2.0))
        val line2 = StraightLine(Cartesian(0.0, 0.0), Cartesian(-0.75, 1.0))
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, StraightLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight4() {
        val line1 = StraightLine(Cartesian(5.0, 1.0), Cartesian(1.0, 5.0))
        val line2 = StraightLine(Cartesian(0.0, 3.0), Cartesian(100.0, 3.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(3.0, 3.0))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc1() {
        val line1 = StraightLine(Cartesian(0.0, 0.0), Cartesian(2.0, 0.0))
        val line2 = ArcLine(Cartesian(0.0, 0.0), 1.0, 3*PI/2+4*PI, 3*PI/2+5*PI)
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(1.0, 0.0))),
                MathUtils.findIntersection(line1 to line2))

        val line3 = StraightLine(Cartesian(0.0, 0.0), Cartesian(-2.0, 0.0))
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line3 to line2))
    }

    @Test
    fun findIntersection_StraightToArc2() {
        val line1 = StraightLine(Cartesian(2.0, 0.0), Cartesian(4.0, 0.0))
        val line2 = ArcLine(Cartesian(-2.0, 0.0), 3.0, -PI/2, PI/2)
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc3() {
        val line1 = StraightLine(Cartesian(0.0, 0.0), Cartesian(2.0, 0.0))
        val line2 = ArcLine(Cartesian(0.0, 0.0), 1.0, PI/2, 3*PI/2)
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc4() {
        val line1 = StraightLine(Cartesian(4.0, 0.0), Cartesian(4.0, 8.0))
        val line2 = ArcLine(Cartesian(4.0, 2.0), 1.0, 0.0, 2*PI)
        assertEquals(
                listOf(
                        MathUtils.Intersection(line1, line2, Cartesian(4.0, 1.0)),
                        MathUtils.Intersection(line1, line2, Cartesian(4.0, 3.0))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc5() {
        val line1 = StraightLine(Cartesian(4.0, 0.0), Cartesian(4.0, 4.0))
        val line2 = ArcLine(Cartesian(2.0, 2.0), 2.0, 0.0, 2*PI)
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(4.0, 2.0))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc6() {
        val line1 = StraightLine(Cartesian(-50.0, -50.0), Cartesian(-42.0, -42.0))
        val line2 = ArcLine(Cartesian(-40.0, -45.0), -5.0, -2*PI, 0.0)
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Cartesian(-45.0, -45.0))),
                MathUtils.findIntersection(line1 to line2))
    }
}