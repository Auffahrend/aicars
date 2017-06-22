package akostenko.aicars.math

import akostenko.aicars.drawing.ArcLine
import akostenko.aicars.drawing.Line
import akostenko.aicars.drawing.StraightLine

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

    fun findIntersection(lines: Pair<Line, Line>): Pair<Line, Line>? {
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

    private fun findIntersection(first: StraightLine, second: StraightLine): Pair<Line, Line>? {
        if (isSecondLineCrossesFirst(first, second) && isSecondLineCrossesFirst(second, first)) {
            return first to second
        } else {
            return null
        }
    }

    private fun isSecondLineCrossesFirst(first: StraightLine, second: StraightLine) : Boolean {
        val product1 = (first.to - first.from).cross(second.from - first.from)
        val product2 = (first.to - first.from).cross(second.to - first.from)
        return product1 * product2 < 0
    }

    private fun findIntersection(first: StraightLine, second: ArcLine): Pair<Line, Line>? {
        return null
    }

    private fun  findIntersection(first: ArcLine, second: ArcLine): Pair<Line, Line>? {
        return null
    }

}
