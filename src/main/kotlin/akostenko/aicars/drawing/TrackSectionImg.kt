package akostenko.aicars.drawing

import akostenko.aicars.math.Polar
import akostenko.aicars.track.TrackSection
import org.newdawn.slick.Color
import java.lang.Math.PI

object TrackSectionImg {

    fun build(section: TrackSection, color: Color): Collection<Line> {
        if (section.isStraight) {
            return getStraightBorderLines(section, color)
        } else {
            return getArcBorderLines(section, color)
        }
    }

    private fun getStraightBorderLines(section: TrackSection, color: Color): Collection<Line> {
        val sectionStart = section.start.toPolar()
        val sectionEnd = section.start.toPolar().plus(Polar(section.length, section.heading))

        val rightBorderOffset = Polar(section.width / 2, section.heading + PI/2)
        val leftBorderOffset = Polar(section.width / 2, section.heading - PI/2)
        return StraightLinesBuilder(color, 3f)
                .between(sectionStart + rightBorderOffset, sectionEnd + rightBorderOffset)
                .between(sectionStart+ leftBorderOffset, sectionEnd + leftBorderOffset)
                .build()
    }

    private fun getArcBorderLines(section: TrackSection, color: Color): Collection<Line> {
        val center = section.start.toDecart() + Polar(section.radius, section.heading + PI/2)
        var from = (section.start - center).toPolar().d - if (section.radius < 0) PI else 0.0
        var to = from + section.length / section.radius
        if (section.radius < 0) {
            val t = to
            to = from
            from = t
        }
        return listOf(ArcLine(center, section.radius - section.width/2, from, to, color, 3f),
                ArcLine(center, section.radius + section.width/2, from, to, color, 3f))
    }

}
