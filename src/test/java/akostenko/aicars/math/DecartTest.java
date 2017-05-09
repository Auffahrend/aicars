package akostenko.aicars.math;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.sqrt;
import static org.junit.Assert.*;

import org.junit.Test;

public class DecartTest {

    @Test
    public void toPolarTest_OnAxis() throws Exception {
        Polar right = new Decart(1, 0).toPolar();
        assertEquals(new Polar(1, 0), right);

        Polar up = new Decart(0, 1).toPolar();
        assertEquals(new Polar(1, PI/2), up);

        Polar left = new Decart(-1, 0).toPolar();
        assertEquals(new Polar(1, PI), left);

        Polar down = new Decart(0, -1).toPolar();
        assertEquals(new Polar(1, 3*PI/2), down);
    }

    @Test
    public void toPolarTest_InQuartiles() throws Exception {
        Polar northEast = new Decart(1, 1).toPolar();
        assertEquals(new Polar(sqrt(2), PI/4), northEast);

        Polar northWest = new Decart(-1, 1).toPolar();
        assertEquals(new Polar(sqrt(2), 3*PI/4), northWest);

        Polar southWest = new Decart(-1, -1).toPolar();
        assertEquals(new Polar(sqrt(2), 5*PI/4), southWest);

        Polar southEast = new Decart(1, -1).toPolar();
        assertEquals(new Polar(sqrt(2), 7*PI/4), southEast);
    }

}