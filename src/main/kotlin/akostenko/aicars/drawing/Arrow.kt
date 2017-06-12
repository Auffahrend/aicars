package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Decart.Companion.ZERO
import akostenko.aicars.math.Polar
import org.newdawn.slick.Color
import java.lang.StrictMath.PI
import java.lang.StrictMath.min

object Arrow {
    private val start = Polar(1.0, PI)
    private val end = Polar(1.0, 0.0)
    private val finRotation = 0.8 * PI

    fun build(center: Decart, lengthPx: Float, rotation: Double, color: Color, widthPx: Float): Collection<StraightLine> {
        val baseLength = (start-end).module()
        val scale = Scale(baseLength, lengthPx)
        var finLengthPx = min(lengthPx / 2, (widthPx * 5))
        if (finLengthPx < widthPx * 2) {
            finLengthPx = (widthPx * 2)
        }
        val finLength = scale.from(finLengthPx)

        return StraightLinesBuilder(color, widthPx)
                // shortening main line to prevent it overlapping with fins
                .from(start).towards((end-start).toPolar().d, (baseLength - scale.from(widthPx)))
                .from(end).towards(end.toPolar().d + finRotation, finLength)
                .from(end).towards(end.toPolar().d - finRotation, finLength)
                .build()
                .map { (from, to, c, w) -> StraightLine(scale.to(from).toDecart(), scale.to(to).toDecart(), c, w) }
                .map { line -> line.rotate(rotation, ZERO) }
                .map { (from, to, c, w) -> StraightLine(center + from, center + to, c, w)  }
    }
}
