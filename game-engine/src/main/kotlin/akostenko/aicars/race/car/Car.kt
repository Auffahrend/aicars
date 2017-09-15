package akostenko.aicars.race.car

import akostenko.aicars.GameSettings
import akostenko.aicars.drawing.CarImg
import akostenko.aicars.drawing.Scale
import akostenko.aicars.model.CarModel.axleTrack
import akostenko.aicars.model.CarModel.cx
import akostenko.aicars.model.CarModel.cy
import akostenko.aicars.model.CarModel.frontArea
import akostenko.aicars.model.CarModel.frontWeightPercent
import akostenko.aicars.model.CarModel.highSpeedCorneringThreshold
import akostenko.aicars.model.CarModel.mass
import akostenko.aicars.model.CarModel.massCenterHeight
import akostenko.aicars.model.CarModel.maxSteering
import akostenko.aicars.model.CarModel.min_rpm
import akostenko.aicars.model.CarModel.peakLateralForceAngle
import akostenko.aicars.model.CarModel.rearWeightPercent
import akostenko.aicars.model.CarModel.tyreRadius
import akostenko.aicars.model.CarModel.tyreRollingFriction
import akostenko.aicars.model.CarModel.tyreStiction
import akostenko.aicars.model.CarModel.wheelbase
import akostenko.aicars.model.CarModel.wingArea
import akostenko.aicars.model.CarModel.yawInertia
import akostenko.aicars.model.EnvironmentModel.airDensity
import akostenko.aicars.model.EnvironmentModel.g
import akostenko.aicars.race.Driver
import akostenko.aicars.race.car.CarTelemetry.Companion.accelerationColor
import akostenko.aicars.race.car.CarTelemetry.Companion.breakingColor
import akostenko.aicars.race.car.CarTelemetry.Companion.textColor
import akostenko.aicars.race.car.CarTelemetry.Companion.turningColor
import akostenko.aicars.race.car.CarTelemetry.Companion.velocityColor
import akostenko.aicars.track.Track
import akostenko.aicars.track.TrackWayPoint
import akostenko.math.MathUtils
import akostenko.math.vector.Cartesian
import akostenko.math.vector.Polar
import akostenko.math.vector.Polar.Companion.ZERO
import akostenko.math.vector.Vector
import akostenko.math.vector.Vector.Companion.PRECISION
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.abs
import org.apache.commons.math3.util.FastMath.atan
import org.apache.commons.math3.util.FastMath.hypot
import org.apache.commons.math3.util.FastMath.min
import org.apache.commons.math3.util.FastMath.signum
import org.apache.commons.math3.util.FastMath.sin
import org.apache.commons.math3.util.FastMath.sqrt
import org.apache.commons.math3.util.FastMath.toRadians
import java.time.Instant.now
import java.util.*

open class Car<DRIVER : Driver>(val driver: DRIVER, internal val track: Track) {

    // modeling http://s2.postimg.org/p2hqskx09/V6_engine_edited.png
    protected val torqueMap = TorqueMap(
            Cartesian(4000.0 / 60, 360.0),
            Cartesian(6000.0 / 60, 410.0),
            Cartesian(8000.0 / 60, 440.0),
            Cartesian(10400.0 / 60, 460.0),
            Cartesian(12000.0 / 60, 450.0),
            Cartesian(14000.0 / 60, 400.0))
    protected val gearbox = Gearbox(this)
    val maxSpeed = gearbox.maxSpeed(tyreRadius)
    private val laps = 0
    /** *m*  */
    var closestWP: TrackWayPoint
        private set
    /** *m*  */
    /** *m*  */
    var position = Cartesian.ZERO
        protected set
    private var odometer = 0.0
    /** *m/s*  */
    protected var velocity: Vector = ZERO
    var accelerationA: Vector = ZERO
        private set
    var breakingA: Vector = ZERO
        private set
    var turningA: Vector = ZERO
        private set
    /** direction of the car  */
    var heading : Polar
        private set
    /** *rad/s*  */
    var carRotationSpeed = 0.0
        private set
    /** direction of steering wheels  */
    var steering = Polar(1.0, 0.0)
        protected set
    private val velocityScale = Scale(100.0, 200f)
    private val gScale = Scale(5.0, 200f)
    private val closestWayPointSelector: (Collection<TrackWayPoint>) -> TrackWayPoint = {
        it.sortedBy({ wp -> (wp.position - position).module() })
                .first() }

    val trackDistance: Int
        get() = closestWP.distanceFromTrackStart

    //////////////// car telemetry
    private val random = Random()
    val telemetry: CarTelemetry
        get() {
            val carTelemetry = CarTelemetry(this)

            carTelemetry.scalars.add(CarTelemetryScalar("Driver", driver.name))
            carTelemetry.scalars.add(CarTelemetryScalar("Distance", closestWP.distanceFromTrackStart.toDouble(), "m", 1, textColor))
            carTelemetry.scalars.add(CarTelemetryScalar("Speed", velocity.module() * 3.6, "kmph", 3, velocityColor))
            carTelemetry.vectors.add(CarTelemetryVector(speed, velocityScale, velocityColor))
            carTelemetry.scalars.add(CarTelemetryScalar("Accel", accelerationA.module() / g, "g", 3, accelerationColor))
            carTelemetry.vectors.add(CarTelemetryVector(acceleration, gScale, accelerationColor))
            carTelemetry.scalars.add(CarTelemetryScalar("RPM", rps() * 60 + random.nextInt(120), ""))
            carTelemetry.scalars.add(CarTelemetryScalar("Gear", (gearbox.current + 1).toDouble(), ""))
            carTelemetry.scalars.add(CarTelemetryScalar("Breaking", breakingA.module() / g, "g", 3, breakingColor))
            carTelemetry.scalars.add(CarTelemetryScalar("Turning", turningA.module() / g, "g", 3, turningColor))
            carTelemetry.vectors.add(CarTelemetryVector(breaking, gScale, breakingColor))
            carTelemetry.scalars.add(CarTelemetryScalar("Peak G", peakG() / g, "g", 3, accelerationColor))
            carTelemetry.vectors.add(CarTelemetryVector(Polar(wheelbase * frontWeightPercent, heading.d), frontSlipF() / (mass * g), gScale, turningColor))
            carTelemetry.vectors.add(CarTelemetryVector(Polar(wheelbase * rearWeightPercent, heading.d + PI), rearSlipF() / (mass * g), gScale, turningColor))
            carTelemetry.vectors.add(CarTelemetryVector(turningA.div(g), gScale, turningColor))
            //        carTelemetry.getScalars().add(new CarTelemetryScalar("Downforce", downforceF() / g, "kg"));
            return carTelemetry
        }


    private var peakG = 0.0
    private var peakGInstant = now().toEpochMilli()
    private fun peakG(): Double {
        val current = (accelerationA + breakingA).module()
        val now = now().toEpochMilli()
        if (peakG < current || now - peakGInstant > 3000) {
            peakG = current
            peakGInstant = now
        }
        return peakG
    }

    /** *m/s*  */
    val speed: Vector
        get() = velocity

    /** lateral acceleration due to engine's torque, *g*  */
    val acceleration: Vector
        get() = accelerationA / g

    /** lateral acceleration due to breaking and friction forces, *g*  */
    val breaking: Vector
        get() = breakingA / g

    //////////////// car physics

    /** m/s^2  */
    private fun accelerateA(): Vector {
        if (driver.accelerating() > 0) {
            val engineForce = torqueMap.get(rps()) * gearbox.ratio / tyreRadius
            val acceleration = min(engineForce, rearAxleWeightF() * tyreStiction) / mass
            return heading * (acceleration * min(driver.accelerating(), 1.0))
        } else {
            return ZERO
        }
    }

    /**
     * @return current engine's revolutions per second
     */
    protected fun rps(): Double {
        var gearboxRPS = velocity.module() / (2.0 * PI * tyreRadius) * gearbox.ratio
        if (gearboxRPS < min_rpm / 60) {
            gearboxRPS = min_rpm / 60
        }
        return gearboxRPS
    }

    /** m/s^2  */
    protected fun breakingA(): Vector = (dragF() + rollingFrictionF() + breakingF()) / mass

    /**
     * @return kg * m / s^2
     */
    protected fun dragF(): Vector = -velocity * (cx * airDensity * velocity.module() * frontArea / 2)

    /**
     * @return kg * m / s^2
     */
    protected fun downforceF(): Double = cy * airDensity * velocity.moduleSqr() * wingArea / 2

    /** kg * m/s^2  */
    protected fun rollingFrictionF(): Vector {
        return if (velocity.module() > PRECISION)
            Polar(weightF() * tyreRollingFriction / tyreRadius, velocity.asPolar().d + PI)
        else
            ZERO
    }

    /** kg * m/s^2  */
    protected fun weightF(): Double = mass * g + downforceF()

    /** kg * m/s^2  */
    private fun rearAxleWeightF(): Double {
        val horizontalForce = (accelerationA + breakingA) * mass
        val longitudeForce = horizontalForce.dot(heading)
        return weightF() * frontWeightPercent + longitudeForce * massCenterHeight / wheelbase
    }

    /** kg * m/s^2  */
    private fun frontAxleWeightF(): Double {
        val horizontalForce = (accelerationA + breakingA) * mass
        val longitudeForce = horizontalForce.dot(heading)
        return weightF() * rearWeightPercent - longitudeForce * massCenterHeight / wheelbase
    }

    private fun breakingF(): Vector = Polar(weightF() * tyreStiction,
            if (velocity.module() > 0) velocity.asPolar().d + PI else heading.d + PI) * min(1.0, driver.breaking())

    private fun tyresSlipA(): Vector = (rearSlipF() + frontSlipF()) / mass

    internal fun tyreSlipForce(slipAngle: Double, axleWeight: Double): Double {
        // see http://www.asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html
        // at about 3 degrees lateral force should peak with values about wight on steering wheels

        val tyreSlipForceFunction: Double
        if (abs(slipAngle) <= peakLateralForceAngle) {
            tyreSlipForceFunction = slipAngle / peakLateralForceAngle * tyreStiction
        } else {
            tyreSlipForceFunction = signum(slipAngle) * tyreStiction * (1 - 0.05 * ((abs(slipAngle) - peakLateralForceAngle) / toRadians(10.0)))
        }

        return axleWeight * tyreSlipForceFunction
    }

    protected fun frontSlipF(): Vector = (heading.normal().rotate(steering.d)) * (-tyreSlipForce(frontSlipAngle(), frontAxleWeightF()))

    protected fun rearSlipF(): Vector = heading.normal() * (-tyreSlipForce(rearSlipAngle(), rearAxleWeightF()))

    private fun frontSlipAngle(): Double {
        return if (velocity.module() > 0)
            atan((lateralVelocity() + carRotationSpeed * wheelbase * frontWeightPercent) / velocity.module()) - steering.d * signum(longitudeVelocity())
        else
            signum(carRotationSpeed) * PI / 2
    }

    private fun rearSlipAngle(): Double {
        return if (velocity.module() > 0)
            atan((lateralVelocity() - carRotationSpeed * wheelbase * rearWeightPercent) / velocity.module())
        else
            -signum(carRotationSpeed) * PI / 2
    }

    private fun lateralVelocity(): Double {
        return heading.normal().dot(velocity)
    }

    private fun longitudeVelocity(): Double {
        return heading.dot(velocity)
    }

    private fun rotationTorque(): Double {
        return frontSlipF().dot(heading.normal()) * hypot(wheelbase * frontWeightPercent, axleTrack / 2) -
                rearSlipF().dot(heading.normal()) * hypot(wheelbase * rearWeightPercent, axleTrack / 2)
    }

    init {
        driver.car = this
        heading = Polar(1.0, 0.0)
        position = track.sections.first().start.asCartesian()
        closestWP = closestWayPointSelector(track.sections.flatMap { section -> section.wayPoints })
    }

    fun update(msDelta: Int) {
        val seconds = 1.0 * msDelta / 1000
        driver.update(seconds)
        gearbox.update()
        steering = Polar(1.0, driver.steering() * maxSteering)
        odometer += velocity.module() * seconds
        position += (velocity * seconds).asCartesian()
        closestWP = findClosestWayPoint()

        applyAccelerationForces(seconds)
        applyTurningForces(seconds)
        applyBreakingForces(seconds)
        applyCollisionImpact()
    }

    private fun applyCollisionImpact() {
        if (GameSettings.instance.collisions.isOn) {
            val collisions = listOf(closestWP.section, track.getNextSection(closestWP.section), track.getPrevSection(closestWP.section))
                    .flatMap { it.borders }
                    .filter { it.collidable }
                    .flatMap { border -> CarImg.build(this).filter { it.collidable }.map { carLine -> border to carLine } }
                    .flatMap { MathUtils.findIntersection(it) }

            if (collisions.isNotEmpty()) {
                velocity = ZERO
//                heading = Polar(1.0, (closestWP.position - track.getPreviousWayPoint(closestWP).position).asPolar().d)
            }
        }
    }

    private fun findClosestWayPoint(): TrackWayPoint {
        var changed = true
        var closest = closestWP
        while (changed) {
            val current = closestWayPointSelector(listOf(closest, track.getNextWayPoint(closest), track.getPreviousWayPoint(closest)))
            changed = (closest != current)
            closest = current
        }
        return closest
    }

    private fun applyAccelerationForces(seconds: Double) {
        accelerationA = accelerateA()
        velocity += accelerationA * seconds
    }

    private fun applyBreakingForces(seconds: Double) {
        breakingA = breakingA()
        val breaking = breakingA * seconds

        // if breaking forces are stronger then acceleration
        if (breaking.module() > velocity.module()) {
            velocity = Polar(0.0, heading.d)
        } else {
            velocity += breaking
        }
    }

    private fun applyTurningForces(seconds: Double) {
        if (velocity.module() >= highSpeedCorneringThreshold) {
            var rotationSpeedChange = rotationTorque() * (seconds / yawInertia)
            if (abs(driver.steering()) < PRECISION
                    && abs(rotationSpeedChange) > abs(carRotationSpeed)
                    && (carRotationSpeed + rotationSpeedChange) * carRotationSpeed < 0) {
                //
                rotationSpeedChange = -carRotationSpeed
            }
            carRotationSpeed += rotationSpeedChange
            heading = heading.rotate(carRotationSpeed * seconds)
            turningA = tyresSlipA()
            velocity += turningA * seconds
        } else if (velocity.module() > 0 && abs(steering.d) > sqrt(PRECISION)) {
            // pure geometry solution
            val frontInnerWheelTurnRadius = wheelbase / sin(abs(steering.d))
            val rearInnerWheelTurnRadius = hypot(frontInnerWheelTurnRadius, wheelbase)
            val rearAxleCenterTurnRadius = rearInnerWheelTurnRadius + axleTrack / 2
            val massCenterTurningRadius = hypot(rearAxleCenterTurnRadius, wheelbase * rearWeightPercent)

            val angle = signum(steering.d) * velocity.module() * seconds / massCenterTurningRadius
            carRotationSpeed = 0.0
            heading = heading.rotate(angle)
            turningA = (velocity.rotate(angle) - velocity) / seconds
            velocity = velocity.rotate(angle)
        } else {
            carRotationSpeed = 0.0
        }
    }

    fun turn(heading: Double): Car<DRIVER> {
        this.heading = Polar(1.0, heading)
        return this
    }

    fun move(position: Vector): Car<DRIVER> {
        this.position = position.asCartesian()
        return this
    }
}
