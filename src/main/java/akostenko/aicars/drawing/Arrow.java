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
        double finLengthPx = min(lengthPx/2, widthPx * 5);
        if (finLengthPx < widthPx * 2) finLengthPx = widthPx * 2;
        double finLength = finLengthPx / scale.getPixels() * scale.getSize();

        return new LinesBuilder()
                // shortening main line to prevent it overlapping with fins
                .from(start).towards(end.minus(start).toPolar().d, baseLength - widthPx/scale.getPixels()*scale.getSize())
                .from(end).towards(end.toPolar().d + finRotation, finLength)
                .from(end).towards(end.toPolar().d - finRotation, finLength)
                .build().stream()
                .map(line -> line.scale(scale))
                .map(line -> line.rotate(rotation))
                .map(line -> line.position(center, color, widthPx))
                .collect(toList());
    }
}
