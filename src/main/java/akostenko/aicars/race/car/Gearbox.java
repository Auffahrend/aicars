package akostenko.aicars.race.car;

import static java.lang.Math.PI;

import java.util.ArrayList;
import java.util.List;

public class Gearbox {
    private final Car car;
    private final List<Gear> gears = new ArrayList<>(8);
    private int current;

    public Gearbox(Car car) {
        this.car = car;
        gears.add(new Gear(60./3.6, 10000, car.tyreRadius));
        gears.add(new Gear(125./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(155./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(190./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(225./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(260./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(295./3.6, 12000, car.tyreRadius));
        gears.add(new Gear(330./3.6, 12000, car.tyreRadius));
    }

    void update() {
        current = chooseCurrentGear();
    }

    private int chooseCurrentGear() {
        double shaftRPS = car.speed().module() / (2*PI*car.tyreRadius);
        for (int i=0; i < gears.size(); i++) {
            double gearRPS = gears.get(i).ratio * shaftRPS;
            if (gearRPS < car.max_rpm/60) {
                return i;
            }
        }

        return gears.size() - 1; // over RPS on highest gear
    }

    /** index of gear, starting from 0 */
    int current() {
        return current;
    }

    double ratio() {
        return gears.get(current).ratio;
    }

    private static class Gear {
        private final double ratio;

        Gear(double maxSpeed, double rpm, double tyreRadius) {
            if (rpm <= 0 || maxSpeed <= 0 || tyreRadius <= 0) {
                throw new IllegalArgumentException("Must be > 0");
            }

            this.ratio = (rpm / 60) / (maxSpeed / (2*PI*tyreRadius));
        }
    }
}
