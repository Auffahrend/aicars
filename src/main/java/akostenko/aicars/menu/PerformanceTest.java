package akostenko.aicars.menu;

import akostenko.aicars.race.Driver;
import akostenko.aicars.race.cartest.AccelerationAndBreakingTest;
import akostenko.aicars.race.cartest.AccelerationTest;

public class PerformanceTest extends Mode {
    static final String NAME = "Car test";

    @Override
    public String getTitle() {
        return NAME;
    }

    public Driver newDriver() {
        return new AccelerationTest();
    }
}
