package akostenko.aicars.plots;

import static akostenko.aicars.model.CarModel.mass;
import static java.lang.Math.PI;

import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;
import akostenko.aicars.race.Driver;
import akostenko.aicars.race.car.Car;
import akostenko.aicars.track.Track;

class SettableCar<DRIVER extends Driver> extends Car<DRIVER> {

    SettableCar(DRIVER driver, Track track) {
        super(driver, track);
    }

    SettableCar<DRIVER> setVelocity(Vector velocity) {
        this.velocity = velocity;
        this.gearbox.update();
        return this;
    }

    SettableCar<DRIVER> setSteering(final double radians) {
        this.steering = new Polar(1, radians);
        return this;
    }

    Double getTorque(double rps) {
        return torqueMap.get(rps);
    }

    /** m/s^2 */
    Double getDownforceA() {
        return downforceF() / mass;
    }

    Double getRps() {
        return rps();
    }

    /** m/s^2 */
    Double getFrontTurningForceA() {
        return frontSlipF().dot(heading.rotate(PI/2)) / mass;
    }

    /** m/s^2 */
    Double getRearTurningForceA() {
        return rearSlipF().dot(heading.rotate(PI/2)) / mass;
    }
}
