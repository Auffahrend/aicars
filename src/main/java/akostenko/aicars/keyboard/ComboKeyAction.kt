package akostenko.aicars.keyboard

import java.util.stream.Collectors.toList

import org.newdawn.slick.KeyListener

import java.util.ArrayList
import java.util.Arrays
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

class ComboKeyAction {
    private val thisListener: KeyListener
    private val combo: Collection<IsKeyDownListener>
    private val predicate: Predicate<Void>
    private val action: Consumer<Void>

    constructor(action: Consumer<Void>, vararg keys: Int) {
        this.action = action
        thisListener = SingleKeyAction({ v -> testCombo() }, keys[0])
        combo = Arrays.stream(keys)
                .boxed()
                .map<IsKeyDownListener>(Function<Int, IsKeyDownListener> { IsKeyDownListener(it) })
                .collect<List<IsKeyDownListener>, Any>(toList<IsKeyDownListener>())
        predicate = { v -> true }
    }

    constructor(action: Consumer<Void>, key: Int, predicate: Predicate<Void>) {
        this.action = action
        thisListener = SingleKeyAction({ v -> testCombo() }, key)
        combo = Stream.of(key)
                .map<IsKeyDownListener>(Function<Int, IsKeyDownListener> { IsKeyDownListener(it) })
                .collect<List<IsKeyDownListener>, Any>(toList<IsKeyDownListener>())
        this.predicate = predicate
    }

    private fun testCombo() {
        if (combo.stream().allMatch { it.isDown } && predicate.test(null)) {
            action.accept(null)
        }
    }

    fun listeners(): Collection<KeyListener> {
        val listeners = ArrayList<KeyListener>(combo)
        listeners.add(thisListener)
        return listeners
    }
}
