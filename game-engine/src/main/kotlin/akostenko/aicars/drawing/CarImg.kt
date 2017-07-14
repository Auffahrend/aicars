package main.kotlin.akostenko.aicars.drawing

import akostenko.math.vector.Cartesian
import akostenko.math.vector.Cartesian.Companion.ZERO
import main.kotlin.akostenko.aicars.model.CarModel.axleTrack
import main.kotlin.akostenko.aicars.model.CarModel.frontWeightPercent
import main.kotlin.akostenko.aicars.model.CarModel.rearWeightPercent
import main.kotlin.akostenko.aicars.model.CarModel.tyreRadius
import main.kotlin.akostenko.aicars.model.CarModel.wheelbase
import main.kotlin.akostenko.aicars.race.car.Car
import akostenko.math.Line
import akostenko.math.StraightLine
import org.apache.commons.math3.util.FastMath.PI

object CarImg {

    private val FL_wheel = Cartesian(wheelbase * frontWeightPercent, axleTrack / 2)
    private val FR_wheel = Cartesian(wheelbase * frontWeightPercent, -axleTrack / 2)

    private val RL_wheel_p1 = Cartesian(-wheelbase * rearWeightPercent - tyreRadius, axleTrack / 2)
    private val RL_wheel_p2 = Cartesian(-wheelbase * rearWeightPercent + tyreRadius, axleTrack / 2)
    private val RR_wheel_p1 = Cartesian(-wheelbase * rearWeightPercent - tyreRadius, -axleTrack / 2)
    private val RR_wheel_p2 = Cartesian(-wheelbase * rearWeightPercent + tyreRadius, -axleTrack / 2)
    private val rearAxle_p1 = Cartesian(-wheelbase * rearWeightPercent, axleTrack / 2)
    private val rearAxle_p2 = Cartesian(-wheelbase * rearWeightPercent, -axleTrack / 2)

    private val carAxis_p1 = Cartesian(wheelbase * frontWeightPercent * 1.5, 0.0)
    private val carAxis_p2 = Cartesian(-wheelbase * rearWeightPercent, 0.0)

    fun build(car: Car<*>): Collection<Line> {

        return StraightLinesBuilder(true)
                .from(FL_wheel).towards(car.steering.asPolar().d, tyreRadius)
                .from(FL_wheel).towards(car.steering.asPolar().d + PI, tyreRadius)
                .between(FL_wheel, FR_wheel)
                .from(FR_wheel).towards(car.steering.asPolar().d, tyreRadius)
                .from(FR_wheel).towards(car.steering.asPolar().d + PI, tyreRadius)

                .between(RL_wheel_p1, RL_wheel_p2)
                .between(RR_wheel_p1, RR_wheel_p2)
                .between(rearAxle_p1, rearAxle_p2)

                .between(carAxis_p1, carAxis_p2)
                .build()
                .map { line -> line.rotate(car.heading.asPolar().d, ZERO) }
                .map { (from, to, collidable) -> StraightLine(from + car.position, to + car.position, collidable) }

    }

}
