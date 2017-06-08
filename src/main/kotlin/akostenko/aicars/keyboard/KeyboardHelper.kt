package akostenko.aicars.keyboard

import org.lwjgl.input.Keyboard
import org.newdawn.slick.KeyListener

object KeyboardHelper {
    private val lShift = IsKeyDownListener(Keyboard.KEY_LSHIFT)
    private val rShift = IsKeyDownListener(Keyboard.KEY_RSHIFT)
    private val lCtrl = IsKeyDownListener(Keyboard.KEY_LCONTROL)
    private val rCtrl = IsKeyDownListener(Keyboard.KEY_RCONTROL)
    private val lAlt = IsKeyDownListener(Keyboard.KEY_LMENU)
    private val rAlt = IsKeyDownListener(Keyboard.KEY_RMENU)

    val listeners: List<KeyListener> = listOf(lShift, rShift, lCtrl, rCtrl, lAlt, rAlt)

    fun isShiftDown(): Boolean = lShift.isDown || rShift.isDown

    fun isCtrlDown(): Boolean = lCtrl.isDown || rCtrl.isDown

    fun isAltDown(): Boolean = lAlt.isDown || rAlt.isDown
}
