package akostenko.aicars.menu

import java.util.Arrays

class ModeMenu : AbstractSubMenu<Mode>() {
    private val modes = Arrays.asList(
            WithPlayer(),
            CarPerformanceTests(),
            CarPhysicsTests())

    override val title: String
        get() = "Mode"

    override fun enter() {

    }

    override fun items(): List<Mode> {
        return modes
    }
}
