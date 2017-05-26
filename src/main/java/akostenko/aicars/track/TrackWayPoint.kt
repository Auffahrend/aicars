package akostenko.aicars.track

import akostenko.aicars.math.Vector

class TrackWayPoint(private val section: TrackSection, private val position: Vector, private val indexInSection: Int, private val distanceFromTrackStart: Int) {

    fun section(): TrackSection {
        return section
    }

    fun position(): Vector {
        return position
    }

    fun indexInSection(): Int {
        return indexInSection
    }

    fun distanceFromTrackStart(): Int {
        return distanceFromTrackStart
    }
}
