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

}