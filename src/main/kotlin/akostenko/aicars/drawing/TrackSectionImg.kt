package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.track.TrackBorder
import akostenko.aicars.track.TrackSection
import org.newdawn.slick.Color
import java.lang.Math.PI

object TrackSectionImg {

    fun build(section: TrackSection, scale: Scale, color: Color, camera: Decart): Collection<Line> {
        val cameraPx = scale.to(-camera).toDecart()
        if (section.isStraight) {
            return getStraightBorderLines(section)
                    .map { line -> line.scale(scale) }
                    .map { line -> line.place(cameraPx, color, 3f) }
        } else {
            return getArcBorderLines(section)
                    .map { arc -> arc.scale(scale) }
                    .map { arc -> arc.place(cameraPx, color, 3f)}
        }
    }

    private fun getStraightBorderLines(section: TrackSection): Collection<LinesBuilder.RelativeLine> {
        val sectionStart = section.start.toPolar()
        val sectionEnd = section.start.toPolar().plus(Polar(section.length, section.heading))

        val rightBorderOffset = Polar(section.width / 2, section.heading + PI/2)
        val leftBorderOffset = Polar(section.width / 2, section.heading - PI/2)
        return LinesBuilder()
                .between(sectionStart + rightBorderOffset, sectionEnd + rightBorderOffset)
                .between(sectionStart+ leftBorderOffset, sectionEnd + leftBorderOffset)
                .build()
    }

    private fun getArcBorderLines(section: TrackSection): Collection<LinesBuilder.RelativeArc> {
        val center = section.start + Polar(section.radius, section.heading + PI/2)
        var from = (section.start - center).toPolar().d - if (section.radius < 0) PI else 0.0
        var to = from + section.length / section.radius
        if (section.radius < 0) {
            val t = to;
            to = from;
            from = t;
        }
        return listOf(LinesBuilder.RelativeArc(center, section.radius - section.width/2, from, to),
                LinesBuilder.RelativeArc(center, section.radius + section.width/2, from, to))
    }

    fun getBorders(section: TrackSection): Collection<TrackBorder> {
        if (section.isStraight) {
            return getStraightBorderLines(section)
                    .map({ createBorder(it) })
        } else {
            throw IllegalArgumentException("Not implemented")
        }
    }

    private fun createBorder(line: LinesBuilder.RelativeLine): TrackBorder {
        return TrackBorder(line.from, line.to)
    }

}
