package akostenko.aicars.menu

abstract class AbstractSubMenu<ITEM : MenuItem> : SubMenu<ITEM> {

    private var currentIndex = 0

    override fun change(delta: Int) {
        if (items.isNotEmpty()) {
            currentIndex += delta + items.size
            currentIndex %= items.size
        }
    }

    override var current: ITEM
        get() = items[currentIndex]
        set(value) { currentIndex = items.indexOf(value) }

    override fun isCurrent(item: MenuItem): Boolean {
        return items.indexOf(item) == currentIndex
    }
}
