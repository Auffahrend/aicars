package akostenko.aicars.plots;

import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.StrictMath.pow;

import akostenko.aicars.math.Decart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class Plot {
    private final String name;
    private final String xAxis;
    private final String yAxis;
    private final double fromInit;
    private final double toInit;
    private final Function<Double, Double> plotFunction;
    private final float xPixels;
    private final int xPrecision;
    private final int yPrecision;

    private double from;
    private double to;
    private List<Decart> plotData = new ArrayList<>();
    private double minY, maxY;

    Plot(String name, String xAxis, String yAxis, double fromInit, double toInit, Function<Double, Double> plotFunction, float xPixels, int xPrecision, int yPrecision) {
        this.name = name;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.fromInit = fromInit;
        this.toInit = toInit;
        this.plotFunction = plotFunction;
        from = fromInit;
        to = toInit;
        this.xPixels = xPixels;
        this.xPrecision = xPrecision;
        this.yPrecision = yPrecision;
    }

    void moveInterval(int change) {
        double offset = (to - from) / 2;
        from += change * offset;
        to += change * offset;
        recalculate();
    }

    void zoom(int change) {
        double diapason = (to - from) / pow(2, change);
        double center = (from + to) / 2;
        from = center - diapason/2;
        to = center + diapason/2;
        recalculate();
    }

    void reset() {
        from = fromInit;
        to = toInit;
        recalculate();
    }

    private void recalculate() {
        double dx = (to - from) / xPixels;
        plotData.clear();
        for (double x = from; x <= to; x+=dx) {
            plotData.add(new Decart(x, plotFunction.apply(x)));
        }

        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;
        plotData.forEach(point -> {
            minY = min(minY, point.getY());
            maxY = max(maxY, point.getY());
        });
    }

    public String name() {
        return name;
    }

    public String xAxis() {
        return xAxis;
    }

    public String yAxis() {
        return yAxis;
    }

    public List<Decart> getPlotData() {
        return plotData;
    }

    public double from() {
        return from;
    }

    public double to() {
        return to;
    }

    public double minY() {
        return minY;
    }

    public double maxY() {
        return maxY;
    }

    public int xPrecision() {
        return xPrecision;
    }

    public int yPrecision() {
        return yPrecision;
    }
}
