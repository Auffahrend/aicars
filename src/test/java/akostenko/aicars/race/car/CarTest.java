package akostenko.aicars.race.car;

import static akostenko.aicars.math.Vector.PRECISION;
import static akostenko.aicars.model.CarModel.maxSteering;
import static akostenko.aicars.model.CarModel.peakLateralForceAngle;
import static akostenko.aicars.model.CarModel.tyreStiction;
import static java.lang.StrictMath.toRadians;
import static org.junit.Assert.assertEquals;

import akostenko.aicars.plots.EmptyDriver;
import akostenko.aicars.track.StraightTrack;

import org.junit.Test;

public class CarTest {

    @Test
    public void tyreSlipForceTest_leftPeak() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(-1*tyreStiction, car.tyreSlipForce(-peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_leftHalfPeak() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(-0.5*tyreStiction, car.tyreSlipForce(-0.5*peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_leftMiddle() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(-0.95*tyreStiction, car.tyreSlipForce(-toRadians(10)-peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_leftMax() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(-0.835*tyreStiction, car.tyreSlipForce(-maxSteering, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_straight() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(0., car.tyreSlipForce(0, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_rightPeak() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(1*tyreStiction, car.tyreSlipForce(peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_rightHalfPeak() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(0.5*tyreStiction, car.tyreSlipForce(0.5*peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_rightMiddle() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(0.95*tyreStiction, car.tyreSlipForce(+toRadians(10)+peakLateralForceAngle, 1), PRECISION);
    }

    @Test
    public void tyreSlipForceTest_rightMax() throws Exception {
        Car<?> car = new Car<>(new EmptyDriver(), new StraightTrack());
        assertEquals(0.835*tyreStiction, car.tyreSlipForce(maxSteering, 1), PRECISION);
    }

}