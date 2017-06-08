package akostenko.aicars.menu

import java.util.*

class DebugMenu : AbstractSubMenu<DebugMode>() {
    override val title: String = "Debug"

    override fun enter() {}

    override val items: List<DebugMode> = listOf(DebugOff(), DebugOn())
}

private class DebugOff : DebugMode() {
    override val isOn = false
    override val title: String
            get() = NAME

    companion object {
        internal val NAME = "Off"
    }
}

private class DebugOn : DebugMode() {
    override val isOn = true
    override val title: String
        get() = NAME

    companion object {
        internal val NAME = "On"
    }
}

abstract class DebugMode : MenuItem {

    abstract val isOn : Boolean

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(other: Any?): Boolean {
        return other is DebugMode && title == other.title
    }

    companion object {
        fun forName(name: String): DebugMode {
            when (name) {
                DebugOn.NAME -> return DebugOn()
                DebugOff.NAME -> return DebugOff()
                else -> return defaultMode
            }
        }

        val defaultMode: DebugMode = DebugOff()
    }
}


