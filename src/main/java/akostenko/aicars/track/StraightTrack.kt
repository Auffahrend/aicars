package akostenko.aicars.track

import java.lang.Math.PI

class StraightTrack : Track() {
    private val width = 16 // m

    private val sections = TrackBuilder.start(0.0, 0.0, -PI / 4, width.toDouble())
            .straight(1000.0)
            .done()

    override val title: String
        get() = NAME

    override fun getWidth(): Double {
        return width.toDouble()
    }

    override fun sections(): List<TrackSection> {
        return sections
    }

    companion object {

        internal val NAME = "Straight"
    }
}
