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
    public <V extends Vector> V plus(V v) {
        if (v instanceof Decart) {
            //noinspection unchecked
            return (V) addDecart((Decart) v);
        }
        if (v instanceof Polar) {
            //noinspection unchecked
            return (V) toPolar().addPolar((Polar) v);
        }
        throw new IllegalArgumentException("Unknown vector type");
    }

    Decart addDecart(Decart v) {
        return new Decart(x+ v.x, y + v.y);
    }

    @Override
    public <V extends Vector> V minus(V v) {
        return plus(v.negative());
    }

    @Override
    public <V extends Vector> V negative() {
        return multi(-1);
    }

    @Override
    public <V extends Vector> V multi(double k) {
        //noinspection unchecked
        return (V) new Decart(x * k, y * k);
    }

    @Override
    public <V extends Vector> V div(double k) {
        //noinspection unchecked
        return (V) new Decart(x / k, y / k);
    }

    @Override
    public <V extends Vector> V rotate(double radians) {
        //noinspection unchecked
        return (V) toPolar().rotate(radians).toDecart();
    }

    @Override
    public double module() {
        return sqrt(x*x + y*y);
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
        if (o instanceof Decart) {
            decart = (Decart) o;
        } else if (o instanceof Polar) {
            decart = ((Polar) o).toDecart();
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
