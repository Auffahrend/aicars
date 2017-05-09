package akostenko.aicars.menu;

import akostenko.aicars.track.StraightTrack;
import akostenko.aicars.track.Track;
import akostenko.aicars.track.CircularTrack;

import java.util.Arrays;
import java.util.List;

public class TrackMenu extends AbstractSubMenu<Track> {

    private final List<Track> items = Arrays.asList(
            new StraightTrack(),
            new CircularTrack()
    );

    @Override
    public String getTitle() {
        return "Track";
    }

    @Override
    public void enter() {
    }

    @Override
    protected List<Track> items() {
        return items;
    }
}
