package akostenko.aicars.drawing;

import akostenko.aicars.math.Decart;
import akostenko.aicars.math.Polar;
import akostenko.aicars.math.Vector;

import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class LinesBuilder {
    private List<LocalLine> lines = new ArrayList<>();

    LinesBuilder between(Vector from, Vector to) {
        lines.add(new LocalLine(from, to));
        return this;
    }

    LineFromTo from(Vector from) {
        return new LineFromTo(from);
    }

    Collection<LocalLine> build() {
        return lines;
    }

    class LineFromTo {
        private final Vector from;
        LineFromTo(Vector from) {
            this.from = from;
        }

        LinesBuilder towards(double direction, double size) {
            LinesBuilder.this.lines.add(new LocalLine(from, from.plus(new Polar(size, direction))));
            return LinesBuilder.this;
        }
    }

    /** All this lines are correct only while image is centered at (0, 0)
    /* They are incompatible with {@link Line} while are unscaled and have no color
     */
    static class LocalLine {
        private final Vector from;
        private final Vector to;

        LocalLine(Vector from, Vector to) {
            this.from = from;
            this.to = to;
        }

        LocalLine rotate(double radians) {
            return new LocalLine(from.rotate(radians), to.rotate(radians));
        }

        LocalLine scale(Scale scale) {
            float k = scale.getPixels() / scale.getSize();
            return new LocalLine(from.multi(k), to.multi(k));
        }

        Line position(Decart position, Color color, int width) {
            return new Line(from.toDecart().plus(position),
                    to.toDecart().plus(position),
                    color,
                    width);
        }
    }
}
