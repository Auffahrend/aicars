package akostenko.aicars.drawing

import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector

internal class StraightLinesBuilder(val collidable: Boolean = true) {
    private val lines = mutableListOf<StraightLine>()

    fun between(from: Vector, to: Vector): StraightLinesBuilder {
        lines.add(StraightLine(from.toDecart(), to.toDecart(), collidable))
        return this
    }

    fun from(from: Vector): LineFromTo {
        return LineFromTo(this, from, collidable)
    }

    fun build(): Collection<StraightLine> {
        return lines
    }

    internal class LineFromTo(private val builder: StraightLinesBuilder, val from: Vector, val collidable: Boolean) {

        fun towards(direction: Double, size: Double): StraightLinesBuilder {
            builder.lines.add(StraightLine(from.toDecart(), from.toDecart() + Polar(size, direction), collidable))
            return builder
        }
    }
}
