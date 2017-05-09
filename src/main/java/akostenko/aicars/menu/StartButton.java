package akostenko.aicars.menu;

import akostenko.aicars.Game;
import akostenko.aicars.GameStateIds;
import akostenko.aicars.race.RaceState;

import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;

import java.util.Collections;
import java.util.List;

public class StartButton extends AbstractSubMenu<MenuItem> {
    @Override
    public String getTitle() {
        return "START";
    }

    @Override
    public void enter() {
        Game.get().enterState(GameStateIds.getId(RaceState.class), new EmptyTransition(), new FadeInTransition());
    }

    @Override
    protected List<MenuItem> items() {
        return Collections.emptyList();
    }
}
