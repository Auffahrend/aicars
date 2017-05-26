package akostenko.aicars.race.car

import akostenko.aicars.math.Vector.PRECISION
import akostenko.aicars.model.CarModel.maxSteering
import akostenko.aicars.model.CarModel.peakLateralForceAngle
import akostenko.aicars.model.CarModel.tyreStiction
import java.lang.StrictMath.toRadians
import org.junit.Assert.assertEquals

import akostenko.aicars.plots.EmptyDriver
import akostenko.aicars.track.StraightTrack

import org.junit.Test

class CarTest {

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftPeak() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(-1 * Companion.getTyreStiction(), car.tyreSlipForce(-Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftHalfPeak() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(-0.5 * Companion.getTyreStiction(), car.tyreSlipForce(-0.5 * Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftMiddle() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(-0.95 * Companion.getTyreStiction(), car.tyreSlipForce(-toRadians(10.0) - Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_leftMax() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(-0.835 * Companion.getTyreStiction(), car.tyreSlipForce(-Companion.getMaxSteering(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_straight() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(0.0, car.tyreSlipForce(0, 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightPeak() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(1 * Companion.getTyreStiction(), car.tyreSlipForce(Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightHalfPeak() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(0.5 * Companion.getTyreStiction(), car.tyreSlipForce(0.5 * Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightMiddle() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(0.95 * Companion.getTyreStiction(), car.tyreSlipForce(+toRadians(10.0) + Companion.getPeakLateralForceAngle(), 1), Companion.getPRECISION())
    }

    @Test
    @Throws(Exception::class)
    fun tyreSlipForceTest_rightMax() {
        val car = Car(EmptyDriver(), StraightTrack())
        assertEquals(0.835 * Companion.getTyreStiction(), car.tyreSlipForce(Companion.getMaxSteering(), 1), Companion.getPRECISION())
    }

}