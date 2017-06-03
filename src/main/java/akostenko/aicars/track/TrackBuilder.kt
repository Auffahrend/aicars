package akostenko.aicars.track

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import org.slf4j.LoggerFactory
import java.lang.Math.toRadians
import java.lang.StrictMath.PI
import java.util.*

internal class TrackBuilder private constructor(private var heading: Double,
                            private var currentPosition: Vector,
                            private val width: Double,
                            private var totalLength: Int) {

    private val sections = mutableListOf<TrackSection>()

    fun straight(length: Double): TrackBuilder {
        sections.add(TrackSection(totalLength, sections.size, currentPosition, length, 0.0, heading, width))
        totalLength += length.toInt()
        currentPosition = currentPosition.plus(Polar(length, heading))
        return this
    }

    fun right(radius: Double, degrees: Double): TrackBuilder {
        val angle = toRadians(degrees)
        sections.add(TrackSection(totalLength, sections.size, currentPosition, angle * radius, radius, heading, width))
        val turnCenter = currentPosition.plus(Polar(radius, heading + PI / 2))
        totalLength += (radius * toRadians(degrees)).toInt()
        currentPosition = turnCenter.plus(Polar(radius, PI + PI / 2 + heading + angle))
        heading += angle
        return this
    }

    fun left(radius: Double, degrees: Double): TrackBuilder {
        val angle = toRadians(degrees)
        sections.add(TrackSection(totalLength, sections.size, currentPosition, angle * radius, -radius, heading, width))
        val turnCenter = currentPosition.plus(Polar(radius, heading - PI / 2))
        totalLength += (radius * toRadians(degrees)).toInt()
        currentPosition = turnCenter.plus(Polar(radius, PI + heading - PI / 2 - angle))
        heading -= angle
        return this
    }

    fun done(): List<TrackSection> {
        return sections.toList()
    }

    companion object {
        fun start(x: Double, y: Double, heading: Double, width: Double): TrackBuilder = TrackBuilder(heading, Decart(x, y), width, 0)
    }

}
