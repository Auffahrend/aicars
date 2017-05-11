package akostenko.aicars.track;

import akostenko.aicars.menu.MenuItem;

import java.util.List;
import java.util.Objects;

public abstract class Track implements MenuItem {
    public static Track forName(String name) {
        switch (name) {
            case StraightTrack.NAME: return new StraightTrack();
            case CircularTrack.NAME: return new CircularTrack();
            default: return defaultTrack();
        }
    }

    private static Track defaultTrack() {
        return new StraightTrack();
    }

    public abstract double getWidth();

    public abstract List<TrackSection> sections();

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Track && Objects.equals(getTitle(), ((Track) obj).getTitle());
    }

}
