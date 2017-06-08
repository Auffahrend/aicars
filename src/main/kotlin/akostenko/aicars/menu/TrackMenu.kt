package akostenko.aicars.menu

import akostenko.aicars.track.DebugTrack
import akostenko.aicars.track.IndySpeedway
import akostenko.aicars.track.MonzaTrack
import akostenko.aicars.track.Track

class TrackMenu : AbstractSubMenu<Track>() {

    override val items = listOf(DebugTrack(), MonzaTrack(), IndySpeedway())

    override val title: String = "Track"

    override fun enter() {}
}
