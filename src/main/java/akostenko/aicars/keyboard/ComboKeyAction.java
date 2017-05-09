package akostenko.aicars.keyboard;

import static java.util.stream.Collectors.toList;

import org.newdawn.slick.KeyListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ComboKeyAction {
    private final KeyListener thisListener;
    private final Collection<IsKeyDownListener> combo;
    private final Predicate<Void> predicate;
    private final Consumer<Void> action;

    public ComboKeyAction(Consumer<Void> action, int... keys) {
        this.action = action;
        thisListener = new SingleKeyAction(v -> testCombo(), keys[0]);
        combo = Arrays.stream(keys)
                .boxed()
                .map(IsKeyDownListener::new)
                .collect(toList());
        predicate = v -> true;
    }

    public ComboKeyAction(Consumer<Void> action, int key, Predicate<Void> predicate) {
        this.action = action;
        thisListener = new SingleKeyAction(v -> testCombo(), key);
        combo = Stream.of(key)
                .map(IsKeyDownListener::new)
                .collect(toList());
        this.predicate = predicate;
    }

    private void testCombo() {
        if (combo.stream().allMatch(IsKeyDownListener::isDown)
                && predicate.test(null)) {
            action.accept(null);
        }
    }

    public Collection<? extends KeyListener> listeners() {
        ArrayList<KeyListener> listeners = new ArrayList<>(combo);
        listeners.add(thisListener);
        return listeners;
    }
}
