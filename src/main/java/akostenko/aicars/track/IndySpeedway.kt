package akostenko.aicars.track

import java.lang.Math.PI

class IndySpeedway : Track() {
    override val width = 16.0

    override val sections = TrackBuilder.start(0.0, -425.0, PI, width)
            .straight(400.0)
            .left(350.0, 90.0)
            .straight(100.0)
            .left(350.0, 90.0)
            .straight(800.0)
            .left(350.0, 90.0)
            .straight(100.0)
            .left(350.0, 90.0)
            .straight(400.0)
            .done()

    override val title = NAME

    companion object {
        internal val NAME = "Indy speedway"
    }
}
