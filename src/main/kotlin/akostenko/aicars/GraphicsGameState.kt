package akostenko.aicars

import akostenko.aicars.drawing.ArcLine
import akostenko.aicars.drawing.Line
import akostenko.aicars.drawing.Scale
import akostenko.aicars.drawing.StraightLine
import akostenko.aicars.math.Decart
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.state.BasicGameState
import java.lang.IllegalArgumentException
import java.lang.Math.max
import java.lang.Math.min
import java.lang.Math.toDegrees

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
