package akostenko.aicars.track;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.signum;
import static java.lang.StrictMath.sin;

import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackSection {

    private static final int wayPointStep = 1; // m

    private final int indexOnTrack;
    private final Vector start; // starting point
    private final double length;
    private final double heading; // for turns it's a tangent line to the beginning point
    private final double radius;
    private final double width;
    private final List<TrackWayPoint> wayPoints;
    private final int distanceFromStart;

    TrackSection(int distanceFromStart, int indexOnTrack, Vector start, double length, double radius, double heading, double width) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length of track section must be positive!");
        }

        this.distanceFromStart = distanceFromStart;
        this.indexOnTrack = indexOnTrack;
        this.start = start;
        this.length = length;
        this.radius = radius;
        this.heading = heading;
        this.width = width;

        int totalWayPoints = ((int) length / wayPointStep) + (length % wayPointStep > 0 ? 1 : 0);
        List<TrackWayPoint> wayPoints = new ArrayList<>(totalWayPoints);
        Vector wayPointPosition = start;
        double headingToNextWayPoint = heading;

        if (radius == 0) {
            for (int i = 0; i < totalWayPoints; i++) {
                wayPoints.add(new TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i));
                wayPointPosition = wayPointPosition.plus(new Polar(wayPointStep, headingToNextWayPoint));
            }
        } else {
            double angleBetweenWayPoints = wayPointStep / abs(radius);
            double distanceBetweenWayPoints = 2 * abs(radius) * sin(angleBetweenWayPoints / 2);
            for (int i = 0; i < totalWayPoints; i++) {
                wayPoints.add(new TrackWayPoint(this, wayPointPosition, i, distanceFromStart + i));
                wayPointPosition = wayPointPosition.plus(new Polar(distanceBetweenWayPoints, headingToNextWayPoint));
                headingToNextWayPoint += signum(radius) * angleBetweenWayPoints;
            }
        }

        this.wayPoints = Collections.unmodifiableList(wayPoints);
    }

    public boolean isStraight() {
        return radius == 0;
    }

    public int indexOnTrack() {
        return indexOnTrack;
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

    public double radius() {
        return radius;
    }

    public double width() {
        return width;
    }

    public List<TrackWayPoint> wayPoints() {
        return wayPoints;
    }
}
