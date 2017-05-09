package akostenko.aicars.drawing;

public class Scale {
    private final float meters;
    private final float pixels;

    public Scale(float meters, float pixels) {
        this.meters = meters;
        this.pixels = pixels;
    }

    public float getMeters() {
        return meters;
    }

    public float getPixels() {
        return pixels;
    }
}
