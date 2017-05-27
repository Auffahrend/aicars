package akostenko.aicars.race.car

import org.newdawn.slick.Color

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
