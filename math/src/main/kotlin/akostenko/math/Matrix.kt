package akostenko.math

class Matrix(val n: Int, val m: Int) {
    constructor(n: Int) : this(n, n)

    val values : Array<DoubleArray> = Array(n, { _ -> DoubleArray(m, { _ -> 0.0})} )

    companion object {
        val empty = Matrix(0, 0)
    }
}