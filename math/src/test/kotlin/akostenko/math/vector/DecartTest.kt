package akostenko.math.vector

import org.junit.Assert.assertEquals
import org.junit.Test
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.sqrt

class DecartTest {

    @Test
    @Throws(Exception::class)
    fun toPolarTest_OnAxis() {
        val right = Decart(1.0, 0.0).toPolar()
        assertEquals(Polar(1.0, 0.0), right)

        val up = Decart(0.0, 1.0).toPolar()
        assertEquals(Polar(1.0, PI / 2), up)

        val left = Decart(-1.0, 0.0).toPolar()
        assertEquals(Polar(1.0, PI), left)

        val down = Decart(0.0, -1.0).toPolar()
        assertEquals(Polar(1.0, 3 * PI / 2), down)
    }

    @Test
    @Throws(Exception::class)
    fun toPolarTest_InQuartiles() {
        val northEast = Decart(1.0, 1.0).toPolar()
        assertEquals(Polar(sqrt(2.0), PI / 4), northEast)

        val northWest = Decart(-1.0, 1.0).toPolar()
        assertEquals(Polar(sqrt(2.0), 3 * PI / 4), northWest)

        val southWest = Decart(-1.0, -1.0).toPolar()
        assertEquals(Polar(sqrt(2.0), 5 * PI / 4), southWest)

        val southEast = Decart(1.0, -1.0).toPolar()
        assertEquals(Polar(sqrt(2.0), 7 * PI / 4), southEast)
    }

}