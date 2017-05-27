package akostenko.aicars.track

import java.lang.Math.PI

class StraightTrack : Track() {
    override val width = 16.0 // m

    override val sections = TrackBuilder.start(0.0, 0.0, -PI / 4, width.toDouble())
            .straight(1000.0)
            .done()

    override val title: String = NAME

    companion object {

        internal val NAME = "Straight"
    }
}
