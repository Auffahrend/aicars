package akostenko.aicars.race;

public class Player extends Driver {

    @Override
    public String getName() {
        return "Player";
    }

    private boolean isAccelerating;
    private boolean isBreaking;
    private boolean isTurningLeft;
    private boolean isTurningRight;

    public void accelerate(boolean apply) {
        isAccelerating = apply;
    }

    public void breaks(boolean apply) {
        isBreaking = apply;
    }

    public void turnLeft(boolean apply) {
        isTurningLeft = apply;
    }

    public void turnRight(boolean apply) {
        isTurningRight = apply;
    }

    @Override
    public boolean accelerates() {
        return isAccelerating;
    }

    @Override
    public boolean breaks() {
        return isBreaking;
    }

    @Override
    public boolean turnsLeft() {
        return isTurningLeft;
    }

    @Override
    public boolean turnsRight() {
        return isTurningRight;
    }

    @Override
    public void update(double dTime) {

    }
}
