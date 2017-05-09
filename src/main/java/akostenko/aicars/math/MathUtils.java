package akostenko.aicars.math;

import java.util.function.Function;

public class MathUtils {
    private MathUtils() {}

    /**
     * @return linear function by given points
     */
    public static Function<Double, Double> linear(double x1, double y1, double x2, double y2) {
        if (y2 == y1) {
            return x -> y2;
        }

        if (x2 == x1) {
            throw new IllegalArgumentException("Can not interpolate by 1 point!");
        }

        // y = kx + y0
        double k = (y2 - y1) / (x2 - x1);
        double y0 = y1 - x1 * k;
        return x -> k*x + y0;
    }
}
