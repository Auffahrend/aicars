package akostenko.aicars.track

import akostenko.math.vector.Vector

data class TrackWayPoint(val section: TrackSection,
                         val position: Vector,
                         val indexInSection: Int,
                         val distanceFromTrackStart: Int)
