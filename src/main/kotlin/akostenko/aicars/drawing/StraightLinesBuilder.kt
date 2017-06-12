package akostenko.aicars.drawing

import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import org.newdawn.slick.Color
import java.util.*

internal class StraightLinesBuilder(val color: Color, val lineWidth: Float) {
    private val lines = ArrayList<StraightLine>()

    fun between(from: Vector, to: Vector): StraightLinesBuilder {
        lines.add(StraightLine(from.toDecart(), to.toDecart(), color, lineWidth))
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
            builder.lines.add(StraightLine(from.toDecart(), from.toDecart() + Polar(size, direction),
                    builder.color, builder.lineWidth))
            return builder
        }
    }
}
