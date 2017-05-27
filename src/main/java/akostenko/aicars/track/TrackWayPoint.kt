package akostenko.aicars.track

import akostenko.aicars.math.Vector

data class TrackWayPoint(val section: TrackSection,
                    val position: Vector,
                    val indexInSection: Int,
                    val distanceFromTrackStart: Int)
