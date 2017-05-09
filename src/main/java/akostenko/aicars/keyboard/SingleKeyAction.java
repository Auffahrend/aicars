package akostenko.aicars.keyboard;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

import java.util.function.Consumer;

public class SingleKeyAction implements KeyListener {
    private final int key;
    private final Consumer<Void> action;

    public SingleKeyAction(Consumer<Void> action, int key) {
        this.key = key;
        this.action = action;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (this.key == key) {
            action.accept(null);
        }
    }

    @Override
    public void keyReleased(int key, char c) {

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
}
