package main.kotlin.akostenko.aicars.keyboard

import org.newdawn.slick.KeyListener

class ComboKeyAction(private val action: () -> Unit,
                     vararg keys: Int,
                     private val predicate: () -> Boolean = { true }) {
    private val mainTrigger: KeyListener
    private val combo: List<IsKeyDownListener>

    private fun testCombo() {
        if (predicate() && combo.all { it.isDown }) {
            action()
        }
    }

    fun listeners(): List<KeyListener> {
        return listOf(*combo.toTypedArray(), mainTrigger)
    }

    init {
        mainTrigger = SingleKeyAction({ testCombo() }, keys[0])
        combo = keys.map { IsKeyDownListener(it) }
    }
}
