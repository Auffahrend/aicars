package akostenko.aicars.menu

import java.util.Collections

abstract class AbstractSubMenu<ITEM : MenuItem> : SubMenu<ITEM> {

    protected abstract fun items(): List<ITEM>

    private var current = 0

    override fun change(delta: Int) {
        if (items().size > 0) {
            current += delta + items().size
            current %= items().size
        }
    }

    override val items: List<ITEM>
        get() = Collections.unmodifiableList(items())

    override fun getCurrent(): ITEM {
        return items()[current]
    }

    override fun setCurrent(item: ITEM) {
        current = items().indexOf(item)
        if (current < 0) current = 0
    }

    override fun isCurrent(item: MenuItem): Boolean {
        return items().indexOf(item) == current
    }
}
