package akostenko.aicars.track

import akostenko.aicars.menu.MenuItem
import java.util.*

abstract class Track : MenuItem {

    abstract val width: Double

    abstract fun sections(): List<TrackSection>

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(obj: Any?): Boolean {
        return obj is Track && title == obj.title
    }

    fun getNextWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.section().wayPoints.size > current.indexInSection() + 1) {
            return current.section().wayPoints[current.indexInSection() + 1]
        } else {
            val currentSectionIndex = current.section().indexOnTrack
            if (sections().size > currentSectionIndex + 1) {
                return sections()[currentSectionIndex + 1].wayPoints[0]
            } else {
                // next lap
                return sections()[0].wayPoints[0]
            }
        }
    }

    fun getPreviousWayPoint(current: TrackWayPoint): TrackWayPoint {
        if (current.indexInSection() > 0) {
            return current.section().wayPoints[current.indexInSection() - 1]
        } else {
            val currentSectionIndex = current.section().indexOnTrack
            if (currentSectionIndex > 0) {
                val previousSection = sections()[currentSectionIndex - 1]
                return previousSection.wayPoints[previousSection.wayPoints.size - 1]
            } else {
                // previous lap
                val lastSection = sections()[sections().size - 1]
                return lastSection.wayPoints[lastSection.wayPoints.size - 1]
            }
        }
    }

    companion object {
        fun forName(name: String): Track {
            when (name) {
                StraightTrack.NAME -> return StraightTrack()
                CircularTrack.NAME -> return CircularTrack()
                else -> return defaultTrack()
            }
        }

        private fun defaultTrack(): Track {
            return StraightTrack()
        }
    }
}
