package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.model.CarModel.axleTrack
import akostenko.aicars.model.CarModel.frontWeightPercent
import akostenko.aicars.model.CarModel.rearWeightPercent
import akostenko.aicars.model.CarModel.tyreRadius
import akostenko.aicars.model.CarModel.wheelbase
import akostenko.aicars.race.car.Car
import org.newdawn.slick.Color
import java.lang.StrictMath.PI
import java.util.stream.Collectors.toList

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

    fun build(car: Car<*>, cameraPosition: Decart, color: Color, scale: Scale): Collection<StraightLine> {
        val positionPx = scale.to(car.position - cameraPosition).toDecart()

        return LinesBuilder()
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
                .map { line -> line.rotate(car.heading.toPolar().d) }
                .map { line -> line.scale(scale) }
                .map { line -> line.place(positionPx, color, 3f) }

    }

}
