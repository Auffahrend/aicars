package akostenko.aicars.menu;

import java.util.Objects;

public abstract class Mode implements MenuItem {
    public static Mode forName(String name) {
        switch (name) {
            case WithPlayer.NAME: return new WithPlayer();
            case CarPerformanceTests.NAME: return new CarPerformanceTests();
            case CarPhysicsTests.NAME: return new CarPhysicsTests();
            default: return defaultMode();
        }
    }

    private static Mode defaultMode() {
        return new WithPlayer();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Mode && Objects.equals(getTitle(), ((Mode) obj).getTitle());
    }
}
