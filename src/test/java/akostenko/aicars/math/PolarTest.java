package akostenko.aicars.math;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.sqrt;
import static org.junit.Assert.*;

import org.junit.Test;

public class PolarTest {

    @Test
    public void addPolarTest1() throws Exception {
        Polar result = new Polar(1, 0)
                .addPolar(new Polar(1, PI / 2));

        assertEquals(new Decart(1, 1), result);
    }

    @Test
    public void addPolarTest2() throws Exception {
        Polar result = new Polar(1, 0)
                .addPolar(new Polar(1, PI));
        assertEquals(new Polar(0, 0), result);
    }

    @Test
    public void addPolarTest3() throws Exception {
        Polar result = new Polar(1, PI * 7/4)
                .addPolar(new Polar(1, PI * 5/4));
        assertEquals(new Polar(sqrt(2), 3*PI/2), result);
    }

    @Test
    public void addPolarTest4() throws Exception {
        Polar result = new Polar(1, PI * 0.9)
                .addPolar(new Polar(1, 0));
        assertTrue(result.d > 0 && result.d < PI);
    }

    @Test
    public void addPolarTest5() throws Exception {
        Polar result = new Polar(1, -PI * 0.1)
                .addPolar(new Polar(1, PI));
        assertTrue(result.d > PI && result.d < 3*PI/2);
    }

    @Test
    public void addPolarTest_collinear1() throws Exception {
        Polar result = new Polar(1, PI / 4)
                .addPolar(new Polar(2, PI / 4 + PI));
        assertEquals(new Polar(1, 5 * PI / 4), result);
    }

    @Test
    public void addPolarTest_collinear2() throws Exception {
        Polar result = new Polar(2, PI / 4)
                .addPolar(new Polar(1, PI / 4 + PI));
        assertEquals(new Polar(1, PI / 4), result);
    }

    @Test
    public void addPolarTest_collinear3() throws Exception {
        Polar result = new Polar(1, PI)
                .addPolar(new Polar(2, 0));
        assertEquals(new Polar(1, 0), result);
    }

}