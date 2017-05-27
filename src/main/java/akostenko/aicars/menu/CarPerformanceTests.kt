package akostenko.aicars.menu

import akostenko.aicars.race.cartest.AccelerationAndBreakingTest
import akostenko.aicars.race.cartest.AccelerationTest
import akostenko.aicars.race.cartest.CircularCruiseTest

class CarPerformanceTests : Mode() {

    override val title: String = NAME

    val drivers = listOf(
            AccelerationTest(100.0),
            AccelerationTest(300.0),
            AccelerationAndBreakingTest(150.0),
            CircularCruiseTest()
    )

    companion object {
        internal val NAME = "Car tests"
    }
}
