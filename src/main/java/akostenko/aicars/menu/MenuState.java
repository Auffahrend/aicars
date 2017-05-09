package akostenko.aicars.menu;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RETURN;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;

import akostenko.aicars.GameStateIds;
import akostenko.aicars.Game;
import akostenko.aicars.GameSettings;
import akostenko.aicars.keyboard.KeyboardHelper;
import akostenko.aicars.keyboard.SingleKeyAction;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;

public class MenuState extends BasicGameState{

    //////////////////////////////////////////
    //   |     |       20%              |   //
    //---+------------------------------+---//
    //   |SubM1  item1  item2           |   //
    //   |                              |   //
    //20%|SubM2  item3  item4           |20%//
    //   |                              |   //
    //   |SubMN                         |   //
    //---+------------------------------+---//
    //   |             20%              |   //
    //////////////////////////////////////////


    private final StartButton startButton = new StartButton();
    private final ModeMenu modeMenu = new ModeMenu();
    private final TrackMenu trackMenu = new TrackMenu();
    private final List<SubMenu<?>> menu = Arrays.asList(startButton, modeMenu, trackMenu);

    private int currentMenu = 0;

    private final int lineWidth = 3;
    private final Color fontColor = new Color(220, 200, 50);
    private final float leftMargin = Game.WIDTH * 0.2f;
    private final float rightMargin = Game.WIDTH * 0.2f;
    private final float topMargin = Game.HEIGHT * 0.2f;
    private float bottomMargin = Game.HEIGHT * 0.2f;
    private final int subMenuHeight = 36;
    private final int subMenuItemHeight = 24;
    private final float subMenuWidth = Game.WIDTH * 0.2f;
    private final TrueTypeFont submenuFont = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.BOLD, subMenuHeight), true);
    private final TrueTypeFont itemFont = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.BOLD, subMenuItemHeight), true);

    @Override
    public int getID() {
        return GameStateIds.getId(this.getClass());
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        Input input = container.getInput();
        GameSettings.get().getGlobalListeners().forEach(input::addKeyListener);
        KeyboardHelper.getKeyListeners().forEach(input::addKeyListener);
        input.addKeyListener(new SingleKeyAction(v -> menuChange(-1), KEY_UP));
        input.addKeyListener(new SingleKeyAction(v -> menuChange(+1), KEY_DOWN));

        input.addKeyListener(new SingleKeyAction(v -> selectionChange(-1), KEY_LEFT));
        input.addKeyListener(new SingleKeyAction(v -> selectionChange(+1), KEY_RIGHT));

        input.addKeyListener(new SingleKeyAction(v -> enterMenu(), KEY_RETURN));

        container.setTargetFrameRate(60);
    }

    private void menuChange(int delta) {
        currentMenu +=delta + menu.size();
        currentMenu %= menu.size();
    }

    private void selectionChange(int delta) {
        menu.get(currentMenu).change(delta);
    }

    private void enterMenu() {
        menu.get(currentMenu).enter();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setLineWidth(lineWidth);
        g.setColor(fontColor);

        menu.forEach(submenu -> {
            float currentY = topMargin + menu.indexOf(submenu) * (subMenuHeight + 3 * lineWidth);
            drawSubMenu(g, submenu, currentY);

            drawSubmenuItems(g, submenu, currentY);
        });
    }

    private void drawSubMenu(Graphics g, SubMenu<?> submenu, float currentY) {
        g.setFont(submenuFont);
        g.drawString(submenu.getTitle(), leftMargin, currentY);
        if (currentMenu == menu.indexOf(submenu)) {
            g.drawLine(leftMargin, currentY + subMenuHeight + lineWidth, leftMargin + subMenuWidth - 5*lineWidth, currentY + subMenuHeight + lineWidth);
        }
    }

    private void drawSubmenuItems(Graphics g, SubMenu<?> submenu, float currentY) {
        g.setFont(itemFont);
        float itemY = currentY + subMenuHeight - subMenuItemHeight;
        List<? extends MenuItem> items = submenu.getItems();
        float itemWidth = (Game.WIDTH - leftMargin - rightMargin - subMenuWidth) / items.size();
        items.forEach(item -> {
            float currentX = leftMargin + subMenuWidth + submenu.getItems().indexOf(item) * itemWidth;
            g.drawString(item.getTitle(), currentX, itemY);
            if (submenu.isCurrent(item)) {
                g.drawLine(currentX, itemY + subMenuItemHeight + lineWidth, currentX + itemWidth - 5*lineWidth, itemY + subMenuItemHeight + lineWidth);
            }
        });
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        super.leave(container, game);
        GameSettings.get()
                .setTrack(trackMenu.getCurrent())
                .setMode(modeMenu.getCurrent())
                .save();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        trackMenu.setCurrent(GameSettings.get().getTrack());
        modeMenu.setCurrent(GameSettings.get().getMode());
    }


}
