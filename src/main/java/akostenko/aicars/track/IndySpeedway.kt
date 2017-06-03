package akostenko.aicars.track

class IndySpeedway : Track() {
    override val width = 16.0

    override val sections = TrackBuilder.start(0.0, -425.0, 0.0, width)
            .straight(500.0)
            .right(250.0, 90.0)
            .straight(250.0)
            .right(250.0, 90.0)
            .straight(1000.0)
            .right(250.0, 90.0)
            .straight(250.0)
            .right(250.0, 90.0)
            .straight(500.0)
            .done()

    override val title = NAME

    companion object {
        internal val NAME = "Indy speedway"
    }
}
