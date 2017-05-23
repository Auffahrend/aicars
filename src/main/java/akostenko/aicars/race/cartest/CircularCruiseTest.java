package akostenko.aicars.race.cartest;

import akostenko.aicars.model.CarModel;
import akostenko.aicars.race.Driver;

public class CircularCruiseTest extends Driver {
    @Override
    public double accelerating() {
        return 1;
    }

    @Override
    public double breaking() {
        return 0;
    }

    @Override
    public double steering() {
        return CarModel.peakLateralForceAngle;
    }

    @Override
    public String getName() {
        return "Max speed turning";
    }

    @Override
    public void update(double dTime) {

    }
}
