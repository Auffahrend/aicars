package akostenko.aicars.track;

import java.util.List;

public class CircularTrack extends Track {

    static final String NAME = "Circular";

    private List<TrackSection> sections = TrackBuilder.start(0, 0, 0)
            .right(300, 180)
            .right(300, 180)
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
