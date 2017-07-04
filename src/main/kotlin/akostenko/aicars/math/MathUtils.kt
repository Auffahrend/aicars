package akostenko.aicars.math

import akostenko.aicars.drawing.ArcLine
import akostenko.aicars.drawing.Line
import akostenko.aicars.drawing.StraightLine
import java.lang.Math.sqrt
import java.lang.Math.tan
import java.lang.StrictMath.abs

object MathUtils {

    /**
     * @return linear function by given points
     */
    fun linear(x1: Double, y1: Double, x2: Double, y2: Double): (Double) -> Double {
        if (y2 == y1) {
            return { y2 }
        }

        if (x2 == x1) {
            throw IllegalArgumentException("Can not interpolate by 1 point!")
        }

        // y = kx + y0
        val k = (y2 - y1) / (x2 - x1)
        val y0 = y1 - x1 * k
        return { x -> k * x + y0 }
    }

    fun linear(line: StraightLine) = linear(line.from.x, line.from.y, line.to.x, line.to.y)

    fun findIntersection(lines: Pair<Line, Line>): List<Intersection<Line, Line>> {
        if (lines.first is StraightLine) {
            if (lines.second is StraightLine) {
                return findIntersection(lines.first as StraightLine, lines.second as StraightLine)
            } else {
                return findIntersection(lines.first as StraightLine, lines.second as ArcLine)
            }
        } else if (lines.second is StraightLine) {
            return findIntersection(lines.second as StraightLine, lines.first as ArcLine)
        } else {
            return findIntersection(lines.first as ArcLine, lines.second as ArcLine)
        }
    }

    private fun findIntersection(first: StraightLine, second: StraightLine):
            List<Intersection<StraightLine, StraightLine>> {
        if (isSecondLineCrossesFirst(first, second) && isSecondLineCrossesFirst(second, first)) {
            return listOf(Intersection(first, second, getStraightsIntersectionPoint(first, second)))
        } else {
            return emptyList()
        }
    }

    private fun getStraightsIntersectionPoint(first: StraightLine, second: StraightLine) : Vector {
        if (first.isVertical) {
            return Decart(first.from.x, second.yFunction(first.from.x))
        } else if (second.isVertical) {
            return Decart(second.from.x, first.yFunction(second.from.x))
        } else {
            val x = (second.yFunction(0.0) - first.yFunction(0.0)) / (tan(first.direction) - tan(second.direction))
            return Decart(x, first.yFunction(x))
        }
    }

    private fun isSecondLineCrossesFirst(first: StraightLine, second: StraightLine) : Boolean {
        val product1 = (first.to - first.from).cross(second.from - first.from)
        val product2 = (first.to - first.from).cross(second.to - first.from)
        return product1 * product2 < 0
    }

    private fun findIntersection(first: StraightLine, second: ArcLine): List<Intersection<StraightLine, ArcLine>> {
        // moving coordinates center to circle center to simplify
        val movedLine = StraightLine(first.from - second.center, first.to - second.center)
        var points = emptyList<Vector>()
        if (first.isVertical) {
            if (movedLine.from.x in -second.radius..second.radius) {
                val y1 = second.circle.innerY1Function(movedLine.from.x)
                val y2 = second.circle.innerY2Function(movedLine.from.x)
                points = listOf(
                        Decart(movedLine.from.x, y1),
                        Decart(movedLine.from.x, y2))
                if (abs(y1-y2) < Vector.PRECISION) points = points.take(1)

            }
        } else {
            // (kx + b)**2 + x**2 = r**2
            // (k**2+1) x**2 + 2kb x + b**2 - r**2 = 0
            // D = (2kb)**2 - 4(k**2+1) * (b**2 - r**2)
            // x = (+-sqrt(D) - 2kb) / 2(k**2+1)
            val k = tan(movedLine.direction)
            val b = movedLine.yFunction(0.0)
            val D = (2*k*b).sqr() - 4*(k.sqr() + 1)*(b.sqr() - second.radius.sqr())
            if (D >= 0) {
                val x1 = (sqrt(D) - 2*k*b)/2/(k.sqr() + 1)
                val x2 = (-sqrt(D) - 2*k*b)/2/(k.sqr() + 1)
                points = listOf(
                        Decart(x1, movedLine.yFunction(x1)),
                        Decart(x2, movedLine.yFunction(x2)))

                if (D < Vector.PRECISION) {
                    points = points.take(1)
                }
            }
        }

        return points
                .map { it + second.center }
                .filter { first.contains(it) }
                .filter { second.contains(it) }
                .sortedWith ( compareBy({ it.toDecart().x }, { it.toDecart().y }) )
                .map { Intersection(first, second, it) }
    }

    private fun findIntersection(first: ArcLine, second: ArcLine): List<Intersection<ArcLine, ArcLine>> {
        TODO()
    }

    data class Intersection<out L1:Line, out L2:Line>(val line1: L1, val line2: L2, val point: Vector)
}

private fun Double.sqr(): Double = this * this
