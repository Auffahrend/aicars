package akostenko.aicars.menu

import akostenko.aicars.track.CircularTrack
import akostenko.aicars.track.StraightTrack
import akostenko.aicars.track.Track

class TrackMenu : AbstractSubMenu<Track>() {

    override val items = listOf(StraightTrack(), CircularTrack())

    override val title: String = "Track"

    override fun enter() {}
}
