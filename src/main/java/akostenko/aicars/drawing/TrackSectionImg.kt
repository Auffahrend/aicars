package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import akostenko.aicars.track.TrackBorder
import akostenko.aicars.track.TrackSection
import org.newdawn.slick.Color
import java.lang.Math.PI
import java.util.stream.Collectors.toList

object TrackSectionImg {

    fun build(section: TrackSection, trackWidth: Double, scale: Scale, color: Color, camera: Decart): Collection<Line> {
        val cameraPx = camera.unaryMinus().times((scale.pixels / scale.size).toDouble())
        if (section.isStraight) {
            return getStraightBorderLines(section, trackWidth).stream()
                    .map { line -> line.scale(scale) }
                    .map { line -> line.position(cameraPx, color, 3f) }
                    .collect(toList())
        } else {
            throw IllegalArgumentException("Not implemented")
        }
    }

    private fun getStraightBorderLines(section: TrackSection, trackWidth: Double): Collection<LinesBuilder.LocalLine> {
        val sectionStart = section.start.toPolar()
        val sectionEnd = section.start.toPolar().plus(Polar(section.length, section.heading))

        val rightBorder = Polar(trackWidth / 2, section.heading + PI / 2)
        val leftBorder = Polar(trackWidth / 2, section.heading - PI / 2)
        return LinesBuilder()
                .between(sectionStart.plus(rightBorder), sectionEnd.plus(rightBorder))
                .between(sectionStart.plus(leftBorder), sectionEnd.plus(leftBorder))
                .build()
    }

    fun getBorders(section: TrackSection, trackWidth: Double): Collection<TrackBorder> {
        if (section.isStraight) {
            return getStraightBorderLines(section, trackWidth).stream()
                    .map({ createBorder(it) })
                    .collect(toList())
        } else {
            throw IllegalArgumentException("Not implemented")
        }
    }

    private fun createBorder(line: LinesBuilder.LocalLine): TrackBorder {
        return TrackBorder(line.from, line.to)
    }

}
