package akostenko.aicars.track;

import akostenko.aicars.math.Vector;

public class TrackSection {

    private final Vector start; // starting point
    private final double length;
    private final double heading; // for turns it's a tangent line to the beginning point
    private final double radius;

    TrackSection(Vector start, double length, double radius, double heading) {
        this.start = start;
        this.length = length;
        this.radius = radius;
        this.heading = heading;
    }

    public boolean isStraight() {
        return radius == 0;
    }

    public Vector start() {
        return start;
    }

    public double length() {
        return length;
    }

    public double heading() {
        return heading;
    }

    public double getRadius() {
        return radius;
    }
}
