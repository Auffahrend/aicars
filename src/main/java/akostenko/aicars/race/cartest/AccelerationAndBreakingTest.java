package akostenko.aicars.race.cartest;

import akostenko.aicars.race.Driver;

public class AccelerationAndBreakingTest extends Driver {

    private final double targetSpeed; // m/s
    private double time = 0;
    private boolean speedReached;
    private boolean targetReached;

    public AccelerationAndBreakingTest(double targetSpeedKMH) {
        this.targetSpeed = targetSpeedKMH;
    }

    @Override
    public String getName() {
        return String.format("0-%d-0 kph", (int) targetSpeed) + (targetReached ? String.format(" %.3fs", time) : "");
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
    public double steering() {
        return 0;
    }

    @Override
    public void update(double dTime) {
        if (!targetReached) {
            speedReached = speedReached || getCar().speed().module() * 3.6 >= targetSpeed;
            targetReached = speedReached && getCar().speed().module() <= 0.1;
            time += dTime;
        }
    }
}
