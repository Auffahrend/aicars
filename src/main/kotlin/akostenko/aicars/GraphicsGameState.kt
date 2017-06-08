package akostenko.aicars

import akostenko.aicars.drawing.ArcLine
import akostenko.aicars.drawing.Line
import akostenko.aicars.drawing.Scale
import akostenko.aicars.drawing.StraightLine
import akostenko.aicars.math.Decart
import org.newdawn.slick.Graphics
import org.newdawn.slick.state.BasicGameState
import java.lang.IllegalArgumentException
import java.lang.Math.max
import java.lang.Math.min
import java.lang.Math.toDegrees

abstract class GraphicsGameState : BasicGameState() {

    protected lateinit var cameraOffset: Decart

    protected fun drawUILine(g: Graphics, straightLine: StraightLine) {
        g.lineWidth = straightLine.width
        g.color = straightLine.color
        g.drawLine(
                straightLine.from.x.toFloat(), straightLine.from.y.toFloat(),
                straightLine.to.x.toFloat(), straightLine.to.y.toFloat())
    }

    protected fun drawScaledLine(g: Graphics, line: Line) {
        if (line is StraightLine) drawStraightLine(g, line)
        else if (line is ArcLine) drawArc(g, line)
        else throw IllegalArgumentException("Not supported line $line")
    }

    protected fun drawUnscaledLine(g: Graphics, line: StraightLine, camera: Decart, scale: Scale) {
        g.lineWidth = line.width
        g.color = line.color
        g.drawLine(
                (scale.to(line.from.x - camera.x) + cameraOffset.x).toFloat(),
                (scale.to(line.from.y - camera.y) + cameraOffset.y).toFloat(),
                (scale.to(line.to.x - camera.x) + cameraOffset.x).toFloat(),
                (scale.to(line.to.y - camera.y) + cameraOffset.y).toFloat())
    }

    private fun drawStraightLine(g: Graphics, straightLine: StraightLine) {
        g.lineWidth = straightLine.width
        g.color = straightLine.color
        g.drawLine(
                (straightLine.from.x + cameraOffset.x).toFloat(), (straightLine.from.y + cameraOffset.y).toFloat(),
                (straightLine.to.x + cameraOffset.x).toFloat(), (straightLine.to.y + cameraOffset.y).toFloat())
    }

    private fun drawArc(g: Graphics, arcLine: ArcLine) {
        g.lineWidth = arcLine.width
        g.color = arcLine.color
        val from = min(arcLine.from, arcLine.to)
        val to = max(arcLine.from, arcLine.to)
        g.drawArc(
                (arcLine.center.x - arcLine.radius + cameraOffset.x).toFloat(),
                (arcLine.center.y - arcLine.radius + cameraOffset.y).toFloat(),
                arcLine.radius.toFloat() * 2,
                arcLine.radius.toFloat() * 2,
                //min(arcLine.radius.toInt() * 2, 150),
                toDegrees(from).toFloat(),
                toDegrees(to).toFloat()
        )
    }

}
