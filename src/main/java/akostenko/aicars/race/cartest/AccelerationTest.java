package akostenko.aicars.race.cartest;

import akostenko.aicars.race.Driver;

public class AccelerationTest extends Driver {

    private final double targetSpeed = 100; // km/h
    private double time = 0;
    private boolean targetReached;

    @Override
    public String getName() {
        return String.format("0-%d kph", (int) targetSpeed) + (targetReached ? String.format(" %.3fs", time) : "");
    }

    @Override
    public double accelerating() {
        return !targetReached ? 1 : 0;
    }

    @Override
    public double breaking() {
        return targetReached ? 1 : 0;
    }

    @Override
    public double steering() {
        return 0;
    }

    @Override
    public void update(double dTime) {
        if (!targetReached) {
            targetReached = getCar().speed().module() * 3.6 >= targetSpeed;
            time += dTime;
        }
    }
}
