package akostenko.math.vector

import org.junit.Assert.assertEquals
import org.junit.Test
import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.sqrt

class CartesianTest {

    @Test
    @Throws(Exception::class)
    fun toPolarTest_OnAxis() {
        val right = Cartesian(1.0, 0.0).asPolar()
        assertEquals(Polar(1.0, 0.0), right)

        val up = Cartesian(0.0, 1.0).asPolar()
        assertEquals(Polar(1.0, PI / 2), up)

        val left = Cartesian(-1.0, 0.0).asPolar()
        assertEquals(Polar(1.0, PI), left)

        val down = Cartesian(0.0, -1.0).asPolar()
        assertEquals(Polar(1.0, 3 * PI / 2), down)
    }

    @Test
    @Throws(Exception::class)
    fun toPolarTest_InQuartiles() {
        val northEast = Cartesian(1.0, 1.0).asPolar()
        assertEquals(Polar(sqrt(2.0), PI / 4), northEast)

        val northWest = Cartesian(-1.0, 1.0).asPolar()
        assertEquals(Polar(sqrt(2.0), 3 * PI / 4), northWest)

        val southWest = Cartesian(-1.0, -1.0).asPolar()
        assertEquals(Polar(sqrt(2.0), 5 * PI / 4), southWest)

        val southEast = Cartesian(1.0, -1.0).asPolar()
        assertEquals(Polar(sqrt(2.0), 7 * PI / 4), southEast)
    }

}