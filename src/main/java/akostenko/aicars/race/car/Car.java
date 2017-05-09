package akostenko.aicars.race.car;

import static akostenko.aicars.math.Polar.ZERO;
import static akostenko.aicars.race.car.CarTelemetryItem.accelerationColor;
import static akostenko.aicars.race.car.CarTelemetryItem.breakingColor;
import static akostenko.aicars.race.car.CarTelemetryItem.textColor;
import static akostenko.aicars.race.car.CarTelemetryItem.velocityColor;
import static java.lang.Math.PI;
import static java.lang.StrictMath.min;
import static java.time.Instant.now;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;
import akostenko.aicars.race.Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Car<DRIVER extends Driver> {
    /** <i>m/s^2</i> */
    private final double g = 9.81;
    /** <i>kg / m^3</i> */
    private final double airDensity = 1.204;

    /** <i>kg</i> */
    private final double mass = 700;
    /** <i>1/s</i> */
    final double max_rpm = 12000;
    /** <i>1/s</i> */
    private final double min_rpm = 3500;
    // modeling http://s2.postimg.org/p2hqskx09/V6_engine_edited.png
    private final TorqueMap torqueMap = new TorqueMap(
            new Decart(4000, 360),
            new Decart(6000, 410),
            new Decart(8000, 440),
            new Decart(10400, 460),
            new Decart(12000, 450),
            new Decart(14000, 400));
    /** non-dimensional */
    private final double cx = 0.80; // Cx
    /** non-dimensional */
    private final double cy = 1.0033; // Cy
    /** <i>m^2</i> */
    private final double frontArea = 1;
    /** <i>m^2</i> */
    private final double wingArea = 5;
    /** stiction coefficient between hot slick tyre and dry asphalt, non-dimensional */
    private final double tyreStiction = 1.25;
    /** rolling friction coefficient between rubber and asphalt, <i>m</i> */
    private final double tyreRollingFriction = 0.015;
    /** <i>m</i> */
    final double tyreRadius = 0.3;

    private final DRIVER driver;
    private final Gearbox gearbox = new Gearbox(this);
    /** <i>m</i> */
    private double trackDistance = 0;
    /** <i>m/s</i> */
    private Vector velocity = ZERO;
    private Vector acceleration = ZERO;
    private Vector breaking = ZERO;
    private Vector turning = ZERO;
    private Vector heading = new Polar(1, 0);
    private Vector steering = new Polar(1, 0);

    //////////////// car telemetry
    private static final Random random = new Random();
    public Iterable<CarTelemetryItem> getTelemetry() {
        List<CarTelemetryItem> items = new ArrayList<>();
        items.add(new CarTelemetryItem("Driver", driver.getName()));
        items.add(new CarTelemetryItem("Distance", trackDistance, "m", 1, textColor));
        items.add(new CarTelemetryItem("Speed", velocity.module() * 3.6, "kmph", 3, velocityColor));
        items.add(new CarTelemetryItem("Accel", accelerateA().module() / g, "g", 3, accelerationColor));
        items.add(new CarTelemetryItem("Peak accel", peakAcceleration() / g, "g", 3, accelerationColor));
        items.add(new CarTelemetryItem("RPM", rps() * 60 + random.nextInt(120), ""));
        items.add(new CarTelemetryItem("Gear", gearbox.current()+1, ""));
        items.add(new CarTelemetryItem("Gear ratio", gearbox.ratio(), "", 2, textColor));
        items.add(new CarTelemetryItem("Breaking", breakingA().module() / g, "g", 3, breakingColor));
        items.add(new CarTelemetryItem("Peak break", peakBreaking() / g, "g", 3, breakingColor));
        items.add(new CarTelemetryItem("Downforce", downforceF() / g, "kg"));
        return items;
    }

    private double peakAcceleration = 0;
    private long peakAccelerationInstant = now().toEpochMilli();
    private double peakAcceleration() {
        double current = accelerateA().module();
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
        double current = breakingA().module();
        long now = now().toEpochMilli();
        if (peakBreaking < current || now - peakBreakingInstant > 3000) {
            peakBreaking = current;
            peakBreakingInstant = now;
        }
        return peakBreaking;
    }

    /** m/s^2 */
    private Vector accelerateA() {
        if (driver.accelerates()) {
            double engineForce = torqueMap.get(rps()) * gearbox.ratio() / tyreRadius;
            double acceleration = min(engineForce, weightF() * tyreStiction) / mass;
            return heading.multi(acceleration);
        } else {
            return ZERO;
        }
    }

    /**
     * @return current engine's revolutions per second
     */
    private double rps() {
        double gearboxRPS = velocity.module() / (2*PI*tyreRadius) * gearbox.ratio();
        if (gearboxRPS < min_rpm/60) {
            gearboxRPS = min_rpm/60;
        }
        return gearboxRPS;
    }

    /** m/s^2 */
    private Vector breakingA() {
        return (
                dragF()
                        .plus(rollingFrictionF())
                        .plus(breakingF()))
                .div(mass);
    }

    /** m/s */
    public Vector speed() {
        return velocity;
    }

    //////////////// car physics

    /**
     * @return kg * m / s^2
     */
    private Vector dragF() {
        return new Polar(cx * airDensity * velocity.module() * velocity.module() * frontArea / 2,
                velocity.toPolar().d + PI);
    }

    /**
     * @return kg * m / s^2
     */
    private double downforceF() {
        return cy * airDensity * velocity.module() * velocity.module() * wingArea / 2;
    }

    /** kg * m/s^2 */
    private Vector rollingFrictionF() {
        return velocity.module() > 0.01
                ? new Polar(weightF() * tyreRollingFriction / tyreRadius, velocity.toPolar().d + PI)
                : ZERO;
    }

    /** kg * m/s^2 */
    private double weightF() {
        return mass*g + downforceF();
    }

    private Vector breakingF() {
        return driver.breaks()
                ? new Polar(weightF() * tyreStiction, velocity.toPolar().d + PI)
                : ZERO;
    }

    ////////////////

    public Car(DRIVER driver) {
        this.driver = driver;
        driver.setCar(this);
    }

    public void update(int msDelta) {
        double dt = 1. * msDelta / 1000;
        driver.update(dt);
        gearbox.update();

        trackDistance += velocity.module() * dt;
        Vector dV_accelerate = accelerateA().multi(dt);
        velocity = velocity.plus(dV_accelerate);

        Vector dV_break = breakingA().multi(dt);

        // if breaking forces are stronger then acceleration
        if (dV_break.module() > velocity.module()) {
            velocity = ZERO;
        } else {
            velocity = velocity.plus(dV_break);
        }
    }

    public Double trackDistance() {
        return trackDistance;
    }

    public DRIVER getDriver() {
        return driver;
    }

}
