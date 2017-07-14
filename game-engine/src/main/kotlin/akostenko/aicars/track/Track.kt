package main.kotlin.akostenko.aicars.track

import akostenko.math.vector.Polar
import akostenko.math.vector.Vector
import main.kotlin.akostenko.aicars.menu.MenuItem
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.abs
import org.apache.commons.math3.util.FastMath.signum
import java.util.*

abstract class Track : MenuItem {

    abstract val width: Double

    abstract val sections: List<TrackSection>

    private val turnMarkersOffsets = listOf(-200, -150, -100, -50)
    val markers: List<TrackMarker> by lazy {
        sections
                .filter { !it.isStraight }
                .map { section -> section to getPrevSection(section)}
                // turns in different directions or straight
                .filter { (section, prevSection) -> section.radius * prevSection.radius <= 0 }
                .flatMap { (section, prevSection) -> turnMarkersOffsets
                        .filter { abs(it.toDouble()) <= prevSection.length }
                        .map{ distance -> createMarker(prevSection, section,
                                getWayPoint(section.wayPoints.first(), distance), (-distance).toString())}
                } +
                listOf(createMarker(sections[0], sections[1], wayPoints[0], "START"))
    }

    private fun createMarker(onSection: TrackSection, turn: TrackSection, at: TrackWayPoint, text: String): TrackMarker {
        val markerOffset : Vector
        if (onSection.isStraight) {
            markerOffset = Polar(width / 2 + 2, onSection.heading + if (turn.radius > 0) -PI / 2 else (PI / 2))
        } else {
            val center = onSection.start + Polar(onSection.radius, onSection.heading + PI / 2)
            markerOffset = Polar(width / 2 + 2, (at.position - center).toPolar().d) * signum(turn.radius)
        }
        return TrackMarker(at.position + markerOffset, text)
    }

    private val wayPoints : List<TrackWayPoint> by lazy { sections.flatMap { it.wayPoints } }

    private fun getWayPoint(from: TrackWayPoint, offsetMeters: Int): TrackWayPoint {
        var index = wayPoints.indexOf(from)
        if (index < 0) throw IllegalArgumentException("No such waypoint $from on track $this")
        index += offsetMeters / TrackSection.wayPointDistanceMeters
        while (index < 0) index+=wayPoints.size
        index %= wayPoints.size
        return wayPoints[index]
    }

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(other: Any?): Boolean {
        return other is Track && title == other.title
    }

    fun getNextSection(current: TrackSection): TrackSection {
        val currentSectionIndex = current.indexOnTrack
        if (sections.size > currentSectionIndex + 1) {
            return sections[currentSectionIndex + 1]
        } else {
            // next lap
            return sections.first()
        }
    }

    fun getPrevSection(current: TrackSection): TrackSection {
        val currentSectionIndex = current.indexOnTrack
        if (currentSectionIndex > 0) {
            return sections[currentSectionIndex - 1]
        } else {
            // previous lap
            return sections.last()
        }
    }

    fun getNextWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.section.wayPoints.size > current.indexInSection + 1) {
            return current.section.wayPoints[current.indexInSection + 1]
        } else {
            return getNextSection(current.section).wayPoints.first()
        }
    }

    fun getPreviousWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.indexInSection > 0) {
            return current.section.wayPoints[current.indexInSection - 1]
        } else {
            return getPrevSection(current.section).wayPoints.last()
        }
    }

    companion object {

        fun forName(name: String): Track {
            when (name) {
                DebugTrack.NAME -> return DebugTrack()
                MonzaTrack.NAME -> return MonzaTrack()
                IndySpeedway.NAME -> return IndySpeedway()
                else -> return defaultTrack()
            }
        }
        private fun defaultTrack(): Track {
            return MonzaTrack()
        }

    }
}

data class TrackMarker(val position : Vector, val text : String)

