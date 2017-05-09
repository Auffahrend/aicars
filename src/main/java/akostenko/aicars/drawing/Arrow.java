package akostenko.aicars.drawing;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.min;

import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Decart;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Arrow {
    private final Decart from;
    private final Decart to;

    public Arrow(Decart from, Decart to) {
        this.from = from;
        this.to = to;
    }

    public void draw(Graphics g, Color color, int width) {
        g.setColor(color);
        g.setLineWidth(width);

        Polar arrowBase = to.minus(from).toPolar();
        double finLength = min(arrowBase.r/2, width * 5);
        if (finLength < width * 2) finLength = width * 2;
        Decart finEnd1 = new Polar(finLength, arrowBase.d + 0.80*PI).plus(to);
        Decart finEnd2 = new Polar(finLength, arrowBase.d - 0.80*PI).plus(to);

        // shortening main line to prevent it overlapping with fins
        Decart shorterTo = new Polar(arrowBase.r - width, arrowBase.d).toDecart().plus(from);

        g.drawLine((float) shorterTo.x, (float) shorterTo.y, (float) from.x, (float) from.y);
        g.drawLine((float) to.x, (float) to.y, (float) finEnd1.x, (float) finEnd1.y);
        g.drawLine((float) to.x, (float) to.y, (float) finEnd2.x, (float) finEnd2.y);
    }
}
