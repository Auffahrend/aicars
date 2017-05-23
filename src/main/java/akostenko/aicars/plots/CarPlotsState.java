package akostenko.aicars.plots;

import static akostenko.aicars.Game.screenHeight;
import static akostenko.aicars.Game.screenWidth;
import static akostenko.aicars.model.CarModel.maxSteering;
import static akostenko.aicars.model.EnvironmentModel.SECONDS_PER_MINUTE;
import static akostenko.aicars.model.EnvironmentModel.g;
import static java.lang.Math.abs;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.log10;
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

import akostenko.aicars.GameSettings;
import akostenko.aicars.GameStateIds;
import akostenko.aicars.GraphicsGameState;
import akostenko.aicars.drawing.Arrow;
import akostenko.aicars.drawing.Line;
import akostenko.aicars.keyboard.SingleKeyAction;
import akostenko.aicars.math.Decart;
import akostenko.aicars.math.MathUtils;
import akostenko.aicars.math.Polar;
import akostenko.aicars.menu.TrackMenu;
import akostenko.aicars.model.CarModel;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class CarPlotsState extends GraphicsGameState {

    private static final double MPS_TO_KMPH = 3.6;
    private static final float marginPx = screenWidth * 0.02f;
    private static final int textSize = 14;
    private static final int headerTextSize = 18;
    private static final int gridStepPx = 100;
    private final TrueTypeFont font = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.PLAIN, textSize), true);
    private final TrueTypeFont headerFont = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.BOLD, headerTextSize), true);
    private final Color gray = new Color(150, 150, 150);
    private final Color darkGray = new Color(50, 50, 50);
    private final Color white = Color.white;

    private Collection<KeyListener> listeners = new ArrayList<>();
    private List<Plot> plots;
    private int currentPlot = 0;
    private boolean showZeroY = false;
    private SettableCar<?> car;

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

        plots = Arrays.asList(
                getFrontSteeringForcePlotForSpeed(50, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(100, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(150, plotWidthPx),
                getFrontSteeringForcePlotForSpeed(200, plotWidthPx),
                getRearSteeringForcePlotForSpeed(50, plotWidthPx),
                getRearSteeringForcePlotForSpeed(100, plotWidthPx),
                getRearSteeringForcePlotForSpeed(150, plotWidthPx),
                getRearSteeringForcePlotForSpeed(200, plotWidthPx),
                new Plot("Torque vs RPM", "RPM", "Torque, N*m", CarModel.min_rpm-100, CarModel.max_rpm+1000,
                        rpm -> car.getTorque(rpm/ SECONDS_PER_MINUTE), plotWidthPx, 0, 0),
                new Plot("RPM for speed", "Speed, kmh", "RPM", 0, 340,
                        kmh -> car.setVelocity(new Polar(kmh/ MPS_TO_KMPH, 0)).getRps()* SECONDS_PER_MINUTE, plotWidthPx, 0, 1),
                new Plot("Downforce for speed", "Speed, kmh", "Downforce, g", 0, 340,
                        kmh -> car.setVelocity(new Polar(kmh/ MPS_TO_KMPH, 0)).getDownforceA()/g, plotWidthPx, 0, 1)
        );
        currentPlot = 0;
        car = new SettableCar<>(new EmptyDriver(), GameSettings.get().getTrack());
        resetPlot();
    }

    private Plot getFrontSteeringForcePlotForSpeed(int speed, float plotWidthPx) {
        return new Plot("Front turning forces @ "+ speed + " km/h", "Steering, rad", "Force, g", -maxSteering, maxSteering,
                steering -> car.setVelocity(new Polar(speed/ MPS_TO_KMPH, 0))
                        .setSteering(steering)
                        .getFrontTurningForceA() / g, plotWidthPx, 2, 2);
    }

    private Plot getRearSteeringForcePlotForSpeed(int speed, float plotWidthPx) {
        return new Plot("Rear turning forces @ " + speed + " km/h", "Steering, rad", "Force, g", -maxSteering, maxSteering,
                steering -> car.setVelocity(new Polar(speed/ MPS_TO_KMPH, steering))
                        .getRearTurningForceA() / g, plotWidthPx, 2, 2);
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
        plots.get(currentPlot).reset();
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
        drawGrid(g, plot.name(),
                plot.xAxis(), plot.from(), plot.to(),
                plot.yAxis(), showZeroY ? min(0, plot.minY()) : plot.minY(), showZeroY ? max(0, plot.maxY()) : plot.maxY(),
                plot.xPrecision(), plot.yPrecision());
        drawPlotData(g, plot.getPlotData(), plot.from(), plot.to(),
                showZeroY ? min(0, plot.minY()) : plot.minY(), showZeroY ? max(0, plot.maxY()) : plot.maxY());
    }

    private void drawGrid(Graphics g, String name, String xName, double from, double to, String yName, double minY, double maxY, int xPrecision, int yPrecision) {
        drawXAxisAndGrid(g, xName, from, to, minY, maxY, xPrecision);
        drawYAxisAndGrid(g, name, from, to, yName, minY, maxY, yPrecision);
    }

    private void drawXAxisAndGrid(Graphics g, String xName, double from, double to, double minY, double maxY, int xPrecision) {
        g.setFont(font);
        float xAxisLength = screenWidth - 2 * marginPx;

        Function<Double, Double> xPxToXValue = MathUtils.linear(marginPx, from, screenWidth - marginPx, to);
        Function<Double, Double> yValueToYPx = MathUtils.linear(minY, screenHeight-marginPx, maxY, marginPx);

        // x grid
        String xValueFormat = "%." + xPrecision + 'f';
        double xStep = xPxToXValue.apply((double) gridStepPx+marginPx)-from;
        boolean dataIncludesZeroY = minY <= 0 && maxY >= 0;
        float xAxisYCoord = dataIncludesZeroY ? yValueToYPx.apply(0.).floatValue() : (screenHeight/2);
        for (int i = 0; i <= (xAxisLength-marginPx)/gridStepPx; i++) {
            float xPx = marginPx + i * gridStepPx;
            drawLine(g, new Line(
                    new Decart(xPx, marginPx),
                    new Decart(xPx, screenHeight-marginPx),
                    darkGray, 1));
            g.setColor(white);
            g.drawString(String.format(xValueFormat, from + i*xStep), xPx, xAxisYCoord + textSize/2);
        }
        // x axis
        g.setLineWidth(2);
        Arrow.get(new Decart(screenWidth/2, xAxisYCoord), xAxisLength, 0, gray, 2)
                .forEach(line -> drawLine(g, line));
        g.setColor(white);
        g.drawString(xName, screenWidth-marginPx-xName.length()*textSize/2, xAxisYCoord-textSize*3/2);
    }

    private void drawYAxisAndGrid(Graphics g, String name, double from, double to, String yName, double minY, double maxY, int yPrecision) {
        g.setFont(font);
        float yAxisLength = screenHeight - 2 * marginPx;

        Function<Double, Double> xValueToXPx = MathUtils.linear(from, marginPx, to, screenWidth - marginPx);
        Function<Double, Double> yPxToYValue = MathUtils.linear(screenHeight-marginPx, minY, marginPx, maxY);

        // y grid
        String yValueFormat = "%." + yPrecision + 'f';
        double yStep = yPxToYValue.apply((double) screenHeight-gridStepPx-marginPx)-minY;
        boolean dataIncludesZeroX = from <= 0 && to >= 0;
        float yAxisXCoord = dataIncludesZeroX ? xValueToXPx.apply(0.).floatValue() : (marginPx);
        for (int i = 0; i <= (yAxisLength-marginPx)/gridStepPx; i++) {
            float yPx = marginPx + i * gridStepPx;
            drawLine(g, new Line(
                    new Decart(marginPx, yPx),
                    new Decart(screenWidth-marginPx, yPx),
                    darkGray, 1));
            g.setColor(white);
            g.drawString(String.format(yValueFormat, maxY - i*yStep), yAxisXCoord+textSize, yPx-textSize/2);
        }
        // y axis
        Arrow.get(new Decart(yAxisXCoord, screenHeight/2), screenHeight - 2*marginPx, -PI/2, gray, 2)
                .forEach(line -> drawLine(g, line));
        g.setColor(white);
        g.drawString(yName, yAxisXCoord+(float)(abs(log10(maxY))+1+yPrecision)*textSize, marginPx-textSize/2);

        g.setFont(headerFont);
        g.drawString(name, screenWidth/2-name.length()/2*headerTextSize/3*2, marginPx/2);
    }

    private void drawPlotData(Graphics g, Iterable<Decart> plotData, double from, double to, double minY, double maxY) {
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
