package akostenko.aicars.drawing;

import akostenko.aicars.math.Decart;

import org.newdawn.slick.Color;

public class Line {
    private final Decart from, to;
    private final Color color;
    private final int width;

    public Line(Decart from, Decart to, int width) {
        this(from, to, null, width);
    }

    public Line(Decart from, Decart to, Color color, int width) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.width = width;
    }

    public Decart getFrom() {
        return from;
    }

    public Decart getTo() {
        return to;
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }
}
