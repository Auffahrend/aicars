package akostenko.aicars.math

class Matrix(val n: Int, val m: Int) {
    constructor(n: Int) : this(n, n)

    val values : Array<DoubleArray> = Array(n, { _ -> DoubleArray(m, { _ -> 0.0})} )

    fun submatrix(i: Int, j: Int) : Matrix {
        if (n > 1 && m > 1) {
            val submatrix = Matrix(n - 1, m - 1)
            return submatrix
        } else return empty
    }

    companion object {
        val empty = Matrix(0, 0)
    }
}