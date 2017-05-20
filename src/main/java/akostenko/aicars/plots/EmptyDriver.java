package akostenko.aicars.plots;

import akostenko.aicars.race.Driver;

public class EmptyDriver extends Driver {
    @Override
    public double accelerating() {
        return 0;
    }

    @Override
    public double breaking() {
        return 0;
    }

    @Override
    public double steering() {
        return 0;
    }

    @Override
    public String getName() {
        return "no name";
    }

    @Override
    public void update(double dTime) {

    }
}
