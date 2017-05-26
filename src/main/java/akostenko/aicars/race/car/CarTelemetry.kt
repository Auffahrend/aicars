package akostenko.aicars.race.car

import org.newdawn.slick.Color
import java.util.*

class CarTelemetry(val car: Car<*>) {
    val scalars: List<CarTelemetryScalar> = ArrayList()
    val vectors: List<CarTelemetryVector> = ArrayList()

    companion object {
        val breakingColor = Color(250, 70, 70)
        val velocityColor = Color(250, 250, 50)
        val accelerationColor = Color(50, 250, 50)
        val textColor = Color(240, 240, 240)
        val turningColor = Color(50, 50, 250)
    }
}
