package akostenko.aicars.race.car;

import org.newdawn.slick.Color;

public class CarTelemetryScalar {

    public static final Color breakingColor = new Color(250, 70, 70);
    public static final Color velocityColor = new Color(250, 250, 50);
    public static final Color accelerationColor = new Color(50, 250, 50);
    public static final Color textColor = new Color(240, 240, 240);

    private final Color color;
    private final String name;
    private final int precision;
    private final String units;
    private final double value;
    private final String textValue;


    private CarTelemetryScalar(String name, double value, String units, int precision, Color color, String textValue) {
        this.color = color;
        this.name = name;
        this.precision = precision;
        this.units = units;
        this.value = value;
        this.textValue = textValue;
    }

    public CarTelemetryScalar(String name, double value, String units, int precision, Color color) {
        this(name, value, units, precision, color, null);
    }

    public CarTelemetryScalar(String name, double value, String units) {
        this(name, value, units, 0, textColor);
    }

    public CarTelemetryScalar(String name, String textValue) {
        this(name, 0, null, 0, textColor, textValue);
    }

    public Color color() {
        return color;
    }

    public String name() {
        return name;
    }

    public String value() {
        return textValue == null ? String.format("%." + precision + "f " + units, value) : textValue;
    }
}
