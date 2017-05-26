package akostenko.aicars.track

import java.lang.Math.toRadians
import java.lang.StrictMath.PI

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector

import java.util.ArrayList
import java.util.Collections

internal class TrackBuilder {

    private var heading: Double = 0.toDouble()
    private var currentPosition: Vector? = null
    private var width: Double = 0.toDouble()
    private val track = ArrayList<TrackSection>()
    private var totalLength: Int = 0

    fun straight(length: Double): TrackBuilder {
        track.add(TrackSection(totalLength, track.size, currentPosition, length, 0.0, heading, width))
        totalLength += length.toInt()
        currentPosition = currentPosition!!.plus(Polar(length, heading))
        return this
    }

    fun right(radius: Double, degrees: Double): TrackBuilder {
        val angle = toRadians(degrees)
        track.add(TrackSection(totalLength, track.size, currentPosition, angle * radius, radius, heading, width))
        val turnCenter = currentPosition!!.plus(Polar(radius, heading + PI / 2))
        totalLength += (radius * toRadians(degrees)).toInt()
        currentPosition = turnCenter.plus(Polar(radius, PI + PI / 2 + heading + angle))
        heading += angle
        return this
    }

    fun left(radius: Double, degrees: Double): TrackBuilder {
        val angle = toRadians(degrees)
        track.add(TrackSection(totalLength, track.size, currentPosition, angle * radius, -radius, heading, width))
        val turnCenter = currentPosition!!.plus(Polar(radius, heading - PI / 2))
        totalLength += (radius * toRadians(degrees)).toInt()
        currentPosition = turnCenter.plus(Polar(radius, PI + heading - PI / 2 - angle))
        heading -= angle
        return this
    }

    fun done(): List<TrackSection> {
        return Collections.unmodifiableList(track)
    }

    companion object {

        fun start(x: Double, y: Double, heading: Double, width: Double): TrackBuilder {
            val trackBuilder = TrackBuilder()
            trackBuilder.heading = heading
            trackBuilder.currentPosition = Decart(x, y)
            trackBuilder.width = width
            trackBuilder.totalLength = 0
            return trackBuilder
        }
    }

}
