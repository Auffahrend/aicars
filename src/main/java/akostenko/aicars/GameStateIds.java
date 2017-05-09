package akostenko.aicars;

import akostenko.aicars.menu.MenuState;
import akostenko.aicars.race.RaceState;
import org.newdawn.slick.state.GameState;

import java.util.HashMap;
import java.util.Map;

public class GameStateIds {
    private static Map<Class<? extends GameState>, Integer> ids = new HashMap<>();

    static {
        ids.put(MenuState.class, 1);
        ids.put(RaceState.class, 2);
    }

    public static int getId(Class<? extends GameState> state) {
        return ids.get(state);
    }
}
