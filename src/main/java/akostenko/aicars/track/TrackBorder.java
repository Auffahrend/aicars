package akostenko.aicars.track;

import akostenko.aicars.math.Vector;

public class TrackBorder {
    private final Vector from;
    private final Vector to;

    public TrackBorder(Vector from, Vector to) {
        this.from = from;
        this.to = to;
    }

    public Vector from() {
        return from;
    }

    public Vector to() {
        return to;
    }
}
