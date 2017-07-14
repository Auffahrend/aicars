package main.kotlin.akostenko.aicars

import akostenko.math.ArcLine
import akostenko.math.Line
import main.kotlin.akostenko.aicars.drawing.Scale
import akostenko.math.StraightLine
import akostenko.math.vector.Decart
import main.kotlin.akostenko.aicars.track.TrackMarker
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.BasicGameState
import java.awt.Font
import java.lang.IllegalArgumentException
import org.apache.commons.math3.util.FastMath.max
import org.apache.commons.math3.util.FastMath.min
import org.apache.commons.math3.util.FastMath.toDegrees

abstract class GraphicsGameState : BasicGameState() {

    protected lateinit var cameraOffset: Decart

    protected fun drawUILine(g: Graphics, straightLine: StraightLine, color: Color, width: Float) {
        g.lineWidth = width
        g.color = color
        g.drawLine(
                straightLine.from.x.toFloat(),
                straightLine.from.y.toFloat(),
                straightLine.to.x.toFloat(),
                straightLine.to.y.toFloat())
    }

    private val trackTextFontsBySize = mutableMapOf<Int, TrueTypeFont>()
    /**
     * @param size height of a character in meters
     */
    protected fun drawTrackMarker(g: Graphics, marker: TrackMarker, camera: Decart, scale: Scale, color: Color, size: Float) {

        var text = marker.text.trim()
        if (text.length > 5) text = text.substring(0..4)

        //1.5 is character's height/width
        val textOffset = Decart(-text.length.toFloat() / 2 * size / 1.5, -size / 2.0)
        g.color = color
        g.font = trackTextFontsBySize.computeIfAbsent(scale.to(size.toDouble()).toInt(),
                { TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, max(it, 1)), true) })
        g.drawString(marker.text,
                scale.to(marker.position.toDecart().x - camera.x + textOffset.x) + cameraOffset.x.toFloat(),
                scale.to(marker.position.toDecart().y - camera.y + textOffset.y) + cameraOffset.y.toFloat())
    }

    protected fun drawRealLine(g: Graphics, line: Line, camera: Decart, scale: Scale, color: Color, width: Float) {
        if (line is StraightLine) drawStraightLine(g, line, camera, scale, color, width)
        else if (line is ArcLine) drawArc(g, line, camera, scale, color, width)
        else throw IllegalArgumentException("Not supported line $line")
    }

    private fun drawStraightLine(g: Graphics, line: StraightLine, camera: Decart, scale: Scale, color: Color, width: Float) {
        g.lineWidth = width
        g.color = color
        g.drawLine(
                (scale.to(line.from.x - camera.x) + cameraOffset.x).toFloat(),
                (scale.to(line.from.y - camera.y) + cameraOffset.y).toFloat(),
                (scale.to(line.to.x - camera.x) + cameraOffset.x).toFloat(),
                (scale.to(line.to.y - camera.y) + cameraOffset.y).toFloat())
    }

    private fun drawArc(g: Graphics, arcLine: ArcLine, camera: Decart, scale: Scale, color: Color, width: Float) {
        g.lineWidth = width
        g.color = color
        val from = min(arcLine.from, arcLine.to)
        val to = max(arcLine.from, arcLine.to)
        g.drawArc(
                scale.to(arcLine.center.x - arcLine.radius - camera.x) + cameraOffset.x.toFloat(),
                scale.to(arcLine.center.y - arcLine.radius - camera.y) + cameraOffset.y.toFloat(),
                scale.to(arcLine.radius * 2),
                scale.to(arcLine.radius * 2),
                toDegrees(from).toFloat(),
                toDegrees(to).toFloat()
        )
    }

}
