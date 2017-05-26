package akostenko.aicars.menu

import akostenko.aicars.track.StraightTrack
import akostenko.aicars.track.Track
import akostenko.aicars.track.CircularTrack

import java.util.Arrays

class TrackMenu : AbstractSubMenu<Track>() {

    private val items = Arrays.asList(
            StraightTrack(),
            CircularTrack()
    )

    override val title: String
        get() = "Track"

    override fun enter() {}

    override fun items(): List<Track> {
        return items
    }
}
