package akostenko.aicars.keyboard;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsKeyDownListener implements KeyListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int key;
    private boolean isDown = false;

    public IsKeyDownListener(int key) {
        this.key = key;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (this.key == key) {
            isDown = true;
            logger.debug("Key is down {}", key);
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        if (this.key == key) {
            isDown = false;
            logger.debug("Key is up {}", key);
        }
    }

    @Override
    public void setInput(Input input) {

    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {

    }

    @Override
    public void inputStarted() {

    }

    public boolean isDown() {
        return isDown;
    }
}
