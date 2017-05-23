package akostenko.aicars.race.car;

import static akostenko.aicars.math.Polar.ZERO;
import static akostenko.aicars.math.Vector.PRECISION;
import static akostenko.aicars.model.CarModel.axleTrack;
import static akostenko.aicars.model.CarModel.cx;
import static akostenko.aicars.model.CarModel.cy;
import static akostenko.aicars.model.CarModel.frontArea;
import static akostenko.aicars.model.CarModel.frontWeightPercent;
import static akostenko.aicars.model.CarModel.highSpeedCorneringThreshold;
import static akostenko.aicars.model.CarModel.mass;
import static akostenko.aicars.model.CarModel.massCenterHeight;
import static akostenko.aicars.model.CarModel.maxSteering;
import static akostenko.aicars.model.CarModel.min_rpm;
import static akostenko.aicars.model.CarModel.peakLateralForceAngle;
import static akostenko.aicars.model.CarModel.rearWeightPercent;
import static akostenko.aicars.model.CarModel.tyreRadius;
import static akostenko.aicars.model.CarModel.tyreRollingFriction;
import static akostenko.aicars.model.CarModel.tyreStiction;
import static akostenko.aicars.model.CarModel.wheelbase;
import static akostenko.aicars.model.CarModel.wingArea;
import static akostenko.aicars.model.CarModel.yawInertia;
import static akostenko.aicars.model.EnvironmentModel.airDensity;
import static akostenko.aicars.model.EnvironmentModel.g;
import static akostenko.aicars.race.car.CarTelemetry.accelerationColor;
import static akostenko.aicars.race.car.CarTelemetry.breakingColor;
import static akostenko.aicars.race.car.CarTelemetry.textColor;
import static akostenko.aicars.race.car.CarTelemetry.turningColor;
import static akostenko.aicars.race.car.CarTelemetry.velocityColor;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.hypot;
import static java.lang.StrictMath.min;
import static java.lang.StrictMath.signum;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.toRadians;
import static java.time.Instant.now;

import akostenko.aicars.drawing.Scale;
import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;
import akostenko.aicars.race.Driver;

import java.util.Random;

public class Car<DRIVER extends Driver> {

    // modeling http://s2.postimg.org/p2hqskx09/V6_engine_edited.png
    protected final TorqueMap torqueMap = new TorqueMap(
            new Decart(4000/60, 360),
            new Decart(6000/60, 410),
            new Decart(8000/60, 440),
            new Decart(10400/60, 460),
            new Decart(12000/60, 450),
            new Decart(14000/60, 400));

    private final DRIVER driver;
    protected final Gearbox gearbox = new Gearbox(this);
    /** <i>m</i> */
    private double trackDistance = 0;
    /** <i>m</i> */
    protected Decart position = Decart.ZERO;
    /** <i>m/s</i> */
    protected Vector velocity = ZERO;
    private Vector accelerationA = ZERO;
    private Vector breakingA = ZERO;
    private Vector turningA = ZERO;
    protected Polar heading = new Polar(1, 0);
    /** <i>rad/s</i> */
    private double carRotationSpeed = 0;
    protected Polar steering = new Polar(1, 0);

    //////////////// car telemetry
    private static final Random random = new Random();
    private final Scale velocityScale = new Scale(100, 200);
    private final Scale gScale = new Scale(5, 200);

    public CarTelemetry getTelemetry() {
        CarTelemetry carTelemetry = new CarTelemetry(this);

        carTelemetry.getScalars().add(new CarTelemetryScalar("Driver", driver.getName()));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Distance", trackDistance, "m", 1, textColor));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Speed", velocity.module() * 3.6, "kmph", 3, velocityColor));
        carTelemetry.getVectors().add(new CarTelemetryVector(velocity, velocityScale, velocityColor));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Accel", accelerationA.module() / g, "g", 3, accelerationColor));
        carTelemetry.getVectors().add(new CarTelemetryVector(accelerationA.div(g), gScale, accelerationColor));
        carTelemetry.getScalars().add(new CarTelemetryScalar("RPM", rps() * 60 + random.nextInt(120), ""));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Gear", gearbox.current()+1, ""));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Breaking", breakingA.module() / g, "g", 3, breakingColor));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Turning", turningA.module() / g, "g", 3, turningColor));
        carTelemetry.getVectors().add(new CarTelemetryVector(breakingA.div(g), gScale, breakingColor));
        carTelemetry.getScalars().add(new CarTelemetryScalar("Peak G", peakG() / g, "g", 3, accelerationColor));
//        carTelemetry.getScalars().add(new CarTelemetryScalar("Downforce", downforceF() / g, "kg"));
        carTelemetry.getVectors().add(new CarTelemetryVector(new Polar(wheelbase*frontWeightPercent, heading.d), frontSlipF().div(mass * g), gScale, turningColor));
        carTelemetry.getVectors().add(new CarTelemetryVector(new Polar(wheelbase*rearWeightPercent, heading.d+PI), rearSlipF().div(mass * g), gScale, turningColor));
        carTelemetry.getVectors().add(new CarTelemetryVector(turningA.div(g), gScale, turningColor));
        return carTelemetry;
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

    /** direction of the car */
    public Vector getHeading() {
        return heading;
    }

    /** direction of steering wheels */
    public Polar getSteering() {
        return steering;
    }

    //////////////// car physics

    /** m/s^2 */
    private Vector accelerateA() {
        if (driver.accelerating() > 0) {
            double engineForce = torqueMap.get(rps()) * gearbox.ratio() / tyreRadius;
            double acceleration = min(engineForce, (rearAxleWeightF()) * tyreStiction) / mass;
            return heading.multi(acceleration * min(driver.accelerating(), 1));
        } else {
            return ZERO;
        }
    }

    /**
     * @return current engine's revolutions per second
     */
    protected double rps() {
        double gearboxRPS = velocity.module() / (2*PI* tyreRadius) * gearbox.ratio();
        if (gearboxRPS < min_rpm/60) {
            gearboxRPS = min_rpm/60;
        }
        return gearboxRPS;
    }

    /** m/s^2 */
    protected Vector breakingA() {
        return (
                dragF()
                        .plus(rollingFrictionF())
                        .plus(breakingF()))
                .div(mass);
    }

    /**
     * @return kg * m / s^2
     */
    protected Vector dragF() {
        return velocity.multi(-cx * airDensity * velocity.module() * frontArea / 2);
    }

    /**
     * @return kg * m / s^2
     */
    protected double downforceF() {
        return cy * airDensity * velocity.moduleSqr() * wingArea / 2;
    }

    /** kg * m/s^2 */
    protected Vector rollingFrictionF() {
        return velocity.module() > PRECISION
                ? new Polar(weightF() * tyreRollingFriction / tyreRadius, velocity.toPolar().d + PI)
                : ZERO;
    }

    /** kg * m/s^2 */
    protected double weightF() {
        return mass*g + downforceF();
    }

    /** kg * m/s^2 */
    private double rearAxleWeightF() {
        Vector horizontalForce = accelerationA.plus(breakingA).multi(mass);
        double longitudeForce = horizontalForce.dot(heading);
        return weightF()*frontWeightPercent + longitudeForce * massCenterHeight/wheelbase;
    }

    /** kg * m/s^2 */
    private double frontAxleWeightF() {
        Vector horizontalForce = accelerationA.plus(breakingA).multi(mass);
        double longitudeForce = horizontalForce.dot(heading);
        return weightF()*rearWeightPercent - longitudeForce * massCenterHeight/wheelbase;
    }

    private Vector breakingF() {
        return new Polar(weightF() * tyreStiction, velocity.module() > 0 ? velocity.toPolar().d + PI : heading.d + PI)
                .multi(min(1, driver.breaking()));
    }

    private Vector tyresSlipA() {
        return rearSlipF().plus(frontSlipF())
                .div(mass);
    }

    double tyreSlipForce(double slipAngle, double axleWeight) {
        // see http://www.asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html
        // at about 3 degrees lateral force should peak with values about wight on steering wheels

        double tyreSlipForceFunction;
        if (abs(slipAngle) <= peakLateralForceAngle) {
            tyreSlipForceFunction = slipAngle / peakLateralForceAngle * tyreStiction;
        } else {
            tyreSlipForceFunction = signum(slipAngle) * tyreStiction
                    * (1 - 0.05*((abs(slipAngle)-peakLateralForceAngle)/toRadians(10)));
        }

        return axleWeight * tyreSlipForceFunction;
    }

    protected Vector frontSlipF() {
        return headingNormal().rotate(steering.d)
                .multi(-tyreSlipForce(frontSlipAngle(), frontAxleWeightF()));

    }

    protected Vector rearSlipF() {
        return headingNormal()
                .multi(-tyreSlipForce(rearSlipAngle(), rearAxleWeightF()));
    }

    private double frontSlipAngle() {
        return velocity.module() > 0
                ? atan((lateralVelocity() + carRotationSpeed * wheelbase * frontWeightPercent) / velocity.module())
                 - steering.d * signum(longitudeVelocity())
                : signum(carRotationSpeed) * PI /2;
    }

    private double rearSlipAngle() {
        return velocity.module() > 0
                ? atan((lateralVelocity() - carRotationSpeed * wheelbase * rearWeightPercent) / velocity.module())
                : -signum(carRotationSpeed) * PI /2;
    }

    private double lateralVelocity() {
        return headingNormal().dot(velocity);
    }

    private double longitudeVelocity() {
        return heading.dot(velocity);
    }

    private Vector headingNormal() {
        return heading.rotate(PI/2);
    }

    private double rotationTorque() {
        return frontSlipF().dot(headingNormal()) * hypot(wheelbase * frontWeightPercent, axleTrack/2)
                - rearSlipF().dot(headingNormal()) * hypot(wheelbase * rearWeightPercent, axleTrack /2);
    }

    ////////////////

    public Car(DRIVER driver) {
        this.driver = driver;
        driver.setCar(this);
    }

    public void update(int msDelta) {
        double seconds = 1. * msDelta / 1000;
        driver.update(seconds);
        gearbox.update();
        steering = new Polar(1, driver.steering() * maxSteering);
        trackDistance += velocity.module() * seconds;
        position = position.plus(velocity.multi(seconds)).toDecart();

        applyAccelerationForces(seconds);
        applyTurningForces(seconds);
        applyBreakingForces(seconds);
    }

    private void applyAccelerationForces(double seconds) {
        accelerationA = accelerateA();
        velocity = velocity.plus(accelerationA.multi(seconds));
    }

    private void applyBreakingForces(double seconds) {
        breakingA = breakingA();
        Vector breaking = breakingA.multi(seconds);

        // if breaking forces are stronger then acceleration
        if (breaking.module() > velocity.module()) {
            velocity = new Polar(0, heading.d);
        } else {
            velocity = velocity.plus(breaking);
        }
    }

    private void applyTurningForces(double seconds) {
        if (velocity.module() >= highSpeedCorneringThreshold) {
            double rotationSpeedChange = rotationTorque() * (seconds / yawInertia);
            if (abs(driver.steering()) < PRECISION
                    && abs(rotationSpeedChange) > abs(carRotationSpeed)
                    && (carRotationSpeed + rotationSpeedChange) * carRotationSpeed < 0) {
                //
                rotationSpeedChange = -carRotationSpeed;
            }
            carRotationSpeed += rotationSpeedChange;
            heading = heading.rotate(carRotationSpeed * seconds);
            turningA = tyresSlipA();
            velocity = velocity.plus(turningA.multi(seconds));
        } else if (velocity.module() > 0 && abs(steering.d) > sqrt(PRECISION)) {
            // pure geometry solution
            double frontInnerWheelTurnRadius = wheelbase / sin(abs(steering.d));
            double rearInnerWheelTurnRadius = hypot(frontInnerWheelTurnRadius, wheelbase);
            double rearAxleCenterTurnRadius = rearInnerWheelTurnRadius + axleTrack / 2;
            double massCenterTurningRadius = hypot(rearAxleCenterTurnRadius, wheelbase*rearWeightPercent);

            double angle = signum(steering.d) * velocity.module() * seconds / massCenterTurningRadius;
            carRotationSpeed = 0;
            heading = heading.rotate(angle);
            turningA = velocity.rotate(angle).minus(velocity).div(seconds);
            velocity = velocity.rotate(angle);
        } else {
            carRotationSpeed = 0;
        }
    }

    public Double trackDistance() {
        return trackDistance;
    }

    public DRIVER getDriver() {
        return driver;
    }

    public Car<DRIVER> turn(double heading) {
        this.heading = new Polar(1, heading);
        return this;
    }

    public Car<DRIVER> move(Vector position) {
        this.position = position.toDecart();
        return this;
    }
}
