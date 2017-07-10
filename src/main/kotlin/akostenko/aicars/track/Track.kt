package akostenko.aicars.track

import akostenko.aicars.math.Vector
import akostenko.aicars.menu.MenuItem
import java.util.*

abstract class Track : MenuItem {

    abstract val width: Double

    abstract val sections: List<TrackSection>

    private val turnMarkersOffsets = listOf(-200, -150, -100, -50)
    val markers: List<TrackMarker> by lazy {
        sections
                .filter { !it.isStraight }
                .map { section -> { turnMarkersOffsets.filter { it.toDouble() <= this.getPrevSection(section).length } }}
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

