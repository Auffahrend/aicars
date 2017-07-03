package akostenko.aicars.math

import akostenko.aicars.drawing.ArcLine
import akostenko.aicars.drawing.StraightLine
import javafx.scene.shape.Arc
import org.junit.Assert.*

import org.junit.Test
import java.lang.StrictMath.PI

class MathUtilsTest {

    @Test
    fun findIntersection_StraightToStraight1() {
        val line1 = StraightLine(Decart(0.0, 0.0), Decart(1.0, 1.0))
        val line2 = StraightLine(Decart(1.0, 0.0), Decart(0.0, 1.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Decart(0.5, 0.5))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight2() {
        val line1 = StraightLine(Decart(0.0, 0.0), Decart(1.0, 2.0))
        val line2 = StraightLine(Decart(0.75, 0.0), Decart(0.75, 1000.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Decart(0.75, 0.75*2))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight3() {
        val line1 = StraightLine(Decart(0.0, 0.0), Decart(1.0, 2.0))
        val line2 = StraightLine(Decart(0.0, 0.0), Decart(-0.75, 1.0))
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, StraightLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToStraight4() {
        val line1 = StraightLine(Decart(5.0, 1.0), Decart(1.0, 5.0))
        val line2 = StraightLine(Decart(0.0, 3.0), Decart(100.0, 3.0))
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Decart(3.0, 3.0))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc1() {
        val line1 = StraightLine(Decart(0.0, 0.0), Decart(2.0, 0.0))
        val line2 = ArcLine(Decart(0.0, 0.0), 1.0, 3*PI/2+4*PI, 3*PI/2+5*PI)
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Decart(1.0, 0.0))),
                MathUtils.findIntersection(line1 to line2))

        val line3 = StraightLine(Decart(0.0, 0.0), Decart(-2.0, 0.0))
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line3 to line2))
    }

    @Test
    fun findIntersection_StraightToArc2() {
        val line1 = StraightLine(Decart(2.0, 0.0), Decart(4.0, 0.0))
        val line2 = ArcLine(Decart(-2.0, 0.0), 3.0, -PI/2, PI/2)
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc3() {
        val line1 = StraightLine(Decart(0.0, 0.0), Decart(2.0, 0.0))
        val line2 = ArcLine(Decart(0.0, 0.0), 1.0, PI/2, 3*PI/2)
        assertEquals(
                emptyList<MathUtils.Intersection<StraightLine, ArcLine>>(),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc4() {
        val line1 = StraightLine(Decart(4.0, 0.0), Decart(4.0, 8.0))
        val line2 = ArcLine(Decart(4.0, 2.0), 1.0, 0.0, 2*PI)
        assertEquals(
                listOf(
                        MathUtils.Intersection(line1, line2, Decart(4.0, 1.0)),
                        MathUtils.Intersection(line1, line2, Decart(4.0, 3.0))),
                MathUtils.findIntersection(line1 to line2))
    }

    @Test
    fun findIntersection_StraightToArc5() {
        val line1 = StraightLine(Decart(4.0, 0.0), Decart(4.0, 4.0))
        val line2 = ArcLine(Decart(2.0, 2.0), 2.0, 0.0, 2*PI)
        assertEquals(
                listOf(MathUtils.Intersection(line1, line2, Decart(4.0, 2.0))),
                MathUtils.findIntersection(line1 to line2))
    }
}