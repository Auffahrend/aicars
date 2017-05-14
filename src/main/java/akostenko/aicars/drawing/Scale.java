package akostenko.aicars.drawing;

public class Scale {
    private final float size;
    private final float pixels;

    public Scale(float size, float pixels) {
        this.size = size;
        this.pixels = pixels;
    }

    public float getSize() {
        return size;
    }

    public float getPixels() {
        return pixels;
    }
}
