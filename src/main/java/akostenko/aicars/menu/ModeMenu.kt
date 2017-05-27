package akostenko.aicars.menu

class ModeMenu : AbstractSubMenu<Mode>() {
    private val modes = listOf(WithPlayer(), CarPerformanceTests(), CarPhysicsTests())

    override val title: String = "Mode"

    override fun enter() {}

    override val items: List<Mode> = modes
}
