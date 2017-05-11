package akostenko.aicars.track;

import static java.lang.Math.toRadians;
import static java.lang.StrictMath.PI;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TrackBuilder {

    private double heading;
    private Vector currentPosition;
    private List<TrackSection> track = new ArrayList<>();

    static TrackBuilder start(double x, double y, double heading) {
        TrackBuilder trackBuilder = new TrackBuilder();
        trackBuilder.heading = heading;
        trackBuilder.currentPosition = new Decart(x, y);
        return trackBuilder;
    }

    TrackBuilder straight(double length) {
        track.add(new TrackSection(currentPosition, length, 0, heading));
        currentPosition = currentPosition.plus(new Polar(length, heading));
        return this;
    }

    TrackBuilder right(double radius, double degrees) {
        double angle = toRadians(degrees);
        track.add(new TrackSection(currentPosition, angle*radius, radius, heading));
        Vector turnCenter = currentPosition.plus(new Polar(radius, heading+PI/2));
        currentPosition = turnCenter.plus(new Polar(radius, PI+PI/2+heading+angle));
        heading += angle;
        return this;
    }

    TrackBuilder left(double radius, double degrees) {
        double angle = toRadians(degrees);
        track.add(new TrackSection(currentPosition, angle*radius, -radius, heading));
        Vector turnCenter = currentPosition.plus(new Polar(radius, heading-PI/2));
        currentPosition = turnCenter.plus(new Polar(radius, PI + heading - PI / 2 - angle));
        heading -= angle;
        return this;
    }

    List<TrackSection> done() {
        return Collections.unmodifiableList(track);
    }

}
