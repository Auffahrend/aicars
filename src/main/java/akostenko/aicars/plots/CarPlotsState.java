package akostenko.aicars.plots;

import static akostenko.aicars.Game.screenHeight;
import static akostenko.aicars.Game.screenWidth;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;
import static org.lwjgl.input.Keyboard.KEY_0;
import static org.lwjgl.input.Keyboard.KEY_ADD;
import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_EQUALS;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_MINUS;
import static org.lwjgl.input.Keyboard.KEY_NUMPAD0;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_SPACE;
import static org.lwjgl.input.Keyboard.KEY_SUBTRACT;
import static org.lwjgl.input.Keyboard.KEY_UP;

import akostenko.aicars.GameStateIds;
import akostenko.aicars.GraphicsGameState;
import akostenko.aicars.drawing.Arrow;
import akostenko.aicars.drawing.Line;
import akostenko.aicars.keyboard.SingleKeyAction;
import akostenko.aicars.math.Decart;
import akostenko.aicars.model.CarModel;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CarPlotsState extends GraphicsGameState {

    private final float marginPx = screenWidth * 0.02f;
    private final int textSize = 14;
    private final int gridStepPx = 100;
    private final TrueTypeFont font = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.PLAIN, textSize), true);
    private final Color gray = new Color(150, 150, 150);
    private final Color darkGray = new Color(50, 50, 50);
    private final Color white = Color.white;

    private List<KeyListener> listeners = new ArrayList<>();
    private List<Plot> plots;
    private int currentPlot = 0;
    private boolean showZeroY = false;
    private SettableCar<?> car = new SettableCar<>(new EmptyDriver());

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
        float plotWidthPx = screenWidth - 2*marginPx;
        float plotHeightPx = screenHeight - 2*marginPx;

        plots = Arrays.asList(
                new Plot("Torque vs RPM", "RPM", "Torque, N*m", CarModel.min_rpm-500, CarModel.max_rpm+1000,
                        rpm -> car.getTorque(rpm), plotWidthPx, 0, 0)
        );
        currentPlot = 0;
        resetPlot();
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        super.leave(container, game);
        listeners.forEach(listener -> container.getInput().removeKeyListener(listener));
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        container.setTargetFrameRate(30);
        listeners.add(new SingleKeyAction(v -> changePlot(-1), KEY_UP));
        listeners.add(new SingleKeyAction(v -> changePlot(+1), KEY_DOWN));
        listeners.add(new SingleKeyAction(v -> moveInterval(-1), KEY_LEFT));
        listeners.add(new SingleKeyAction(v -> moveInterval(+1), KEY_RIGHT));
        listeners.add(new SingleKeyAction(v -> zoom(+1), KEY_EQUALS));
        listeners.add(new SingleKeyAction(v -> zoom(+1), KEY_ADD));
        listeners.add(new SingleKeyAction(v -> zoom(-1), KEY_MINUS));
        listeners.add(new SingleKeyAction(v -> zoom(-1), KEY_SUBTRACT));
        listeners.add(new SingleKeyAction(v -> resetPlot(), KEY_SPACE));
        listeners.add(new SingleKeyAction(v -> toggleShowZeroY(), KEY_0));
        listeners.add(new SingleKeyAction(v -> toggleShowZeroY(), KEY_NUMPAD0));
        cameraOffset = new Decart(0, 0);
    }

    private void changePlot(int change) {
        currentPlot = (currentPlot + change + plots.size()) % plots.size();
    }

    private void moveInterval(int change) {
        plots.get(currentPlot).moveInterval(change);
    }

    private void zoom(int change) {
        plots.get(currentPlot).zoom(change);
    }

    private void toggleShowZeroY() {
        showZeroY = !showZeroY;
    }

    private void resetPlot() {
        plots.get(currentPlot).reset();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.clear();
        Plot currentPlot = plots.get(this.currentPlot);
        drawPlot(g, currentPlot);
    }

    private void drawPlot(Graphics g, Plot plot) {
        drawGrid(g,
                plot.xAxis(), plot.from(), plot.to(),
                plot.yAxis(), showZeroY ? min(0, plot.minY()) : plot.minY(), showZeroY ? max(0, plot.maxY()) : plot.maxY(),
                plot.xPrecision(), plot.yPrecision());
        drawPlotData(g, plot.getPlotData(), plot.from(), plot.to(),
                showZeroY ? min(0, plot.minY()) : plot.minY(), showZeroY ? max(0, plot.maxY()) : plot.maxY());
    }

    private void drawGrid(Graphics g, String xName, double from, double to, String yName, double minY, double maxY, int xPrecision, int yPrecision) {
        g.setFont(font);

        // x axis
        float xAxisLength = screenWidth - 2 * marginPx;
        String xValueFormat = "%." + xPrecision + "f";
        // vertical grid
        double xStep = (to - from) / ((xAxisLength - marginPx) / gridStepPx);
        for (int i = 0; i <= (xAxisLength-marginPx)/gridStepPx; i++) {
            float xPx = marginPx + i * gridStepPx;
            drawLine(g, new Line(
                    new Decart(xPx, marginPx),
                    new Decart(xPx, screenHeight-marginPx),
                    darkGray, 1));
            g.setColor(white);
            g.drawString(String.format(xValueFormat, from + i*xStep), xPx, screenHeight/2+textSize/2);
        }
        // axis
        g.setLineWidth(2);
        Arrow.get(new Decart(0.5f * screenWidth, 0.5f*screenHeight), xAxisLength, 0, gray, 2)
                .forEach(line -> drawLine(g, line));
        g.setColor(white);
        g.drawString(xName, screenWidth-2*marginPx, screenHeight/2+textSize/2);

        // y axis

        float yAxisLength = screenHeight - 2 * marginPx;
        String yValueFormat = "%." + yPrecision + "f";
        // horizontal grid
        double yStep = (maxY - minY) / ((yAxisLength - marginPx) / gridStepPx);
        for (int i = 0; i <= (yAxisLength-marginPx)/gridStepPx; i++) {
            float yPx = marginPx + i * gridStepPx;
            drawLine(g, new Line(
                    new Decart(marginPx, yPx),
                    new Decart(screenWidth-marginPx, yPx),
                    darkGray, 1));
            g.setColor(white);
            g.drawString(String.format(yValueFormat, maxY - i*yStep), marginPx+textSize, yPx-textSize/2);
        }
        // axis
        Arrow.get(new Decart(marginPx, 0.5f * screenHeight), screenHeight - 2*marginPx, -PI/2, gray, 2)
                .forEach(line -> drawLine(g, line));
        g.setColor(white);
        g.drawString(yName, 3*marginPx, marginPx-textSize/2);
    }

    private void drawPlotData(Graphics g, List<Decart> plotData, double from, double to, double minY, double maxY) {
        float xAxisLength = screenWidth - 2 * marginPx;
        float yAxisLength = screenHeight - 2 * marginPx;
        Function<Double, Float> xToScreenX = x -> (float) (marginPx + (x-from) * (xAxisLength/(to-from)));
        Function<Double, Float> yToScreenY = y -> (float) (screenHeight - marginPx - (y-minY) * (yAxisLength/(maxY-minY)));

        plotData.forEach(point -> {
            Decart screenCoordinates = new Decart(xToScreenX.apply(point.x), yToScreenY.apply(point.y));
            drawLine(g, new Line(screenCoordinates, screenCoordinates, white, 1));
        });
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

    }
}
