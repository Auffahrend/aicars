package akostenko.aicars.math;

public interface Vector {
    double PRECISION = 0.000_000_001;

    Polar toPolar();
    Decart toDecart();
    Vector plus(Vector v);
    Vector minus(Vector v);
    Vector negative();
    Vector multi(double k);
    Vector div(double k);
    Vector rotate(double radians);
    double module();

}
