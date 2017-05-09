package akostenko.aicars.race;

import static java.lang.Math.PI;
import static java.lang.StrictMath.min;
import static java.time.Instant.now;

import java.util.ArrayList;
import java.util.List;

public class Car<DRIVER extends Driver> {
    /** <i>m/s^2</i> */
    private final double g = 9.8;
    /** <i>kg / m^3</i> */
    private final double airDensity = 1.204;

    /** <i>kg</i> */
    private final double mass = 700;
    /** <i>watts = kg * m^2 / s^3</i> */
    private final double power = 1.05*1000*mass;
    /** non-dimensional */
    private final double cx = 0.9; // Cx
    /** non-dimensional */
    private final double cy = 1.0033; // Cy
    /** <i>m^2</i> */
    private final double frontArea = 0.8;
    /** <i>m^2</i> */
    private final double wingArea = 10;
    /** stiction coefficient between hot slick tyre and dry asphalt, non-dimensional */
    private final double tyreStiction = 1.10;
    /** rolling friction coefficient between rubber and asphalt, <i>m</i> */
    private final double tyreRollingFriction = 0.015;
    private final DRIVER driver;

    /** <i>m</i> */
    private double trackDistance = 0;
    /** <i>m/s</i> */
    private double velocity = 0;
    /** <i>1/s</i> */
    private double rpm = 10000;
    /** <i>m</i> */
    private double tyreRadius = 0.3;

    //////////////// car telemetry
    Iterable<CarTelemetryItem> getTelemetry() {
        List<CarTelemetryItem> items = new ArrayList<>();
        items.add(new CarTelemetryItem("Driver", driver.getName()));
        items.add(new CarTelemetryItem("Distance", trackDistance, "m", 1, CarTelemetryItem.textColor));
        items.add(new CarTelemetryItem("Speed", velocity, "m/s", 3, CarTelemetryItem.velocityColor));
        items.add(new CarTelemetryItem("Accel", accelerateA() / g, "g", 3, CarTelemetryItem.accelerationColor));
        items.add(new CarTelemetryItem("Peak accel", peakAcceleration() / g, "g", 3, CarTelemetryItem.accelerationColor));
        items.add(new CarTelemetryItem("Gear rat", gearRatio(), ""));
        items.add(new CarTelemetryItem("Breaking", breakingA() / g, "g", 3, CarTelemetryItem.breakingColor));
        items.add(new CarTelemetryItem("Peak break", peakBreaking() / g, "g", 3, CarTelemetryItem.breakingColor));
        items.add(new CarTelemetryItem("Downforce", downforceF() / g, "kg"));
        return items;
    }

    private double peakAcceleration = 0;
    private long peakAccelerationInstant = now().toEpochMilli();
    private double peakAcceleration() {
        double current = accelerateA();
        long now = now().toEpochMilli();
        if (peakAcceleration < current || now - peakAccelerationInstant > 3000) {
            peakAcceleration = current;
            peakAccelerationInstant = now;
        }
        return peakAcceleration;
    }

    private double peakBreaking = 0;
    private long peakBreakingInstant = now().toEpochMilli();
    private double peakBreaking() {
        double current = breakingA();
        long now = now().toEpochMilli();
        if (peakBreaking < current || now - peakBreakingInstant > 3000) {
            peakBreaking = current;
            peakBreakingInstant = now;
        }
        return peakBreaking;
    }

    /** m/s^2 */
    public double accelerateA() {
        if (driver.accelerates()) {
            double engineForce = power / (rpm / gearRatio()) / tyreRadius;
            return min(engineForce, weightF() * tyreStiction) / mass;
        } else {
            return 0;
        }
    }

    /** m/s^2 */
    public double breakingA() {
        return dragF()/mass
                + rollingFrictionF()/mass
                + breakingF()/mass;
    }

    /** m/s */
    public double speed() {
        return velocity;
    }

    //////////////// car physics

    /**
     * @return kg * m / s^2
     */
    private double dragF() {
        return cx * airDensity * velocity * velocity * frontArea / 2;
    }

    /**
     * @return kg * m / s^2
     */
    private double downforceF() {
        return cy * airDensity * velocity * velocity * wingArea / 2;
    }

    /** kg * m/s^2 */
    private double rollingFrictionF() {
        return weightF() * tyreRollingFriction / tyreRadius
                * velocity > 0.01 ? 1 : 0
                ;
    }

    /** kg * m/s^2 */
    private double weightF() {
        return mass*g + downforceF();
    }


    private double gearRatio() {
        return rpm / (velocity / tyreRadius*2*PI);
    }

    ////////////////

    public Car(DRIVER driver) {
        this.driver = driver;
        driver.setCar(this);
    }

    void update(int msDelta) {
        double dt = 1. * msDelta / 1000;
        driver.update(dt);
        trackDistance += velocity * dt;
        velocity += accelerateA() * dt
                + (velocity > 0 ? -1 : 1) * breakingA() * dt;
        if (velocity < 0.1 && driver.breaks()) velocity = 0;
        if (velocity < 0.01) velocity = 0;
    }

    Double trackDistance() {
        return trackDistance;
    }

    DRIVER getDriver() {
        return driver;
    }

    private double breakingF() {
        return driver.breaks() ? weightF() * tyreStiction : 0.;
    }
}
