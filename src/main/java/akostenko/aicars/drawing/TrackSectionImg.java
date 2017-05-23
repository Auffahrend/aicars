package akostenko.aicars.drawing;

import static java.lang.Math.PI;
import static java.util.stream.Collectors.toList;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.track.TrackBorder;
import akostenko.aicars.track.TrackSection;

import org.newdawn.slick.Color;

import java.util.Collection;

public class TrackSectionImg {

    public static Collection<Line> get(TrackSection section, double trackWidth, Scale scale, Color color, Decart camera) {
        Decart cameraPx = camera.negative().multi(scale.getPixels()/scale.getSize());
        if (section.isStraight()) {
            return getStraightBorderLines(section, trackWidth).stream()
                    .map(line -> line.scale(scale))
                    .map(line -> line.position(cameraPx, color, 3))
                    .collect(toList());
        } else {
            throw new IllegalArgumentException("Not implemented");
        }
    }

    private static Collection<LinesBuilder.LocalLine> getStraightBorderLines(TrackSection section, double trackWidth) {
        Polar sectionStart = section.start().toPolar();
        Polar sectionEnd = section.start().toPolar().plus(new Polar(section.length(), section.heading()));

        Polar rightBorder = new Polar(trackWidth / 2, section.heading() + PI / 2);
        Polar leftBorder = new Polar(trackWidth / 2, section.heading() - PI / 2);
        return new LinesBuilder()
                .between(sectionStart.plus(rightBorder), sectionEnd.plus(rightBorder))
                .between(sectionStart.plus(leftBorder), sectionEnd.plus(leftBorder))
                .build();
    }

    public static Collection<TrackBorder> getBorders(TrackSection section, double trackWidth) {
        if (section.isStraight()) {
            return getStraightBorderLines(section, trackWidth).stream()
                    .map(TrackSectionImg::createBorder)
                    .collect(toList());
        } else {
            throw new IllegalArgumentException("Not implemented");
        }
    }

    private static TrackBorder createBorder(LinesBuilder.LocalLine line) {
        return new TrackBorder(line.from(), line.to());
    }

}
