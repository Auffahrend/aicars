package akostenko.aicars.race.carparts;

import static akostenko.aicars.math.MathUtils.linear;
import static java.util.Comparator.comparing;

import akostenko.aicars.math.Decart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TorqueMap {
    final List<Double> rpsPoints = new ArrayList<>();
    final List<Function<Double, Double>> torqueApproximations = new ArrayList<>();

    /** points' Xs are RPS and Ys are torque. */
    public TorqueMap(Decart... points) {
        Arrays.sort(points, comparing(Decart::getX));

        if (points.length < 1) {
            throw new IllegalArgumentException("Torque map must contain at least 1 point!");
        }

        if (points.length == 1) {
            // constant torque
            addApproximation(0., (rps) -> points[0].y);
        }

        for (int i = 0; i < points.length-1; i++) {
            Decart _1 = points[i];
            Decart _2 = points[i+1];
            addApproximation(_1.x, linear(_1.x, _1.y, _2.x, _2.y));
        }
    }

    private void addApproximation(double rps, Function<Double, Double> torqueApproximation) {
        rpsPoints.add(rps);
        torqueApproximations.add(torqueApproximation);
    }

    public double get(double rps) {
        Function<Double, Double> approximation = torqueApproximations.get(0);
        if (rps < rpsPoints.get(0)) {
            approximation = torqueApproximations.get(0);
        } else if (rps > rpsPoints.get(rpsPoints.size()-1)) {
            approximation = torqueApproximations.get(torqueApproximations.size()-1);
        } else {
            for (int i = 0; i < rpsPoints.size()-1; i++) {
                if (rps >= rpsPoints.get(i) && rps < rpsPoints.get(i+1)) {
                    approximation = torqueApproximations.get(i);
                }
            }
        }

        return approximation.apply(rps);
    }
}
