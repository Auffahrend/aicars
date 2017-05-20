package akostenko.aicars.plots;

import akostenko.aicars.math.Vector;
import akostenko.aicars.race.Driver;
import akostenko.aicars.race.car.Car;

public class SettableCar<DRIVER extends Driver> extends Car {

    public SettableCar(DRIVER driver) {
        super(driver);
    }

    Car setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    public Double getTorque(double rps) {
        return torqueMap.get(rps);
    }
}
