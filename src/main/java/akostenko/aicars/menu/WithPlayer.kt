package akostenko.aicars.menu

class WithPlayer : Mode() {
    override val title: String
        get() = NAME

    companion object {
        internal val NAME = "Player"
    }
}
