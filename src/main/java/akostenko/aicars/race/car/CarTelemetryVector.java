package akostenko.aicars.race.car;

import static akostenko.aicars.math.Decart.ZERO;

import akostenko.aicars.drawing.Scale;
import akostenko.aicars.math.Vector;

import org.newdawn.slick.Color;

public class CarTelemetryVector {
    /** point on car, where this vector is applied;
     * CG by default
     */
    private final Vector appliedTo;
    private final Vector vector;
    private final Scale scale;
    private final Color color;

    public CarTelemetryVector(Vector vector, Scale scale, Color color) {
        this(ZERO, vector, scale, color);
    }

    public CarTelemetryVector(Vector appliedTo, Vector vector, Scale scale, Color color) {
        this.appliedTo = appliedTo;
        this.vector = vector;
        this.scale = scale;
        this.color = color;
    }

    public Vector appliedTo() {
        return appliedTo;
    }

    public Vector vector() {
        return vector;
    }
}
