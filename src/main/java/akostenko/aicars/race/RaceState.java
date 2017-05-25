package akostenko.aicars.race;

import static akostenko.aicars.race.car.CarTelemetry.accelerationColor;
import static akostenko.aicars.race.car.CarTelemetry.breakingColor;
import static akostenko.aicars.race.car.CarTelemetry.textColor;
import static java.lang.Math.PI;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;

import akostenko.aicars.Game;
import akostenko.aicars.GameSettings;
import akostenko.aicars.GameStateIds;
import akostenko.aicars.GraphicsGameState;
import akostenko.aicars.drawing.Arrow;
import akostenko.aicars.drawing.CarImg;
import akostenko.aicars.drawing.Line;
import akostenko.aicars.drawing.Scale;
import akostenko.aicars.drawing.TrackSectionImg;
import akostenko.aicars.keyboard.IsKeyDownListener;
import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Vector;
import akostenko.aicars.menu.CarPerformanceTests;
import akostenko.aicars.menu.WithPlayer;
import akostenko.aicars.race.car.Car;
import akostenko.aicars.race.car.CarTelemetry;
import akostenko.aicars.race.car.CarTelemetryScalar;
import akostenko.aicars.race.car.CarTelemetryVector;
import akostenko.aicars.track.Track;
import akostenko.aicars.track.TrackSection;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class RaceState extends GraphicsGameState {

    private Collection<Car<?>> cars;
    private Car<Player> playerCar;
    private Track track;
    private int msSinceLastCollisionDetection = 0;
    private int msSinceLastCarUpdates = 0;

    private final int msBetweenCollisionDetections = 1;
    private final int msBetweenCarUpdates = 10;
    private final Collection<KeyListener> listeners = new ArrayList<>();
    private final IsKeyDownListener accelerateListener = new IsKeyDownListener(KEY_UP);
    private final IsKeyDownListener brakeListener = new IsKeyDownListener(KEY_DOWN);
    private final IsKeyDownListener turnLeftListener = new IsKeyDownListener(KEY_LEFT);
    private final IsKeyDownListener turnRightListener = new IsKeyDownListener(KEY_RIGHT);
    private final int telemetryTextSize = 14;
    private final int lineWidth = 3;
    private final int fatLineWidth = 5;
    private final TrueTypeFont telemetryFont = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.BOLD, telemetryTextSize), true);
    private final Scale scale = new Scale(1, 20);
    private final Color trackBorder = new Color(100, 100, 100);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public int getID() {
        return GameStateIds.getId(this.getClass());
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        listeners.forEach(listener -> container.getInput().addKeyListener(listener));
        reset();
    }

    private void reset() {
        cars = new ArrayList<>();
        playerCar = null;
        GameSettings settings = GameSettings.get();
        track = settings.getTrack();
        if (settings.getMode() instanceof WithPlayer) {
            playerCar = new Car<>(new Player(), track);
            cars.add(playerCar);
        } else if (settings.getMode() instanceof CarPerformanceTests) {
            ((CarPerformanceTests) settings.getMode()).getDrivers()
                    .forEach(driver -> cars.add(new Car<>(driver, track)));
        }

        TrackSection trackStart = track.sections().get(0);
        cars.forEach(car -> car.turn(trackStart.heading()).move(trackStart.start()));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        listeners.add(accelerateListener);
        listeners.add(brakeListener);
        listeners.add(turnLeftListener);
        listeners.add(turnRightListener);

        container.setTargetFrameRate(100);
        cameraOffset = new Decart(Game.screenWidth /2, Game.screenHeight /2);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        super.leave(container, game);
        listeners.forEach(listener -> container.getInput().removeKeyListener(listener));
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Car<?> focused = getFocusedCar();

        drawTrack(g, focused, track);
        cars.forEach(car -> drawCar(g, car, focused));
        drawCarTelemetry(g, focused);

        if (cars.size() > 1) {
            drawDriverPositions(g);
        }
    }

    private void drawCar(Graphics g, Car<?> car, Car<?> focused) {
        CarImg.get(car, focused.getPosition(), textColor, scale)
                .forEach(line -> drawLine(g, line));
    }

    private void drawTrack(Graphics g, Car<?> focused, Track track) {
        track.sections()
                .forEach(section -> drawTrackSection(g, focused, section, track.getWidth()));
    }

    private void drawTrackSection(Graphics g, Car<?> focused, TrackSection section, double width) {
        TrackSectionImg.get(section, width, scale, trackBorder, focused.getPosition())
                .forEach(line -> drawLine(g, line));
    }


    private void drawDriverPositions(Graphics g) {
        g.setColor(textColor);
        g.setLineWidth(lineWidth);
        g.setFont(telemetryFont);

        List<Car<?>> carPositions = new ArrayList<>(cars);
        carPositions.sort(comparing(car -> getPositionOnTrack(car, track),  reverseOrder()));
        IntStream.range(0, cars.size())
                .forEach(i -> g.drawString(
                        String.format("%d. %s%n", i+1, carPositions.get(i).getDriver().getName()),
                        Game.screenWidth -telemetryLeftMargin-telemetryNameWidth*1.5f,
                        telemetryTopMargin + i*(telemetryTextSize + telemetrySpacing)));
    }

    private double getPositionOnTrack(Car<?> car, Track track) {
        return car.getPosition().module();
    }

    private final float telemetryLeftMargin = 50;
    private final float telemetryTopMargin = 50;
    private final float telemetryNameWidth = 100;
    private final float telemetrySpacing = 5;
    private final float telemetryNameX = telemetryLeftMargin + telemetrySpacing;
    private final float telemetryValueX = telemetryNameX + telemetryNameWidth + telemetrySpacing;
    private void drawCarTelemetry(Graphics g, Car<?> car) {
        drawTelemetry(g, car);
        drawDriverInput(g, car.getDriver());
    }

    private void drawTelemetry(Graphics g, Car<?> car) {
        g.setFont(telemetryFont);
        g.setLineWidth(lineWidth);

        AtomicReference<Float> currentY = new AtomicReference<>(telemetryTopMargin + telemetrySpacing);

        CarTelemetry telemetry = car.getTelemetry();
        telemetry.getScalars().forEach(item -> drawTelemetryScalar(g, item, currentY));
        telemetry.getVectors().forEach(vector -> drawTelemetryVector(g, car, car.getPosition(), vector));

        g.setColor(textColor);
        g.drawRect(telemetryLeftMargin, telemetryTopMargin,
                telemetryValueX+telemetryNameWidth, currentY.get()-telemetrySpacing-telemetryTextSize);
    }

    private void drawTelemetryScalar(Graphics g, CarTelemetryScalar value, AtomicReference<Float> currentY) {
        g.setColor(value.color());
        g.drawString(value.name(), telemetryNameX, currentY.get());
        g.drawString(value.value(), telemetryValueX, currentY.get());
        currentY.accumulateAndGet(telemetryTextSize + telemetrySpacing, Float::sum);
    }

    private void drawTelemetryVector(Graphics g, Car<?> car, Decart camera, CarTelemetryVector item) {
        Decart from = car.getPosition().plus(item.appliedTo()).minus(camera).multi(scale.getPixels() / scale.getSize());
        Vector centerOffset = item.vector().multi(0.5).multi(item.scale().getPixels() / item.scale().getSize());
        Arrow.get(from.plus(centerOffset),
                (float) item.vector().module() * item.scale().getPixels() / item.scale().getSize(),
                item.vector().toPolar().d, item.color(), lineWidth)
                .forEach(line -> drawLine(g, line));
    }

    private final float arrowSize = 30; //px
    private final float arrowSpace = 3; //px
    private final Color grey = new Color(40, 40, 40);
    private final Decart arrowsBlock = new Decart(Game.screenWidth - arrowSize * 4, Game.screenHeight - arrowSize * 3);
    private final Decart upArrowCenter = arrowsBlock.plus(new Decart(arrowSize*3/2, arrowSize/2));
    private final Decart downArrowCenter = arrowsBlock.plus(new Decart(arrowSize*3/2, arrowSize*3/2));
    private final Decart leftArrowCenter = arrowsBlock.plus(new Decart(arrowSize*1/2, arrowSize*3/2));
    private final Decart rightArrowCenter = arrowsBlock.plus(new Decart(arrowSize*5/2, arrowSize*3/2));

    private void drawDriverInput(Graphics g, Driver driver) {
        Arrow.get(upArrowCenter, arrowSize-arrowSpace*2, -PI/2,
                driver.accelerating() > 0 ? accelerationColor : grey,
                driver.accelerating() > 0 ? fatLineWidth : lineWidth)
                .forEach(line -> drawUILine(g, line));
        Arrow.get(downArrowCenter, arrowSize-arrowSpace*2, PI/2,
                driver.breaking() > 0 ? breakingColor : grey,
                driver.breaking() > 0 ? fatLineWidth : lineWidth)
                .forEach(line -> drawUILine(g, line));
        Arrow.get(leftArrowCenter, arrowSize, PI,
                turnLeftListener.isDown() || driver.steering() < 0 ? textColor : grey,
                turnLeftListener.isDown() || driver.steering() < 0 ? fatLineWidth : lineWidth)
                .forEach(line -> drawUILine(g, line));
        Arrow.get(rightArrowCenter, arrowSize, 0,
                turnRightListener.isDown() || driver.steering() > 0 ? textColor : grey,
                turnRightListener.isDown() || driver.steering() > 0 ? fatLineWidth : lineWidth)
                .forEach(line -> drawUILine(g, line));
    }

    private void drawUILine(Graphics g, Line line) {
        g.setLineWidth(line.getWidth());
        g.setColor(line.getColor());
        g.drawLine(
                (float) line.getFrom().x, (float) line.getFrom().y,
                (float) line.getTo().x, (float) line.getTo().y);
    }

    private Car<?> getFocusedCar() {
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
            processInput(playerCar.getDriver(), delta);
        }

        msSinceLastCarUpdates += delta;
        msSinceLastCollisionDetection += delta;

        if (msSinceLastCarUpdates >= msBetweenCarUpdates) {
            cars.forEach(car -> car.update(msSinceLastCarUpdates));
//            executor.submit(() -> cars.forEach(car -> car.update(msSinceLastCarUpdates)));
            msSinceLastCarUpdates = 0;
        }

        if (msSinceLastCollisionDetection >= msBetweenCollisionDetections) {
//            executor.submit(() -> cars.forEach(car -> detectCollision(car, msSinceLastCollisionDetection)));
            cars.forEach(car -> detectCollision(car, msSinceLastCollisionDetection));
            msSinceLastCollisionDetection = 0;
        }
    }

    private void detectCollision(Car<?> car, int msDelta) {

    }

    private void processInput(Player player, int delta) {
        player.accelerate(accelerateListener.isDown(), delta);
        player.breaks(brakeListener.isDown(), delta);
        player.turn(turnLeftListener.isDown(), turnRightListener.isDown(), delta);
    }

}
