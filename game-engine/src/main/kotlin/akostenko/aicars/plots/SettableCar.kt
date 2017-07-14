package main.kotlin.akostenko.aicars.plots

import main.kotlin.akostenko.aicars.model.CarModel.mass
import org.apache.commons.math3.util.FastMath.PI

import akostenko.math.vector.Polar
import akostenko.math.vector.Vector
import main.kotlin.akostenko.aicars.race.Driver
import main.kotlin.akostenko.aicars.race.car.Car
import main.kotlin.akostenko.aicars.track.Track

internal class SettableCar<DRIVER : Driver>(driver: DRIVER, track: Track) : Car<DRIVER>(driver, track) {

    fun setVelocity(velocity: Vector): SettableCar<DRIVER> {
        this.velocity = velocity
        this.gearbox.update()
        return this
    }

    fun setSteering(radians: Double): SettableCar<DRIVER> {
        this.steering = Polar(1.0, radians)
        return this
    }

    fun getTorque(rps: Double): Double {
        return torqueMap.get(rps)
    }

    /** m/s^2  */
    val downforceA: Double
        get() = downforceF() / mass

    val rps: Double
        get() = rps()

    /** m/s^2  */
    val frontTurningForceA: Double
        get() = frontSlipF().dot(heading.rotate(PI / 2)) / mass

    /** m/s^2  */
    val rearTurningForceA: Double
        get() = rearSlipF().dot(heading.rotate(PI / 2)) / mass
}
