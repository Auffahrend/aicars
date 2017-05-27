package akostenko.aicars.race.car

import akostenko.aicars.drawing.Scale
import akostenko.aicars.math.Decart.Companion.ZERO
import akostenko.aicars.math.Vector
import org.newdawn.slick.Color

data class CarTelemetryVector(
        /** point on car, where this vector is applied;
         * CG by default
         */
        val appliedTo: Vector,
        val vector: Vector,
        val scale: Scale,
        val color: Color) {

    constructor(vector: Vector, scale: Scale, color: Color) : this(ZERO, vector, scale, color)
}
