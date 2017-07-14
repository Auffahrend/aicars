package main.kotlin.akostenko.aicars.menu

import main.kotlin.akostenko.aicars.neural.NeuralNet
import main.kotlin.akostenko.aicars.race.cartest.AccelerationAndBreakingTest
import main.kotlin.akostenko.aicars.race.cartest.AccelerationTest
import main.kotlin.akostenko.aicars.race.cartest.CircularCruiseTest
import java.util.*

class ModeMenu : AbstractSubMenu<Mode>() {
    override val title: String = "Mode"

    override fun enter() {}

    override val items: List<Mode> = listOf(WithPlayer(), NeuralNetDemo(), CarPerformanceTests(), CarPhysicsTests())
}

class WithPlayer : Mode() {
    override val title: String
        get() = NAME

    companion object {
        internal val NAME = "Player"
    }
}

class NeuralNetDemo : Mode() {
    override val title: String
        get() = NAME

    val drivers = NeuralNet.bestDrivers()

    companion object {
        internal val NAME = "Neural"
    }
}

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

class CarPhysicsTests : Mode() {

    override val title: String = NAME

    companion object {

        internal val NAME = "Plots"
    }
}

abstract class Mode : MenuItem {

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(other: Any?): Boolean {
        return other is Mode && title == other.title
    }

    companion object {
        fun forName(name: String): Mode {
            when (name) {
                WithPlayer.NAME -> return WithPlayer()
                NeuralNetDemo.NAME -> return NeuralNetDemo()
                CarPerformanceTests.NAME -> return CarPerformanceTests()
                CarPhysicsTests.NAME -> return CarPhysicsTests()
                else -> return defaultMode()
            }
        }

        private fun defaultMode(): Mode = WithPlayer()
    }
}