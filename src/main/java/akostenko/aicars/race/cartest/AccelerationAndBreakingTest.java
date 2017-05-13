package akostenko.aicars.race.cartest;

import akostenko.aicars.race.Driver;

public class AccelerationAndBreakingTest extends Driver {

    private final double targetSpeed = 150 / 3.6; // m/s
    private double time = 0;
    private boolean speedReached;
    private boolean targetReached;

    @Override
    public String getName() {
        return "0-150-0 kph" + (targetReached ? String.format(" %.3fs", time) : "");
    }

    @Override
    public double accelerating() {
        return !speedReached ? 1 : 0;
    }

    @Override
    public double breaking() {
        return speedReached ? 1 : 0;
    }

    @Override
    public double turning() {
        return 0;
    }

    @Override
    public void update(double dTime) {
        if (!targetReached) {
            speedReached = speedReached || getCar().speed().module() >= targetSpeed;
            targetReached = speedReached && getCar().speed().module() <= 0.1;
            time += dTime;
        }
    }
}
