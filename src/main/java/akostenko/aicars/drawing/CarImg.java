package akostenko.aicars.drawing;

import static akostenko.aicars.model.CarModel.axleTrack;
import static akostenko.aicars.model.CarModel.tyreRadius;
import static akostenko.aicars.model.CarModel.wheelbase;
import static java.lang.StrictMath.PI;
import static java.util.stream.Collectors.toList;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Vector;
import akostenko.aicars.race.car.Car;

import org.newdawn.slick.Color;

import java.util.Collection;

public class CarImg {

    private static final Vector FL_wheel = new Decart(wheelbase/2, axleTrack/2);
    private static final Vector FR_wheel = new Decart(wheelbase/2, -axleTrack/2);

    private static final Vector RL_wheel_p1 = new Decart(-wheelbase/2 - tyreRadius, axleTrack/2);
    private static final Vector RL_wheel_p2 = new Decart(-wheelbase/2 + tyreRadius, axleTrack/2);
    private static final Vector RR_wheel_p1 = new Decart(-wheelbase/2 - tyreRadius, -axleTrack/2);
    private static final Vector RR_wheel_p2 = new Decart(-wheelbase/2 + tyreRadius, -axleTrack/2);
    private static final Vector rearAxle_p1 = new Decart(-wheelbase/2, axleTrack/2);
    private static final Vector rearAxle_p2 = new Decart(-wheelbase/2, -axleTrack/2);

    private static final Vector carAxis_p1 = new Decart(wheelbase/2, 0);
    private static final Vector carAxis_p2 = new Decart(-wheelbase/2, 0);

    public static Collection<Line> get(Car<?> car, Decart cameraPosition, Color color, Scale scale) {
        Decart screenPosition = car.getPosition().multi(scale.getPixels() / scale.getMeters())
                .plus(cameraPosition);


        return new LinesBuilder()
                .from(FL_wheel).towards(car.getSteering().toPolar().d, tyreRadius)
                .from(FL_wheel).towards(car.getSteering().toPolar().d+PI, tyreRadius)
                .between(FL_wheel, FR_wheel)
                .from(FR_wheel).towards(car.getSteering().toPolar().d, tyreRadius)
                .from(FR_wheel).towards(car.getSteering().toPolar().d+PI, tyreRadius)

                .between(RL_wheel_p1, RL_wheel_p2)
                .between(RR_wheel_p1, RR_wheel_p2)
                .between(rearAxle_p1, rearAxle_p2)

                .between(carAxis_p1, carAxis_p2)
                .build().stream()
                .map(line -> line.rotate(car.getHeading().toPolar().d))
                .map(line -> line.scale(scale))
                .map(line -> line.position(screenPosition, color))
                .collect(toList());
    }

}
