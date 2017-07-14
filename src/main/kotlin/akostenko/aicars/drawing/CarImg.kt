package akostenko.aicars.drawing

import akostenko.math.vector.Decart
import akostenko.math.vector.Decart.Companion.ZERO
import akostenko.aicars.model.CarModel.axleTrack
import akostenko.aicars.model.CarModel.frontWeightPercent
import akostenko.aicars.model.CarModel.rearWeightPercent
import akostenko.aicars.model.CarModel.tyreRadius
import akostenko.aicars.model.CarModel.wheelbase
import akostenko.aicars.race.car.Car
import akostenko.math.Line
import akostenko.math.StraightLine
import java.lang.Math.PI

object CarImg {

    private val FL_wheel = Decart(wheelbase * frontWeightPercent, axleTrack / 2)
    private val FR_wheel = Decart(wheelbase * frontWeightPercent, -axleTrack / 2)

    private val RL_wheel_p1 = Decart(-wheelbase * rearWeightPercent - tyreRadius, axleTrack / 2)
    private val RL_wheel_p2 = Decart(-wheelbase * rearWeightPercent + tyreRadius, axleTrack / 2)
    private val RR_wheel_p1 = Decart(-wheelbase * rearWeightPercent - tyreRadius, -axleTrack / 2)
    private val RR_wheel_p2 = Decart(-wheelbase * rearWeightPercent + tyreRadius, -axleTrack / 2)
    private val rearAxle_p1 = Decart(-wheelbase * rearWeightPercent, axleTrack / 2)
    private val rearAxle_p2 = Decart(-wheelbase * rearWeightPercent, -axleTrack / 2)

    private val carAxis_p1 = Decart(wheelbase * frontWeightPercent * 1.5, 0.0)
    private val carAxis_p2 = Decart(-wheelbase * rearWeightPercent, 0.0)

    fun build(car: Car<*>): Collection<Line> {

        return StraightLinesBuilder(true)
                .from(FL_wheel).towards(car.steering.toPolar().d, tyreRadius)
                .from(FL_wheel).towards(car.steering.toPolar().d + PI, tyreRadius)
                .between(FL_wheel, FR_wheel)
                .from(FR_wheel).towards(car.steering.toPolar().d, tyreRadius)
                .from(FR_wheel).towards(car.steering.toPolar().d + PI, tyreRadius)

                .between(RL_wheel_p1, RL_wheel_p2)
                .between(RR_wheel_p1, RR_wheel_p2)
                .between(rearAxle_p1, rearAxle_p2)

                .between(carAxis_p1, carAxis_p2)
                .build()
                .map { line -> line.rotate(car.heading.toPolar().d, ZERO) }
                .map { (from, to, collidable) -> StraightLine(from + car.position, to + car.position, collidable) }

    }

}
