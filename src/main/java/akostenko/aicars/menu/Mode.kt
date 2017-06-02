package akostenko.aicars.menu

import java.util.Objects

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
                CarPerformanceTests.NAME -> return CarPerformanceTests()
                CarPhysicsTests.NAME -> return CarPhysicsTests()
                else -> return defaultMode()
            }
        }

        private fun defaultMode(): Mode = WithPlayer()
    }
}