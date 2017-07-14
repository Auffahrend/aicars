package test.kotlin.akostenko.aicars.race.car

import akostenko.math.vector.Vector.Companion.PRECISION
import main.kotlin.akostenko.aicars.model.CarModel.maxSteering
import main.kotlin.akostenko.aicars.model.CarModel.peakLateralForceAngle
import main.kotlin.akostenko.aicars.model.CarModel.tyreStiction
import main.kotlin.akostenko.aicars.plots.EmptyDriver
import main.kotlin.akostenko.aicars.race.car.Car
import main.kotlin.akostenko.aicars.track.MonzaTrack
import org.junit.Assert.assertEquals
import org.junit.Test
import org.apache.commons.math3.util.FastMath.toRadians

class CarTest {

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftPeak() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(-1 * tyreStiction, car.tyreSlipForce(-peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftHalfPeak() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(-0.5 * tyreStiction, car.tyreSlipForce(-0.5 * peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftMiddle() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(-0.95 * tyreStiction, car.tyreSlipForce(-toRadians(10.0) - peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftMax() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(-0.835 * tyreStiction, car.tyreSlipForce(-maxSteering, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_straight() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(0.0, car.tyreSlipForce(0.0, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightPeak() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(1 * tyreStiction, car.tyreSlipForce(peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightHalfPeak() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(0.5 * tyreStiction, car.tyreSlipForce(0.5 * peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightMiddle() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(0.95 * tyreStiction, car.tyreSlipForce(+toRadians(10.0) + peakLateralForceAngle, 1.0), PRECISION)
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightMax() {
        val car = Car(EmptyDriver(), MonzaTrack())
        assertEquals(0.835 * tyreStiction, car.tyreSlipForce(maxSteering, 1.0), PRECISION)
    }

}