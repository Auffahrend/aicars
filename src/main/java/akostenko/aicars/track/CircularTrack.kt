package akostenko.aicars.track

class CircularTrack : Track() {
    override val width = 16.0

    override val sections = TrackBuilder.start(0.0, 0.0, 0.0, width)
            .right(300.0, 180.0)
            .right(300.0, 180.0)
            .done()

    override val title = NAME

    companion object {
        internal val NAME = "Circular"
    }
}
