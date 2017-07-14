package main.kotlin.akostenko.aicars.menu

interface SubMenu<ITEM : MenuItem> {
    val title: String

    fun change(delta: Int)

    fun enter()

    val items: List<ITEM>

    var current: ITEM

    fun isCurrent(item: MenuItem): Boolean
}
