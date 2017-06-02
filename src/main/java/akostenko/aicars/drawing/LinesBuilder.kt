package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import org.newdawn.slick.Color
import java.util.*

internal class LinesBuilder {
    private val lines = ArrayList<LocalLine>()

    fun between(from: Vector, to: Vector): LinesBuilder {
        lines.add(LocalLine(from, to))
        return this
    }

    fun from(from: Vector): LineFromTo {
        return LineFromTo(from)
    }

    fun build(): Collection<LocalLine> {
        return lines
    }

    internal inner class LineFromTo(private val from: Vector) {

        fun towards(direction: Double, size: Double): LinesBuilder {
            this@LinesBuilder.lines.add(LocalLine(from, from.plus(Polar(size, direction))))
            return this@LinesBuilder
        }
    }

    /** All this lines are correct only while image is centered at (0, 0)
     * They are incompatible with [StraightLine] while are unscaled and have no color
     */
    internal class LocalLine(val from:Vector, val to:Vector) {

        fun rotate(radians:Double):LocalLine {
            return LocalLine(from.rotate(radians), to.rotate(radians))
        }

        fun scale(scale:Scale):LocalLine {
            val k = scale.pixels / scale.size
            return LocalLine(from * k.toDouble(), to * k.toDouble())
        }

        fun place(position:Decart, color:Color, width:Float): StraightLine {
            return StraightLine(from.toDecart().plus(position),
                    to.toDecart().plus(position),
                    color,
                    width)
        }
    }

    internal class LocalArc(val center: Vector, val radius: Double, val from: Double, val to: Double) {

        fun rotate(radians: Double): LocalArc {
            return LocalArc(center, radius, from + radians, to + radians)
        }

        fun scale(scale: Scale): LocalArc {
            val k = scale.pixels / scale.size
            return LocalArc(center * k.toDouble(), radius * k, from, to)
        }

        fun place(position: Decart, color:Color, width:Float): ArcLine {
            return ArcLine(position + center, radius, from, to, color, width)
        }
    }
}
