package akostenko.aicars.track

class CircularTrack : Track() {
    private val width = 16

    private val sections = TrackBuilder.start(0.0, 0.0, 0.0, width.toDouble())
            .right(300.0, 180.0)
            .right(300.0, 180.0)
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

        internal val NAME = "Circular"
    }
}
