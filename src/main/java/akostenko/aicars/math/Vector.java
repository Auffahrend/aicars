package akostenko.aicars.math;

public interface Vector {
    double PRECISION = 0.000_000_001;
    Vector ZERO = new Decart(0,0);

    Polar toPolar();
    Decart toDecart();
    <V extends Vector> V add(V v);
    <V extends Vector> V minus(V v);
    <V extends Vector> V negative();
    <V extends Vector> V multi(double k);
    <V extends Vector> V rotate(double radians);
    double module();

}
