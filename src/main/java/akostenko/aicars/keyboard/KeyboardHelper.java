package akostenko.aicars.keyboard;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.KeyListener;

import java.util.ArrayList;
import java.util.Collection;

public class KeyboardHelper {
    private static final IsKeyDownListener lShift = new IsKeyDownListener(Keyboard.KEY_LSHIFT);
    private static final IsKeyDownListener rShift = new IsKeyDownListener(Keyboard.KEY_RSHIFT);
    private static final IsKeyDownListener lCtrl = new IsKeyDownListener(Keyboard.KEY_LCONTROL);
    private static final IsKeyDownListener rCtrl = new IsKeyDownListener(Keyboard.KEY_RCONTROL);
    private static final IsKeyDownListener lAlt = new IsKeyDownListener(Keyboard.KEY_LMENU);
    private static final IsKeyDownListener rAlt = new IsKeyDownListener(Keyboard.KEY_RMENU);

    public static Iterable<KeyListener> getKeyListeners() {
        Collection<KeyListener> result = new ArrayList<>();
        result.add(lShift);
        result.add(rShift);
        result.add(lCtrl);
        result.add(rCtrl);
        result.add(lAlt);
        result.add(rAlt);
        return result;
    }

    public static boolean isShiftDown() {
        return lShift.isDown() || rShift.isDown();
    }

    public static boolean isCtrlDown() {
        return lCtrl.isDown() || rCtrl.isDown();
    }

    public static boolean isAltDown() {
        return lAlt.isDown() || rAlt.isDown();
    }
}
