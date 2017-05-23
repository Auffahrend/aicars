package akostenko.aicars.menu;

import akostenko.aicars.race.Driver;
import akostenko.aicars.race.cartest.AccelerationAndBreakingTest;
import akostenko.aicars.race.cartest.AccelerationTest;
import akostenko.aicars.race.cartest.CircularCruiseTest;

import java.util.Arrays;
import java.util.List;

public class CarPerformanceTests extends Mode {
    static final String NAME = "Car tests";

    @Override
    public String getTitle() {
        return NAME;
    }

    public List<Driver> getDrivers() {
        return Arrays.asList(
                new AccelerationTest(100),
                new AccelerationTest(300),
                new AccelerationAndBreakingTest(150),
                new CircularCruiseTest()
                );
    }
}
