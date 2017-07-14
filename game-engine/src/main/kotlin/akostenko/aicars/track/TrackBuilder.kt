package main.kotlin.akostenko.aicars.track

import akostenko.math.vector.Decart
import akostenko.math.vector.Polar
import akostenko.math.vector.Vector
import org.apache.commons.math3.util.FastMath.toRadians
import org.apache.commons.math3.util.FastMath.PI

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

    internal companion object {
        fun start(x: Double, y: Double, heading: Double, width: Double): TrackBuilder = TrackBuilder(heading, Decart(x, y), width, 0)
    }

}
