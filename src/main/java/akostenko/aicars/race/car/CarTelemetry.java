package akostenko.aicars.race.car;

import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collection;

public class CarTelemetry {
    public static final Color breakingColor = new Color(250, 70, 70);
    public static final Color velocityColor = new Color(250, 250, 50);
    public static final Color accelerationColor = new Color(50, 250, 50);
    public static final Color textColor = new Color(240, 240, 240);
    public static final Color turningColor = new Color(50, 50, 250);

    private final Car car;
    private Collection<CarTelemetryScalar> scalars = new ArrayList<>();
    private Collection<CarTelemetryVector> vectors = new ArrayList<>();

    public CarTelemetry(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public Collection<CarTelemetryScalar> getScalars() {
        return scalars;
    }

    public Collection<CarTelemetryVector> getVectors() {
        return vectors;
    }
}
