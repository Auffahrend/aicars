package akostenko.aicars.math;

import static akostenko.aicars.math.Vector.PRECISION;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.sqrt;
import static org.junit.Assert.*;

import org.junit.Test;

public class PolarTest {

    @Test
    public void dot_orthogonal() throws Exception {
        Vector a = new Decart(1, 0);
        Vector b = new Decart(0, 1);
        double result = a.dot(b);
        assertEquals(0, result, PRECISION);
    }

    @Test
    public void dot_collinear() throws Exception {
        Vector a = new Decart(2, 0);
        Vector b = new Decart(-3, 0);
        double result = a.dot(b);
        assertEquals(-6, result, PRECISION);
    }

    @Test
    public void dot_commutative() throws Exception {
        Vector a = new Polar(2, PI+PI/4);
        Vector b = new Polar(3, PI+PI/2);
        double result1 = a.dot(b);
        double result2 = b.dot(a);

        assertEquals(3*sqrt(2), result1, PRECISION);
        assertEquals(3*sqrt(2), result2, PRECISION);
    }

    @Test
    public void cross_orthogonal() throws Exception {
        Vector a = new Decart(2, 0);
        Vector b = new Decart(0, -3);
        double result = a.cross(b);
        assertEquals(-6, result, PRECISION);
    }

    @Test
    public void cross_collinear() throws Exception {
        Vector a = new Decart(2, 0);
        Vector b = new Decart(-3, 0);
        double result = a.cross(b);
        assertEquals(0, result, PRECISION);
    }

    @Test
    public void cross_antiCommutative() throws Exception {
        Vector a = new Polar(2, PI+PI/4);
        Vector b = new Polar(3, PI+PI/2);
        double result1 = a.cross(b);
        double result2 = b.cross(a);

        assertEquals(result1, -result2, PRECISION);
    }

}