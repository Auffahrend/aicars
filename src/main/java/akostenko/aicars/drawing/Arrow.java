package akostenko.aicars.drawing;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.min;
import static java.util.stream.Collectors.toList;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;
import org.newdawn.slick.Color;

import java.util.Collection;

public class Arrow {
    private static final Vector start = new Polar(1, PI);
    private static final Vector end = new Polar(1, 0);
    private static final double finRotation = 0.8*PI;

    public static Collection<Line> get(Decart center, float lengthPx, double rotation, Color color, int widthPx) {
        float baseLength = (float) start.minus(end).module();
        Scale scale = new Scale(baseLength, lengthPx);
        Vector heading = new Polar(1, rotation);
        double finLength = min(lengthPx/2, widthPx * 5);
        if (finLength < widthPx * 2) finLength = widthPx * 2;

        return new LinesBuilder()
                // shortening main line to prevent it overlapping with fins
                .from(start).to(baseLength - widthPx/scale.getPixels()*scale.getMeters(), end.toPolar().d)
                .from(end).to(finLength, finRotation)
                .from(end).to(finLength, -finRotation)
                .build().stream()
                .map(line -> line.scale(scale))
                .map(line -> line.rotate(heading))
                .map(line -> line.position(center, color))
                .collect(toList());
    }
}
