package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import org.newdawn.slick.Color
import java.util.*

internal class LinesBuilder {
    private val lines = ArrayList<RelativeLine>()

    fun between(from: Vector, to: Vector): LinesBuilder {
        lines.add(RelativeLine(from, to))
        return this
    }

    fun from(from: Vector): LineFromTo {
        return LineFromTo(from)
    }

    fun build(): Collection<RelativeLine> {
        return lines
    }

    internal inner class LineFromTo(private val from: Vector) {

        fun towards(direction: Double, size: Double): LinesBuilder {
            this@LinesBuilder.lines.add(RelativeLine(from, from.plus(Polar(size, direction))))
            return this@LinesBuilder
        }
    }

    /** All this lines are correct only while image is centered at (0, 0)
     * They are incompatible with [StraightLine] while are unscaled and have no color
     */
    internal class RelativeLine(val from:Vector, val to:Vector) {

        fun rotate(radians:Double): RelativeLine {
            return RelativeLine(from.rotate(radians), to.rotate(radians))
        }

        fun scale(scale:Scale): RelativeLine {
            val k = scale.pixels / scale.size
            return RelativeLine(from * k, to * k)
        }

        fun place(position:Decart, color:Color, width:Float): StraightLine {
            return StraightLine(from.toDecart().plus(position),
                    to.toDecart().plus(position),
                    color,
                    width)
        }
    }

    internal class RelativeArc(val center: Vector, val radius: Double, val from: Double, val to: Double) {

        fun rotate(radians: Double): RelativeArc {
            return RelativeArc(center, radius, from + radians, to + radians)
        }

        fun scale(scale: Scale): RelativeArc {
            val k = scale.pixels / scale.size
            return RelativeArc(center * k, radius * k, from, to)
        }

        fun place(position: Decart, color:Color, width:Float): ArcLine {
            return ArcLine(position + center, radius, from, to, color, width)
        }
    }
}
