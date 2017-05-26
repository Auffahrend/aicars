package akostenko.aicars.track

import akostenko.aicars.math.Polar
import akostenko.aicars.math.Vector
import java.lang.StrictMath.abs
import java.lang.StrictMath.signum
import java.lang.StrictMath.sin
import java.util.*

class TrackSection internal constructor(distanceFromStart: Int,
                                        val indexOnTrack: Int,
                                        val start: Vector,
                                        val length: Double,
                                        val radius: Double,
                                        val heading: Double // for turns it's a tangent line to the beginning point
                                        , val width: Double) {
    val wayPoints: List<TrackWayPoint>

    init {
        if (length <= 0) {
            throw IllegalArgumentException("Length of track section must be positive!")
        }

        val totalWayPoints = length.toInt() / wayPointStep + if (length % wayPointStep > 0) 1 else 0
        val wayPoints = ArrayList<TrackWayPoint>(totalWayPoints)
        var wayPointPosition = start
        var headingToNextWayPoint = heading

        if (radius == 0.0) {
            for (i in 0..totalWayPoints - 1) {
                wayPoints.add(TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i))
                wayPointPosition = wayPointPosition.plus(Polar(wayPointStep.toDouble(), headingToNextWayPoint))
            }
        } else {
            val angleBetweenWayPoints = wayPointStep / abs(radius)
            val distanceBetweenWayPoints = 2.0 * abs(radius) * sin(angleBetweenWayPoints / 2)
            for (i in 0..totalWayPoints - 1) {
                wayPoints.add(TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i))
                wayPointPosition = wayPointPosition.plus(Polar(distanceBetweenWayPoints, headingToNextWayPoint))
                headingToNextWayPoint += signum(radius) * angleBetweenWayPoints
            }
        }

        this.wayPoints = Collections.unmodifiableList(wayPoints)
    }

    val isStraight: Boolean
        get() = radius == 0.0


    companion object {

        private val wayPointStep = 1 // m
    }
}
