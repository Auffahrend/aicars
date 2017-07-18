package akostenko.aicars.race.car

import akostenko.aicars.drawing.Scale
import akostenko.math.vector.Cartesian
import akostenko.math.vector.Vector
import org.newdawn.slick.Color

class CarTelemetry(val car: Car<*>) {
    val scalars = mutableListOf<CarTelemetryScalar>()
    val vectors = mutableListOf<CarTelemetryVector>()

    companion object {
        val breakingColor = Color(250, 70, 70)
        val velocityColor = Color(250, 250, 50)
        val accelerationColor = Color(50, 250, 50)
        val textColor = Color(240, 240, 240)
        val turningColor = Color(50, 50, 250)
    }
}

data class CarTelemetryScalar
private constructor(val name: String,
                    private val value: Double,
                    private val units: String?,
                    private val precision: Int,
                    val color: Color,
                    private val textValue: String?) {

    @JvmOverloads
    constructor(name: String,
                value: Double,
                units: String,
                precision: Int = 0,
                color: Color = CarTelemetry.textColor)
            : this(name, value, units, precision, color, null)

    constructor(name: String, textValue: String)
            : this(name, 0.0, null, 0, CarTelemetry.textColor, textValue)

    fun textValue(): String = textValue ?: String.format("%." + precision + "f " + units, value)
}

data class CarTelemetryVector(
        /** point on car, where this vector is applied;
         * CG by default
         */
        val appliedTo: Vector,
        val vector: Vector,
        val scale: Scale,
        val color: Color) {

    constructor(vector: Vector, scale: Scale, color: Color) : this(Cartesian.ZERO, vector, scale, color)
}