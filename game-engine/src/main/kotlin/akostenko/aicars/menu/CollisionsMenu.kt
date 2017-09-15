package akostenko.aicars.menu

class CollisionsMenu : AbstractSubMenu<CollisionsMode>() {
    override val title: String = "Collisions"

    override fun enter() {}

    override val items: List<CollisionsMode> = listOf(CollisionsMode.OFF, CollisionsMode.ON)
}

enum class CollisionsMode(override val title : String, val isOn: Boolean) : MenuItem {

    ON("On", true), OFF("Off", false);

    companion object {
        fun forName(name: String): CollisionsMode {
            return CollisionsMode.values()
                    .firstOrNull { it.title == name }
                    ?: defaultMode
        }

        val defaultMode: CollisionsMode = OFF
    }
}


