package main.kotlin.akostenko.aicars.drawing

import akostenko.math.StraightLine
import akostenko.math.vector.Decart
import akostenko.math.vector.Decart.Companion.ZERO
import akostenko.math.vector.Polar
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.min

object Arrow {
    private val start = Polar(1.0, PI)
    private val end = Polar(1.0, 0.0)
    private val finRotation = 0.8 * PI

    fun build(center: Decart, lengthPx: Float, rotation: Double, widthPx: Float): Collection<StraightLine> {
        val baseLength = (start - end).module()
        val scale = Scale(baseLength, lengthPx)
        var finLengthPx = min(lengthPx / 2, (widthPx * 5))
        if (finLengthPx < widthPx * 2) {
            finLengthPx = (widthPx * 2)
        }
        val finLength = scale.from(finLengthPx)

        return StraightLinesBuilder(false)
                // shortening main line to prevent it overlapping with fins
                .from(start).towards((end - start).toPolar().d, (baseLength - scale.from(widthPx)))
                .from(end).towards(end.toPolar().d + finRotation, finLength)
                .from(end).towards(end.toPolar().d - finRotation, finLength)
                .build()
                .map { (from, to, collidable) -> StraightLine(scale.to(from).toDecart(), scale.to(to).toDecart(), collidable) }
                .map { line -> line.rotate(rotation, ZERO) }
                .map { (from, to, collidable) -> StraightLine(center + from, center + to, collidable) }
    }
}
