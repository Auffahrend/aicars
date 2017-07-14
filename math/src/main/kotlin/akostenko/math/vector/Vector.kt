package akostenko.math.vector

interface Vector {

    fun asPolar(): Polar
    fun asCartesian(): Cartesian
    operator fun plus(v: Vector): Vector
    operator fun minus(v: Vector): Vector
    operator fun unaryMinus(): Vector
    operator fun times(k: Double): Vector
    operator fun div(k: Double): Vector
    fun rotate(radians: Double): Vector
    fun module(): Double
    fun moduleSqr(): Double

    /** scalar product, a * b  */
    fun dot(v: Vector): Double

    /** vector product a x b
     * @return Since we are using 2D vectors, the abs(result) == a*b*sin(a^b),
     * and result > 0 if direction of turning a towards b is the same as direction of x-axis toward y-axis, and vise versa
     */
    fun cross(v: Vector): Double

    companion object {
        val PRECISION = 0.000_000_000_1
    }
}
