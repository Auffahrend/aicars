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
    public boolean accelerates() {
        return !speedReached;
    }

    @Override
    public boolean breaks() {
        return speedReached;
    }

    @Override
    public boolean turnesLeft() {
        return false;
    }

    @Override
    public boolean turnesRight() {
        return false;
    }

    @Override
    public void update(double dTime) {
        if (!targetReached) {
            speedReached = speedReached || getCar().speed() >= targetSpeed;
            targetReached = speedReached && getCar().speed() <= 0.1;
            time += dTime;
        }
    }
}
