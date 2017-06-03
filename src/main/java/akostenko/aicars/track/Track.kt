package akostenko.aicars.track

import akostenko.aicars.menu.MenuItem
import java.util.*

abstract class Track : MenuItem {

    abstract val width: Double

    abstract val sections: List<TrackSection>

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(other: Any?): Boolean {
        return other is Track && title == other.title
    }

    fun getNextWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.section.wayPoints.size > current.indexInSection + 1) {
            return current.section.wayPoints[current.indexInSection + 1]
        } else {
            val currentSectionIndex = current.section.indexOnTrack
            if (sections.size > currentSectionIndex + 1) {
                return sections[currentSectionIndex + 1].wayPoints.first()
            } else {
                // next lap
                return sections.first().wayPoints.first()
            }
        }
    }

    fun getPreviousWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.indexInSection > 0) {
            return current.section.wayPoints[current.indexInSection - 1]
        } else {
            val currentSectionIndex = current.section.indexOnTrack
            if (currentSectionIndex > 0) {
                val previousSection = sections[currentSectionIndex - 1]
                return previousSection.wayPoints.last()
            } else {
                // previous lap
                val lastSection = sections.last()
                return lastSection.wayPoints[lastSection.wayPoints.size - 1]
            }
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
