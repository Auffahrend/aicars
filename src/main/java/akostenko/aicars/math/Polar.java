package akostenko.aicars.math;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.asin;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;

public class Polar implements Vector {
    public static final Polar ZERO = new Polar(0,0);

    /** length of vector */
    public final double r;
    /** direction, <i>radians</i>, measured from X axis towards Y axis */
    public final double d;

    public Polar(double r, double d) {
        if (r < 0) throw new IllegalArgumentException("r must be >= 0! r is " + r);
        this.r = r;
        while (d < 0) d += 2*PI;
        while (d > 2*PI) d -= 2*PI;
        this.d = d;
    }

    @Override
    public Polar toPolar() {
        return this;
    }

    @Override
    public Decart toDecart() {
        return new Decart(r * cos(d), r * sin(d));
    }

    @Override
    public Polar plus(Vector v) {
        return addPolar(v.toPolar());
    }

    Polar addPolar(Polar v) {
        double newR = sqrt(r * r + v.r * v.r - 2 * r * v.r * cos(d - v.d + PI));
        double newD = 0;
        if (newR != 0.) {
             newD = asin( sin(d - v.d + PI) * v.r / newR) - d;
        }
        return new Polar(newR, newD);
    }

    @Override
    public Polar minus(Vector v) {
        return plus(v.negative());
    }

    @Override
    public Polar negative() {
        return multi(-1);
    }

    @Override
    public Polar multi(double k) {
        if (k == 0) {
            return ZERO;
        } else {
            return new Polar(r * abs(k), d + (k > 0 ? 0 : PI));
        }
    }

    @Override
    public Polar div(double k) {
        return new Polar(r / abs(k), d + (k > 0 ? 0 : PI));
    }

    @Override
    public Polar rotate(double radians) {
        return new Polar(r, d+radians);
    }

    @Override
    public double module() {
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        Polar polar;
        if (o instanceof Vector) {
            polar = ((Vector) o).toPolar();
        } else return false;

        return (polar.r - r < PRECISION) && (polar.d - d < PRECISION);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(r);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Polar{" +
                "r=" + r +
                ", d=" + d +
                '}';
    }
}
