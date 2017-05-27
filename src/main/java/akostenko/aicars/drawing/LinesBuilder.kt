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
     * They are incompatible with [Line] while are unscaled and have no color
     */
    internal class LocalLine(val from:Vector, val to:Vector) {

        fun rotate(radians:Double):LocalLine {
            return LocalLine(from.rotate(radians), to.rotate(radians))
        }

        fun scale(scale:Scale):LocalLine {
            val k = scale.pixels / scale.size
            return LocalLine(from.times(k.toDouble()), to.times(k.toDouble()))
        }

        fun position(position:Decart, color:Color, width:Float):Line {
            return Line(from.toDecart().plus(position),
                    to.toDecart().plus(position),
                    color,
                    width)
        }
    }
}
