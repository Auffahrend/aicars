package main.kotlin.akostenko.aicars.menu

import java.util.*

class CollisionsMenu : AbstractSubMenu<CollisionsMode>() {
    override val title: String = "Collisions"

    override fun enter() {}

    override val items: List<CollisionsMode> = listOf(CollisionsOff(), CollisionsOn())
}

private class CollisionsOff : CollisionsMode() {
    override val isOn = false
    override val title: String
            get() = NAME

    companion object {
        internal val NAME = "Off"
    }
}

private class CollisionsOn : CollisionsMode() {

    override val isOn = true
    override val title: String
            get() = NAME

    companion object {
        internal val NAME = "On"
    }
}

abstract class CollisionsMode : MenuItem {

    abstract val isOn : Boolean

    override fun hashCode(): Int {
        return Objects.hash(title)
    }

    override fun equals(other: Any?): Boolean {
        return other is CollisionsMode && title == other.title
    }

    companion object {
        fun forName(name: String): CollisionsMode {
            when (name) {
                CollisionsOn.NAME -> return CollisionsOn()
                CollisionsOff.NAME -> return CollisionsOff()
                else -> return defaultMode
            }
        }

        val defaultMode: CollisionsMode = CollisionsOff()
    }
}


