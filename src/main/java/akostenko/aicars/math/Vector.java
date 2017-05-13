package akostenko.aicars.math;

public interface Vector {
    double PRECISION = 0.000_000_000_000_001;

    Polar toPolar();
    Decart toDecart();
    Vector plus(Vector v);
    Vector minus(Vector v);
    Vector negative();
    Vector multi(double k);
    Vector div(double k);
    Vector rotate(double radians);
    double module();
    double moduleSqr();

    /** scalar product, a * b */
    double dot(Vector v);

    /** vector product a x b
     * @return Since we are using 2D vectors, the abs(result) == a*b*sin(a^b),
     * and result > 0 if direction of turning a towards b is the same as direction of x-axis toward y-axis, and vise versa
     */
    double cross(Vector v);
}
