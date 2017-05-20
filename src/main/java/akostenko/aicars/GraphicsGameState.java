package akostenko.aicars;

import akostenko.aicars.drawing.Line;
import akostenko.aicars.math.Decart;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

public abstract class GraphicsGameState extends BasicGameState {

    protected Decart cameraOffset;

    protected void drawLine(Graphics g, Line line) {
        g.setLineWidth(line.getWidth());
        g.setColor(line.getColor());
        g.drawLine(
                (float) (line.getFrom().x + cameraOffset.x), (float) (line.getFrom().y + cameraOffset.y),
                (float) (line.getTo().x + cameraOffset.x), (float) (line.getTo().y + cameraOffset.y));
    }

}
