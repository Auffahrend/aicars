package akostenko.aicars.menu;

import java.util.Arrays;
import java.util.List;

public class ModeMenu extends AbstractSubMenu<Mode> {
    private final List<Mode> modes = Arrays.asList(
            new WithPlayer(),
            new CarPerformanceTests(),
            new CarPhysicsTests());

    @Override
    public String getTitle() {
        return "Mode";
    }

    @Override
    public void enter() {

    }

    @Override
    protected List<Mode> items() {
        return modes;
    }
}
