package akostenko.aicars.race;

import static akostenko.aicars.race.CarTelemetryItem.accelerationColor;
import static akostenko.aicars.race.CarTelemetryItem.breakingColor;
import static akostenko.aicars.race.CarTelemetryItem.textColor;
import static akostenko.aicars.race.CarTelemetryItem.velocityColor;
import static java.lang.Math.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;
import static java.time.Instant.now;

import akostenko.aicars.math.Decart;
import akostenko.aicars.race.carparts.TorqueMap;

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
    private final double max_rpm = 12000;
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
    private final double tyreRadius = 0.3;

    private final DRIVER driver;
    private final Gearbox gearbox = new Gearbox();
    /** <i>m</i> */
    private double trackDistance = 0;
    /** <i>m/s</i> */
    private double velocity = 0;

    //////////////// car telemetry
    private static final Random random = new Random();
    Iterable<CarTelemetryItem> getTelemetry() {
        List<CarTelemetryItem> items = new ArrayList<>();
        items.add(new CarTelemetryItem("Driver", driver.getName()));
        items.add(new CarTelemetryItem("Distance", trackDistance, "m", 1, textColor));
        items.add(new CarTelemetryItem("Speed", velocity * 3.6, "kmph", 3, velocityColor));
        items.add(new CarTelemetryItem("Accel", accelerateA() / g, "g", 3, accelerationColor));
        items.add(new CarTelemetryItem("Peak accel", peakAcceleration() / g, "g", 3, accelerationColor));
        items.add(new CarTelemetryItem("RPM", rps() * 60 + random.nextInt(120), ""));
        items.add(new CarTelemetryItem("Gear", gearbox.current()+1, ""));
        items.add(new CarTelemetryItem("Gear ratio", gearbox.ratio(), "", 2, textColor));
        items.add(new CarTelemetryItem("Breaking", breakingA() / g, "g", 3, breakingColor));
        items.add(new CarTelemetryItem("Peak break", peakBreaking() / g, "g", 3, breakingColor));
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
    private double accelerateA() {
        if (driver.accelerates()) {
            double engineForce = torqueMap.get(rps()) * gearbox.ratio() / tyreRadius;
            return min(engineForce, weightF() * tyreStiction) / mass;
        } else {
            return 0;
        }
    }

    /**
     * @return current engine's revolutions per second
     */
    private double rps() {
        double gearboxRPS = velocity / (2*PI*tyreRadius) * gearbox.ratio();
        if (gearboxRPS < min_rpm/60) {
            gearboxRPS = min_rpm/60;
        }
        return gearboxRPS;
    }

    /** m/s^2 */
    private double breakingA() {
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
                * velocity > 0.01 ? 1 : 0;
    }

    /** kg * m/s^2 */
    private double weightF() {
        return mass*g + downforceF();
    }


    ////////////////

    public Car(DRIVER driver) {
        this.driver = driver;
        driver.setCar(this);
    }

    void update(int msDelta) {
        double dt = 1. * msDelta / 1000;
        driver.update(dt);
        gearbox.update();

        trackDistance += velocity * dt;
        double dV_accelerate = accelerateA() * dt;
        double dV_break = (velocity + dV_accelerate > 0 ? -1 : 1) * breakingA() * dt;

        // if breaking forces are stronger then acceleration
        if (abs(dV_break) > abs(dV_accelerate)) {
            double newVelocity = velocity + dV_accelerate + dV_break;
            // velocity can not change its sign due to breaking. Braking only stops vehicle
            if (velocity != 0) {
                velocity = velocity * newVelocity < 0 ? 0 : newVelocity;
            }
        } else {
            velocity += dV_accelerate + dV_break;
        }
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

    private class Gearbox {
        private final List<Gear> gears = new ArrayList<>(8);
        private int current;

        public Gearbox() {
            gears.add(new Gear(60./3.6, 10000, tyreRadius));
            gears.add(new Gear(125./3.6, 12000, tyreRadius));
            gears.add(new Gear(155./3.6, 12000, tyreRadius));
            gears.add(new Gear(190./3.6, 12000, tyreRadius));
            gears.add(new Gear(225./3.6, 12000, tyreRadius));
            gears.add(new Gear(260./3.6, 12000, tyreRadius));
            gears.add(new Gear(295./3.6, 12000, tyreRadius));
            gears.add(new Gear(330./3.6, 12000, tyreRadius));
        }

        void update() {
            current = chooseCurrentGear();
        }

        private int chooseCurrentGear() {
            double shaftRPS = velocity / (2*PI*tyreRadius);
            for (int i=0; i < gears.size(); i++) {
                double gearRPS = gears.get(i).ratio * shaftRPS;
                if (gearRPS < max_rpm/60) {
                    return i;
                }
            }

            return gears.size() - 1; // over RPS on highest gear
        }

        /** index of gear, starting from 0 */
        int current() {
            return current;
        }

        double ratio() {
            return gears.get(current).ratio;
        }
    }

    private static class Gear {
        private final double ratio;

        Gear(double maxSpeed, double rpm, double tyreRadius) {
            if (rpm <= 0 || maxSpeed <= 0 || tyreRadius <= 0) {
                throw new IllegalArgumentException("Must be > 0");
            }

            this.ratio = (rpm / 60) / (maxSpeed / (2*PI*tyreRadius));
        }
    }
}
