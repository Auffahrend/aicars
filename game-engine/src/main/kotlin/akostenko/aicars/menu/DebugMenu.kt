package akostenko.aicars.menu

class DebugMenu : AbstractSubMenu<DebugMode>() {
    override val title: String = "Debug"

    override fun enter() {}

    override val items: List<DebugMode> = listOf(DebugMode.OFF, DebugMode.ON)
}

enum class DebugMode(override val title : String, val isOn: Boolean) : MenuItem {

    ON("On", true), OFF("Off", false);

    companion object {
        fun forName(name: String): DebugMode {
            return DebugMode.values()
                    .firstOrNull { it.title == name }
                    ?: defaultMode
        }

        val defaultMode: DebugMode = OFF
    }
}


