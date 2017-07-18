package akostenko.aicars.track

import akostenko.math.ArcLine
import akostenko.math.Line
import akostenko.aicars.drawing.StraightLinesBuilder
import akostenko.math.vector.Polar
import akostenko.math.vector.Vector
import org.apache.commons.math3.util.FastMath.abs
import org.apache.commons.math3.util.FastMath.signum
import org.apache.commons.math3.util.FastMath.sin
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

        val totalWayPoints = length.toInt() / wayPointDistanceMeters + if (length % wayPointDistanceMeters > 0) 1 else 0
        val wayPoints = ArrayList<TrackWayPoint>(totalWayPoints)
        var wayPointPosition = start
        var headingToNextWayPoint = heading

        if (radius == 0.0) {
            for (i in 0..totalWayPoints - 1) {
                wayPoints.add(TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i))
                wayPointPosition = wayPointPosition.plus(Polar(wayPointDistanceMeters.toDouble(), headingToNextWayPoint))
            }
        } else {
            val angleBetweenWayPoints = wayPointDistanceMeters / abs(radius)
            val distanceBetweenWayPoints = 2.0 * abs(radius) * sin(angleBetweenWayPoints / 2)
            for (i in 0..totalWayPoints - 1) {
                wayPoints.add(TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i))
                wayPointPosition = wayPointPosition.plus(Polar(distanceBetweenWayPoints, headingToNextWayPoint))
                headingToNextWayPoint += signum(radius) * angleBetweenWayPoints
            }
        }

        this.wayPoints = Collections.unmodifiableList(wayPoints)
    }

    val isStraight: Boolean = radius == 0.0

    val borders: Collection<Line> by lazy { if (isStraight) getStraightBorderLines() else getArcBorderLines() }

    private fun getStraightBorderLines(): Collection<Line> {
        val sectionStart = start.asPolar()
        val sectionEnd = start.asPolar() + Polar(length, heading)

        val rightBorderOffset = Polar(width / 2, heading + Math.PI / 2)
        val leftBorderOffset = Polar(width / 2, heading - Math.PI / 2)
        return StraightLinesBuilder(true)
                .between(sectionStart + rightBorderOffset, sectionEnd + rightBorderOffset)
                .between(sectionStart +  leftBorderOffset, sectionEnd + leftBorderOffset)
                .build() +
                if (indexOnTrack == 0)
                    StraightLinesBuilder(false)
                            .between(sectionStart + leftBorderOffset, sectionStart + rightBorderOffset)
                            .build()
                    else emptyList()
    }

    private fun getArcBorderLines(): Collection<Line> {
        val center = start.asCartesian() + Polar(radius, heading + Math.PI / 2)
        var from = (start - center).asPolar().d - if (radius < 0) Math.PI else 0.0
        var to = from + length / radius
        if (radius < 0) {
            val t = to
            to = from
            from = t
        }
        return listOf(ArcLine(center, radius - width / 2, from, to),
                ArcLine(center, radius + width / 2, from, to)) +
                if (indexOnTrack == 0) {
                    val directionToStartFromCenter = Polar(1.0, (start - center).asPolar().d)
                    StraightLinesBuilder(false)
                            .between(center + directionToStartFromCenter*(abs(radius)-width/2),
                                    center + directionToStartFromCenter*(abs(radius)+width/2))
                            .build()
                }
                else emptyList()
    }

    companion object {
        internal val wayPointDistanceMeters = 1 // m
    }
}

