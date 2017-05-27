package akostenko.aicars.keyboard

import java.util.stream.Collectors.toList

import org.newdawn.slick.KeyListener

import java.util.ArrayList
import java.util.Arrays
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

class ComboKeyAction(private val action: () -> Unit,
                     vararg keys: Int,
                     private val predicate: () -> Boolean = { true }) {
    private val thisListener: KeyListener
    private val combo: List<IsKeyDownListener>

    private fun testCombo() {
        if (predicate() && combo.all { it.isDown }) {
            action()
        }
    }

    fun listeners(): Array<KeyListener> {
        return listOf(thisListener, *combo.toTypedArray()).toTypedArray()
    }

    init {
        thisListener = SingleKeyAction({ testCombo() }, keys[0])
        combo = Arrays.stream(keys)
                .boxed()
                .map({ IsKeyDownListener(it) })
                .collect(toList())
    }
}
