package akostenko.aicars;

import akostenko.aicars.menu.MenuState;
import akostenko.aicars.plots.CarPlotsState;
import akostenko.aicars.race.RaceState;
import org.newdawn.slick.state.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameStateIds {
    private static final Map<Class<? extends GameState>, Integer> ids = new HashMap<>();

    static {
        ids.put(MenuState.class, 1);
        ids.put(RaceState.class, 2);
        ids.put(CarPlotsState.class, 3);
    }

    public static int getId(Class<? extends GameState> state) {
        return Optional.ofNullable(ids.get(state))
                .orElseThrow(() -> new IllegalArgumentException("ID for game state " + state.getSimpleName() + " is unknown."));
    }
}
