package akostenko.aicars.menu

class ConcurrencyMenu : AbstractSubMenu<ConcurrencyMode>() {
    override val title: String = "Debug"

    override fun enter() {}

    override val items: List<ConcurrencyMode> = listOf(ConcurrencyMode.OFF, ConcurrencyMode.ON)
}

enum class ConcurrencyMode(override val title : String, val isOn: Boolean) : MenuItem {

    ON("On", true), OFF("Off", false);

    companion object {
        fun forName(name: String): ConcurrencyMode {
            return ConcurrencyMode.values()
                    .firstOrNull { it.title == name }
                    ?: defaultMode
        }

        val defaultMode: ConcurrencyMode = ON
    }
}