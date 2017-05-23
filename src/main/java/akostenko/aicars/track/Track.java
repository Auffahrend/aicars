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

    public TrackWayPoint getNextWayPoint(TrackWayPoint current) {
        if (current.section().wayPoints().size() > current.indexInSection() + 1) {
            return current.section().wayPoints().get(current.indexInSection() + 1);
        } else {
            int currentSectionIndex = current.section().indexOnTrack();
            if (sections().size() > currentSectionIndex + 1) {
                return sections().get(currentSectionIndex + 1).wayPoints().get(0);
            } else {
                // next lap
                return sections().get(0).wayPoints().get(0);
            }
        }
    }

    public TrackWayPoint getPreviousWayPoint(TrackWayPoint current) {
        if (current.indexInSection() > 0) {
            return current.section().wayPoints().get(current.indexInSection() - 1);
        } else {
            int currentSectionIndex = current.section().indexOnTrack();
            if (currentSectionIndex > 0) {
                TrackSection previousSection = sections().get(currentSectionIndex - 1);
                return previousSection.wayPoints().get(previousSection.wayPoints().size()-1);
            } else {
                // previous lap
                TrackSection lastSection = sections().get(sections().size() - 1);
                return lastSection.wayPoints().get(lastSection.wayPoints().size()-1);
            }
        }
    }
}
