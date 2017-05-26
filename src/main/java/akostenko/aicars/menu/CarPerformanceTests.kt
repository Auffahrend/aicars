package akostenko.aicars.menu

import akostenko.aicars.race.Driver
import akostenko.aicars.race.cartest.AccelerationAndBreakingTest
import akostenko.aicars.race.cartest.AccelerationTest
import akostenko.aicars.race.cartest.CircularCruiseTest

import java.util.Arrays

class CarPerformanceTests : Mode() {

    override val title: String
        get() = NAME

    val drivers: List<Driver>
        get() = Arrays.asList(
                AccelerationTest(100.0),
                AccelerationTest(300.0),
                AccelerationAndBreakingTest(150.0),
                CircularCruiseTest()
        )

    companion object {
        internal val NAME = "Car tests"
    }
}
