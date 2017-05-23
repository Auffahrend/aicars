package akostenko.aicars.track;

import akostenko.aicars.math.Vector;

public class TrackWayPoint {
    private final TrackSection section;
    private final Vector position;
    private final int indexInSection;
    private final int distanceFromTrackStart;

    public TrackWayPoint(TrackSection section, Vector position, int indexInSection, int distanceFromStart) {
        this.section = section;
        this.position = position;
        this.indexInSection = indexInSection;
        this.distanceFromTrackStart = distanceFromStart;
    }

    public TrackSection section() {
        return section;
    }

    public Vector position() {
        return position;
    }

    public int indexInSection() {
        return indexInSection;
    }

    public int distanceFromTrackStart() {
        return distanceFromTrackStart;
    }
}
