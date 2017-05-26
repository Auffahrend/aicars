package akostenko.aicars.track

import akostenko.aicars.math.Vector

class TrackBorder(private val from: Vector, private val to: Vector) {

    fun from(): Vector {
        return from
    }

    fun to(): Vector {
        return to
    }
}
