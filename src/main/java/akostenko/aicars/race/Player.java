package akostenko.aicars.race;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;

public class Player extends Driver {

    private final double steeringSensitivity = 4;
    private final double accelerationSensitivity = 5;
    private final double breakingSensitivity = 10;
    private final double fullInputTime = 0.5;

    @Override
    public String getName() {
        return "Player";
    }

    private double accelerating;
    private double breaking;
    private double steering;

    public void accelerate(boolean apply, double ms) {
        accelerating += ms/1000 * fullInputTime*accelerationSensitivity * (apply ? 1 : -3);
        accelerating = accelerating < 0 ? 0 : accelerating > 1 ? 1 : accelerating;
    }

    public void breaks(boolean apply, double ms) {
        breaking += ms/1000 * fullInputTime*breakingSensitivity * (apply ? 1 : -3);
        breaking = breaking < 0 ? 0 : breaking > 1 ? 1 : breaking;
    }

    public void turn(boolean left, boolean right, double ms) {
        double steeringDelta = ms / 1000 * fullInputTime * steeringSensitivity;
        double turnLeftDelta = steeringDelta * (left ? -1 : 0) * (steering > 0 ? 2 : 1);
        double turnRightDelta = steeringDelta * (right ? +1 : 0) * (steering < 0 ? 2 : 1);
        steering += turnLeftDelta + turnRightDelta;
        if (!left && !right) {
            // inertia of steering
            steeringDelta = min(steeringDelta, abs(steering));
            steeringDelta = steering > 0 ? -steeringDelta : (steering < 0 ? steeringDelta : 0);
            steering += steeringDelta;
        } else {
            steering = steering < -1 ? -1 : steering > 1 ? 1 : steering;
        }
    }

    @Override
    public double accelerating() {
        return accelerating;
    }

    @Override
    public double breaking() {
        return breaking;
    }

    @Override
    public double steering() {
        return steering;
    }

    @Override
    public void update(double seconds) {

    }
}
