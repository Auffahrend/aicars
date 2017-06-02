package akostenko.aicars.drawing

import akostenko.aicars.math.Decart
import akostenko.aicars.math.Polar
import org.newdawn.slick.Color
import java.lang.StrictMath.PI
import java.lang.StrictMath.min
import java.util.stream.Collectors.toList

object Arrow {
    private val start = Polar(1.0, PI)
    private val end = Polar(1.0, 0.0)
    private val finRotation = 0.8 * PI

    fun build(center: Decart, lengthPx: Float, rotation: Double, color: Color, widthPx: Float): Collection<StraightLine> {
        val baseLength = start.minus(end).module().toFloat()
        val scale = Scale(baseLength, lengthPx)
        var finLengthPx = min(lengthPx / 2, (widthPx * 5)).toDouble()
        if (finLengthPx < widthPx * 2) {
            finLengthPx = (widthPx * 2).toDouble()
        }
        val finLength = finLengthPx / scale.pixels * scale.size

        return LinesBuilder()
                // shortening main line to prevent it overlapping with fins
                .from(start).towards(end.minus(start).toPolar().d, (baseLength - widthPx / scale.pixels * scale.size).toDouble())
                .from(end).towards(end.toPolar().d + finRotation, finLength)
                .from(end).towards(end.toPolar().d - finRotation, finLength)
                .build().stream()
                .map { line -> line.scale(scale) }
                .map { line -> line.rotate(rotation) }
                .map { line -> line.place(center, color, widthPx) }
                .collect(toList())
    }
}
