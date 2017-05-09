package akostenko.aicars.drawing;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.min;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;

import org.newdawn.slick.Color;

import java.util.Arrays;
import java.util.Collection;

public class Arrow {
    private final Decart from;
    private final Decart to;

    public Arrow(Decart from, Decart to) {
        this.from = from;
        this.to = to;
    }

    public Collection<Line> render(Color color, int width) {
        Polar arrowBase = to.minus(from).toPolar();
        double finLength = min(arrowBase.r/2, width * 5);
        if (finLength < width * 2) finLength = width * 2;
        Decart finEnd1 = new Polar(finLength, arrowBase.d + 0.80*PI).plus(to).toDecart();
        Decart finEnd2 = new Polar(finLength, arrowBase.d - 0.80*PI).plus(to).toDecart();

        // shortening main line to prevent it overlapping with fins
        Decart shorterTo = new Polar(arrowBase.r - width, arrowBase.d).toDecart().plus(from);

        return Arrays.asList(
                new Line(shorterTo, from, color, width),
                new Line(to, finEnd1, color, width),
                new Line(to, finEnd2, color, width)
        );
    }
}
