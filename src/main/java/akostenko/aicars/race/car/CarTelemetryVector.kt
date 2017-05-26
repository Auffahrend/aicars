package akostenko.aicars.race.car

import akostenko.aicars.math.Decart.ZERO

import akostenko.aicars.drawing.Scale
import akostenko.aicars.math.Vector

import org.newdawn.slick.Color

class CarTelemetryVector(
        /** point on car, where this vector is applied;
         * CG by default
         */
        private val appliedTo: Vector, private val vector: Vector, private val scale: Scale, private val color: Color) {

    constructor(vector: Vector, scale: Scale, color: Color) : this(ZERO, vector, scale, color) {}

    fun appliedTo(): Vector {
        return appliedTo
    }

    fun vector(): Vector {
        return vector
    }

    fun scale(): Scale {
        return scale
    }

    fun color(): Color {
        return color
    }
}
