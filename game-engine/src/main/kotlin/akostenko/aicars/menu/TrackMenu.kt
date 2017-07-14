package main.kotlin.akostenko.aicars.menu

import main.kotlin.akostenko.aicars.track.DebugTrack
import main.kotlin.akostenko.aicars.track.IndySpeedway
import main.kotlin.akostenko.aicars.track.MonzaTrack
import main.kotlin.akostenko.aicars.track.Track

class TrackMenu : AbstractSubMenu<Track>() {

    override val items = listOf(DebugTrack(), MonzaTrack(), IndySpeedway())

    override val title: String = "Track"

    override fun enter() {}
}
