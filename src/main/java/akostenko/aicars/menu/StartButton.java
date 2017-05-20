package akostenko.aicars.menu;

import akostenko.aicars.Game;
import akostenko.aicars.GameSettings;
import akostenko.aicars.GameStateIds;
import akostenko.aicars.plots.CarPlotsState;
import akostenko.aicars.race.RaceState;

import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartButton extends AbstractSubMenu<MenuItem> {

    private static final Map<Mode, Class<? extends GameState>> modeGameStates = new HashMap<>();
    static {
        modeGameStates.put(new WithPlayer(), RaceState.class);
        modeGameStates.put(new CarPerformanceTests(), RaceState.class);
        modeGameStates.put(new CarPhysicsTests(), CarPlotsState.class);
    }

    @Override
    public String getTitle() {
        return "START";
    }

    @Override
    public void enter() {
        Game.get()
                .enterState(GameStateIds.getId(modeGameStates.get(GameSettings.get().getMode())),
                        new EmptyTransition(), new FadeInTransition());
    }

    @Override
    protected List<MenuItem> items() {
        return Collections.emptyList();
    }
}
