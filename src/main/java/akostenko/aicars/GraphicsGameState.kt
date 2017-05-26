package akostenko.aicars

import akostenko.aicars.drawing.Line
import akostenko.aicars.math.Decart

import org.newdawn.slick.Graphics
import org.newdawn.slick.state.BasicGameState

abstract class GraphicsGameState : BasicGameState() {

    protected var cameraOffset: Decart? = null

    protected fun drawLine(g: Graphics, line: Line) {
        g.lineWidth = line.width.toFloat()
        g.color = line.color
        g.drawLine(
                (line.from.x + cameraOffset!!.x).toFloat(), (line.from.y + cameraOffset!!.y).toFloat(),
                (line.to.x + cameraOffset!!.x).toFloat(), (line.to.y + cameraOffset!!.y).toFloat())
    }

}
