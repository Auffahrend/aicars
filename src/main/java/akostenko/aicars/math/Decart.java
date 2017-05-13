package akostenko.aicars.math;

import static java.lang.Math.PI;
import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.sqrt;

public class Decart implements Vector {
    public static final Decart ZERO = new Decart(0,0);


    public final double x;
    public final double y;

    public Decart(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Polar toPolar() {
        double d;
        if (x != 0) {
            d = atan(y / x);
            if (d < 0) {
                d += PI;
            } else if (y == 0 && x < 0) {
                d += PI;
            }
            if (y < 0) {
                d += PI;
            }
        } else if (y != 0) {
            d = y > 0 ? PI/2 : 3*PI/2;
        } else {
            d = 0;
        }
        return new Polar(sqrt(x*x + y*y), d);
    }

    @Override
    public Decart toDecart() {
        return this;
    }

    @Override
    public Decart plus(Vector v) {
        return addDecart(v.toDecart());
    }

    Decart addDecart(Decart v) {
        return new Decart(x+ v.x, y + v.y);
    }

    @Override
    public Decart minus(Vector v) {
        return plus(v.negative());
    }

    @Override
    public Decart negative() {
        return multi(-1);
    }

    @Override
    public Decart multi(double k) {
        return new Decart(x * k, y * k);
    }

    @Override
    public Vector div(double k) {
        return multi(1./k);
    }

    @Override
    public Decart rotate(double radians) {
        return toPolar().rotate(radians).toDecart();
    }

    @Override
    public double module() {
        return sqrt(moduleSqr());
    }

    @Override
    public double moduleSqr() {
        return x*x + y*y;
    }

    @Override
    public double dot(Vector v) {
        return toPolar().dot(v.toPolar());
    }

    @Override
    public double cross(Vector v) {
        return toPolar().cross(v.toPolar());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        Decart decart;
        if (o instanceof Vector) {
            decart = ((Vector) o).toDecart();
        } else return false;

        return (decart.x - x < PRECISION) && (decart.y - y < PRECISION);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Decart{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
