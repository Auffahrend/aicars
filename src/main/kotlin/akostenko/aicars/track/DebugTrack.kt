package akostenko.aicars.track

class DebugTrack : Track() {
    override val title = NAME
    override val width = 25.0

    override val sections = TrackBuilder.start(0.0, 0.0, 0.0, width)
            .left(120.0, 90.0)
            .right(100.0, 180.0)
            .straight(80.0)
            .right(100.0, 180.0)
            .left(120.0, 90.0)
            .left(120.0, 90.0)
            .right(100.0, 180.0)
            .straight(80.0)
            .right(100.0, 180.0)
            .left(120.0, 90.0)
            .done()

    companion object {
        internal val NAME = "Debug"
    }
}

