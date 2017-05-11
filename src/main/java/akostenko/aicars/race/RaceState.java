package akostenko.aicars.race;

import static akostenko.aicars.race.car.CarTelemetryItem.accelerationColor;
import static akostenko.aicars.race.car.CarTelemetryItem.breakingColor;
import static akostenko.aicars.race.car.CarTelemetryItem.textColor;
import static java.lang.Math.PI;
import static java.util.Comparator.comparing;
import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;

import akostenko.aicars.Game;
import akostenko.aicars.GameSettings;
import akostenko.aicars.GameStateIds;
import akostenko.aicars.drawing.Arrow;
import akostenko.aicars.drawing.CarImg;
import akostenko.aicars.drawing.Line;
import akostenko.aicars.drawing.Scale;
import akostenko.aicars.keyboard.IsKeyDownListener;
import akostenko.aicars.math.Decart;
import akostenko.aicars.menu.PerformanceTest;
import akostenko.aicars.menu.WithPlayer;
import akostenko.aicars.race.car.Car;
import akostenko.aicars.race.car.CarTelemetryItem;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class RaceState extends BasicGameState {

    private Collection<Car<?>> cars;
    private Car<Player> playerCar;

    private final IsKeyDownListener accelerateListener = new IsKeyDownListener(KEY_UP);
    private final IsKeyDownListener brakeListener = new IsKeyDownListener(KEY_DOWN);
    private final IsKeyDownListener turnLeftListener = new IsKeyDownListener(KEY_LEFT);
    private final IsKeyDownListener turnRightListener = new IsKeyDownListener(KEY_RIGHT);
    private final int telemetryTextSize = 14;
    private final int lineWidth = 3;
    private final int fatLineWidth = 5;
    private final TrueTypeFont telemetryFont = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.BOLD, telemetryTextSize), true);
    private final Scale scale = new Scale(1, 10);
    private final Decart cameraCenter = new Decart(Game.WIDTH/2, Game.HEIGHT/2);

    @Override
    public int getID() {
        return GameStateIds.getId(this.getClass());
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        reset();
    }

    private void reset() {
        cars = new ArrayList<>();
        playerCar = null;
        GameSettings settings = GameSettings.get();
        if (settings.getMode() instanceof WithPlayer) {
            playerCar = new Car<>(new Player());
            cars.add(playerCar);
        } else if (settings.getMode() instanceof PerformanceTest) {
            cars.add(new Car<>(((PerformanceTest) settings.getMode()).newDriver()));
        }
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        Input input = container.getInput();
        GameSettings.get().getGlobalListeners().forEach(input::addKeyListener);
        input.addKeyListener(accelerateListener);
        input.addKeyListener(brakeListener);
        input.addKeyListener(turnLeftListener);
        input.addKeyListener(turnRightListener);

        container.setTargetFrameRate(100);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Car focused = getFocusedCar();
        drawCarTelemetry(g, focused);

        drawCar(g, focused);
    }

    private void drawCar(Graphics g, Car focused) {
        CarImg.get(focused, cameraCenter, textColor, scale)
                .forEach(line -> drawLine(g, line));
    }

    private final float telemetryLeftMargin = 50;
    private final float telemetryTopMargin = 50;
    private final float telemetryNameWidth = 100;
    private final float telemetrySpacing = 10;
    private final float telemetryNameX = telemetryLeftMargin + telemetrySpacing;
    private final float telemetryValueX = telemetryNameX + telemetryNameWidth + telemetrySpacing;
    private void drawCarTelemetry(Graphics g, Car<?> car) {
        drawTelemetry(g, car);
        renderDriverInput(g, car.getDriver());
    }

    private void drawTelemetry(Graphics g, Car<?> car) {
        g.setFont(telemetryFont);
        g.setLineWidth(lineWidth);

        AtomicReference<Float> currentY = new AtomicReference<>(telemetryTopMargin + telemetrySpacing);

        car.getTelemetry()
                .forEach(item -> drawTelemetryItem(g, item, currentY));

        g.setColor(textColor);
        g.drawRect(telemetryLeftMargin, telemetryTopMargin,
                telemetryValueX+telemetryNameWidth, currentY.get()-telemetrySpacing-telemetryTextSize);
    }

    private void drawTelemetryItem(Graphics g, CarTelemetryItem item, AtomicReference<Float> currentY) {
        g.setColor(item.color());
        g.drawString(item.name(), telemetryNameX, currentY.get());
        g.drawString(item.value(), telemetryValueX, currentY.get());
        currentY.accumulateAndGet(telemetryTextSize + telemetrySpacing, Float::sum);
    }

    private final float arrowSize = 30; //px
    private final float arrowSpace = 3; //px
    private final Color grey = new Color(40, 40, 40);
    private final Decart arrowsBlock = new Decart(Game.WIDTH - arrowSize * 4, Game.HEIGHT - arrowSize * 3);
    private final Decart upArrowCenter = arrowsBlock.plus(new Decart(arrowSize*3/2, arrowSize/2));
    private final Decart downArrowCenter = arrowsBlock.plus(new Decart(arrowSize*3/2, arrowSize*3/2));
    private final Decart leftArrowCenter = arrowsBlock.plus(new Decart(arrowSize*1/2, arrowSize*3/2));
    private final Decart rightArrowCenter = arrowsBlock.plus(new Decart(arrowSize*5/2, arrowSize*3/2));

    private void renderDriverInput(Graphics g, Driver driver) {
        Arrow.get(upArrowCenter, arrowSize-arrowSpace*2, 0,
                driver.accelerates() ? accelerationColor : grey, driver.accelerates() ? fatLineWidth : lineWidth)
                .forEach(line -> drawLine(g, line));
        Arrow.get(leftArrowCenter, arrowSize, PI/4, driver.turnsLeft() ? textColor : grey, driver.turnsLeft() ? fatLineWidth : lineWidth)
                .forEach(line -> drawLine(g, line));
        Arrow.get(downArrowCenter, arrowSize-arrowSpace*2, PI/2,
                driver.breaks() ? breakingColor : grey, driver.breaks() ? fatLineWidth : lineWidth)
                .forEach(line -> drawLine(g, line));
        Arrow.get(rightArrowCenter, arrowSize, 3*PI/4, driver.turnsRight() ? textColor : grey, driver.turnsRight() ? fatLineWidth : lineWidth)
                .forEach(line -> drawLine(g, line));
    }

    private void drawLine(Graphics g, Line line) {
        g.setLineWidth(line.getWidth());
        g.setColor(line.getColor());
        g.drawLine(
                (float) line.getFrom().x, (float) line.getFrom().y,
                (float) line.getTo().x, (float) line.getTo().y);
    }

    private Car getFocusedCar() {
        if (playerCar != null) {
            return playerCar;
        } else {
            return cars.stream()
                    .max(comparing(Car::trackDistance))
                    .orElseThrow(() -> new IllegalStateException("No cars in game"));
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if (playerCar != null) {
            processInput(playerCar.getDriver());
        }
        cars.forEach(car -> car.update(delta));
    }

    private void processInput(Player player) {
        player.accelerate(accelerateListener.isDown());
        player.breaks(brakeListener.isDown());
        player.turnLeft(turnLeftListener.isDown());
        player.turnRight(turnRightListener.isDown());
    }

}
