package akostenko.aicars.track

class DebugTrack : Track() {
    override val title = NAME
    override val width = 12.0

    override val sections = TrackBuilder.start(0.0, 0.0, 0.0, width)
            .left(50.0, 90.0)
            .straight(50.0)
            .right(50.0, 45.0)
            .done()

    companion object {
        internal val NAME = "Debug"
    }
}

