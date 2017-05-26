package akostenko.aicars.keyboard

import org.lwjgl.input.Keyboard
import org.newdawn.slick.KeyListener

import java.util.ArrayList

object KeyboardHelper {
    private val lShift = IsKeyDownListener(Keyboard.KEY_LSHIFT)
    private val rShift = IsKeyDownListener(Keyboard.KEY_RSHIFT)
    private val lCtrl = IsKeyDownListener(Keyboard.KEY_LCONTROL)
    private val rCtrl = IsKeyDownListener(Keyboard.KEY_RCONTROL)
    private val lAlt = IsKeyDownListener(Keyboard.KEY_LMENU)
    private val rAlt = IsKeyDownListener(Keyboard.KEY_RMENU)

    val keyListeners: Iterable<KeyListener>
        get() {
            val result = ArrayList<KeyListener>()
            result.add(lShift)
            result.add(rShift)
            result.add(lCtrl)
            result.add(rCtrl)
            result.add(lAlt)
            result.add(rAlt)
            return result
        }

    val isShiftDown: Boolean
        get() = lShift.isDown || rShift.isDown

    val isCtrlDown: Boolean
        get() = lCtrl.isDown || rCtrl.isDown

    val isAltDown: Boolean
        get() = lAlt.isDown || rAlt.isDown
}
