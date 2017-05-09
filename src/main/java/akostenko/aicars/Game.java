package akostenko.aicars;

import akostenko.aicars.menu.MenuState;
import akostenko.aicars.keyboard.GameAction;
import akostenko.aicars.race.RaceState;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends StateBasedGame {

    /** Screen width */
    public static final int WIDTH = 800;
    /** Screen height */
    public static final int HEIGHT = 600;
    private static final String GAME_NAME = "AI Cars game";

    private static Game instance;

    public Game() {
        super(GAME_NAME);
    }

    public static Game get() {
        return instance;
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MenuState());
        addState(new RaceState());
    }

    public void noticeAction(GameAction gameAction) {
        try {
            switch (gameAction) {
                case RESTART:
                    restartGame();
                    break;
                case QUIT:
                    quitGame();
                    break;
            }
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }

    private void restartGame() throws SlickException {
        getState(GameStateIds.getId(RaceState.class)).leave(getContainer(), this);
        enterState(GameStateIds.getId(MenuState.class));
    }

    private void quitGame() throws SlickException {
        getState(GameStateIds.getId(MenuState.class)).leave(getContainer(), this);
        getContainer().exit();
    }

    public static void main(String[] args) throws SlickException {
        instance = new Game();
        AppGameContainer app = new AppGameContainer(instance);
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.setForceExit(false);
        app.start();
        instance.enterState(GameStateIds.getId(MenuState.class));
    }
}
