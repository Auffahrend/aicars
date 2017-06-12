package akostenko.aicars.drawing

import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import java.util.*

internal class StraightLinesBuilder {
    private val lines = ArrayList<StraightLine>()

    fun between(from: Vector, to: Vector): StraightLinesBuilder {
        lines.add(StraightLine(from.toDecart(), to.toDecart()))
        return this
    }

    fun from(from: Vector): LineFromTo {
        return LineFromTo(this, from)
    }

    fun build(): Collection<StraightLine> {
        return lines
    }

    internal class LineFromTo(private val builder: StraightLinesBuilder, val from: Vector) {

        fun towards(direction: Double, size: Double): StraightLinesBuilder {
            builder.lines.add(StraightLine(from.toDecart(), from.toDecart() + Polar(size, direction)))
            return builder
        }
    }
}
