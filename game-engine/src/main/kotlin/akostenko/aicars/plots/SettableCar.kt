package akostenko.aicars.plots

import akostenko.aicars.model.CarModel.mass
import akostenko.aicars.race.Driver
import akostenko.aicars.race.car.Car
import akostenko.aicars.track.Track
import akostenko.math.vector.Polar
import akostenko.math.vector.Vector

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
        get() = frontSlipF().dot(heading.normal()) / mass

    /** m/s^2  */
    val rearTurningForceA: Double
        get() = rearSlipF().dot(heading.normal()) / mass
}
