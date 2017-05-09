package akostenko.aicars.race.cartest;

import akostenko.aicars.race.Driver;

public class AccelerationTest extends Driver {

    private final double targetSpeed = 150; // km/h
    private double time = 0;
    private boolean targetReached;

    @Override
    public String getName() {
        return String.format("0-%d kph", (int) targetSpeed) + (targetReached ? String.format(" %.3fs", time) : "");
    }

    @Override
    public boolean accelerates() {
        return !targetReached;
    }

    @Override
    public boolean breaks() {
        return targetReached;
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
            targetReached = getCar().speed() * 3.6 >= targetSpeed;
            time += dTime;
        }
    }
}
