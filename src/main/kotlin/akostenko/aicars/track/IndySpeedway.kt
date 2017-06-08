package akostenko.aicars.track

class IndySpeedway : Track() {
    override val width = 16.0

    override val sections = TrackBuilder.start(0.0, -425.0, 0.0, width)
            .straight(503.0)
            .right(256.0, 90.0)
            .straight(201.0)
            .right(256.0, 90.0)
            .straight(1006.0)
            .right(256.0, 90.0)
            .straight(201.0)
            .right(256.0, 90.0)
            .straight(503.0)
            .done()

    override val title = NAME

    companion object {
        internal val NAME = "Indy speedway"
    }
}
