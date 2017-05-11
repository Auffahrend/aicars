package akostenko.aicars.race.car;

import static akostenko.aicars.math.Polar.ZERO;
import static akostenko.aicars.math.Vector.PRECISION;
import static akostenko.aicars.model.CarModel.cx;
import static akostenko.aicars.model.CarModel.cy;
import static akostenko.aicars.model.CarModel.frontArea;
import static akostenko.aicars.model.CarModel.mass;
import static akostenko.aicars.model.CarModel.min_rpm;
import static akostenko.aicars.model.CarModel.tyreRadius;
import static akostenko.aicars.model.CarModel.tyreRollingFriction;
import static akostenko.aicars.model.CarModel.tyreStiction;
import static akostenko.aicars.model.CarModel.wingArea;
import static akostenko.aicars.model.EnvironmentModel.airDensity;
import static akostenko.aicars.model.EnvironmentModel.g;
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

    // modeling http://s2.postimg.org/p2hqskx09/V6_engine_edited.png
    private final TorqueMap torqueMap = new TorqueMap(
            new Decart(4000, 360),
            new Decart(6000, 410),
            new Decart(8000, 440),
            new Decart(10400, 460),
            new Decart(12000, 450),
            new Decart(14000, 400));

    private final DRIVER driver;
    private final Gearbox gearbox = new Gearbox(this);
    /** <i>m</i> */
    private double trackDistance = 0;
    /** <i>m</i> */
    private Decart position = Decart.ZERO;
    /** <i>m/s</i> */
    private Vector velocity = ZERO;
    private Vector accelerationA = ZERO;
    private Vector breakingA = ZERO;
    private Vector turningA = ZERO;
    private Vector heading = new Polar(1, 0);
    private Vector steering = new Polar(1, 0);

    //////////////// car telemetry
    private static final Random random = new Random();
    public Iterable<CarTelemetryItem> getTelemetry() {
        List<CarTelemetryItem> items = new ArrayList<>();
        items.add(new CarTelemetryItem("Driver", driver.getName()));
        items.add(new CarTelemetryItem("Distance", trackDistance, "m", 1, textColor));
        items.add(new CarTelemetryItem("Speed", velocity.module() * 3.6, "kmph", 3, velocityColor));
        items.add(new CarTelemetryItem("Accel", accelerationA.module() / g, "g", 3, accelerationColor));
        items.add(new CarTelemetryItem("RPM", rps() * 60 + random.nextInt(120), ""));
        items.add(new CarTelemetryItem("Gear", gearbox.current()+1, ""));
        items.add(new CarTelemetryItem("Breaking", breakingA.module() / g, "g", 3, breakingColor));
        items.add(new CarTelemetryItem("Peak G", peakG() / g, "g", 3, accelerationColor));
//        items.add(new CarTelemetryItem("Downforce", downforceF() / g, "kg"));
        return items;
    }

    private double peakG = 0;
    private long peakGInstant = now().toEpochMilli();
    private double peakG() {
        double current = accelerationA.plus(breakingA).module();
        long now = now().toEpochMilli();
        if (peakG < current || now - peakGInstant > 3000) {
            peakG = current;
            peakGInstant = now;
        }
        return peakG;
    }

    /** <i>m</i> */
    public Decart getPosition() {
        return position;
    }

    /** <i>m/s</i> */
    public Vector speed() {
        return velocity;
    }

    /** lateral acceleration due to engine's torque, <i>g</i> */
    public Vector getAcceleration() {
        return accelerationA.div(g);
    }

    /** lateral acceleration due to breaking and friction forces, <i>g</i> */
    public Vector getBreaking() {
        return breakingA.div(g);
    }

    /** tangential acceleration due to steering, <i>g</i> */
    public Vector getTurning() {
        return turningA.div(g);
    }

    /** direction of the car */
    public Vector getHeading() {
        return heading;
    }

    /** direction of steering wheels */
    public Vector getSteering() {
        return steering;
    }

    //////////////// car physics

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
        double gearboxRPS = velocity.module() / (2*PI* tyreRadius) * gearbox.ratio();
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
        return velocity.module() > PRECISION
                ? new Polar(weightF() * tyreRollingFriction / tyreRadius, velocity.toPolar().d + PI)
                : ZERO;
    }

    /** kg * m/s^2 */
    private double weightF() {
        return mass* g + downforceF();
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
        heading = heading.rotate(driver.turnsLeft() ? -0.01 : driver.turnsRight() ? +0.01 : 0);

        trackDistance += velocity.module() * dt;
        position = position.plus(velocity.multi(dt)).toDecart();
        accelerationA = accelerateA();
        velocity = velocity.plus(accelerationA.multi(dt));

        turningA = turningA();
        velocity = velocity.plus(turningA.multi(dt));

        breakingA = breakingA();
        Vector breaking = breakingA.multi(dt);

        // if breaking forces are stronger then acceleration
        if (breaking.module() > velocity.module()) {
            velocity = ZERO;
        } else {
            velocity = velocity.plus(breaking);
        }
    }

    private Vector turningA() {
        return ZERO;
    }

    public Double trackDistance() {
        return trackDistance;
    }

    public DRIVER getDriver() {
        return driver;
    }

}
