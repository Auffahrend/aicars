package akostenko.aicars.menu;

import akostenko.aicars.race.Driver;
import akostenko.aicars.race.cartest.AccelerationAndBreakingTest;
import akostenko.aicars.race.cartest.AccelerationTest;

import java.util.Arrays;
import java.util.List;

public class PerformanceTests extends Mode {
    static final String NAME = "Car tests";

    @Override
    public String getTitle() {
        return NAME;
    }

    public List<Driver> getDrivers() {
        return Arrays.asList(
                new AccelerationTest(100),
                new AccelerationTest(300),
                new AccelerationAndBreakingTest(150)
                );
    }
}
