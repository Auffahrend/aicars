package akostenko.aicars.track;

import static java.lang.Math.PI;

import java.util.List;

public class StraightTrack extends Track {

    static final String NAME = "Straight";

    private List<TrackSection> sections = TrackBuilder.start(0,0, - PI / 4)
            .straight(1000)
            .done();

    @Override
    public String getTitle() {
        return NAME;
    }

    @Override
    public double getWidth() {
        return 12;
    }

    @Override
    public List<TrackSection> sections() {
        return sections;
    }
}
