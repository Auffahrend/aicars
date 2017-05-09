package akostenko.aicars.math;

public interface Vector {
    double PRECISION = 0.000_000_001;

    Polar toPolar();
    Decart toDecart();
    <V extends Vector> V plus(V v);
    <V extends Vector> V minus(V v);
    <V extends Vector> V negative();
    <V extends Vector> V multi(double k);
    <V extends Vector> V div(double k);
    <V extends Vector> V rotate(double radians);
    double module();

}
