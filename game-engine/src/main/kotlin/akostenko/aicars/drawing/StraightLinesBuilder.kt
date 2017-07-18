package akostenko.aicars.drawing

import akostenko.math.StraightLine
import akostenko.math.vector.Polar
import akostenko.math.vector.Vector

internal class StraightLinesBuilder(val collidable: Boolean = true) {
    private val lines = mutableListOf<StraightLine>()

    fun between(from: Vector, to: Vector): StraightLinesBuilder {
        lines.add(StraightLine(from.asCartesian(), to.asCartesian(), collidable))
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
            builder.lines.add(StraightLine(from.asCartesian(), from.asCartesian() + Polar(size, direction), collidable))
            return builder
        }
    }
}
