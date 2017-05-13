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
    public double accelerating() {
        return isAccelerating ? 1 : 0;
    }

    @Override
    public double breaking() {
        return isBreaking ? 1 : 0;
    }

    @Override
    public double turning() {
        return (isTurningLeft ? -1 : 0)
                + (isTurningRight ? 1 : 0);
    }

    @Override
    public void update(double dTime) {

    }
}
