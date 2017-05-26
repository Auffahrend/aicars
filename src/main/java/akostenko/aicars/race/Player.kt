package akostenko.aicars.race

import java.lang.StrictMath.abs
import java.lang.StrictMath.min

class Player : Driver() {

    private val steeringSensitivity = 4.0
    private val accelerationSensitivity = 5.0
    private val breakingSensitivity = 10.0
    private val fullInputTime = 0.5

    override val name: String
        get() = "Player"

    private var accelerating: Double = 0.toDouble()
    private var breaking: Double = 0.toDouble()
    private var steering: Double = 0.toDouble()

    fun accelerate(apply: Boolean, ms: Double) {
        accelerating += ms / 1000 * fullInputTime * accelerationSensitivity * (if (apply) 1 else -3).toDouble()
        accelerating = if (accelerating < 0) 0 else if (accelerating > 1) 1 else accelerating
    }

    fun breaks(apply: Boolean, ms: Double) {
        breaking += ms / 1000 * fullInputTime * breakingSensitivity * (if (apply) 1 else -3).toDouble()
        breaking = if (breaking < 0) 0 else if (breaking > 1) 1 else breaking
    }

    fun turn(left: Boolean, right: Boolean, ms: Double) {
        var steeringDelta = ms / 1000 * fullInputTime * steeringSensitivity
        val turnLeftDelta = steeringDelta * (if (left) -1 else 0).toDouble() * (if (steering > 0) 2 else 1).toDouble()
        val turnRightDelta = steeringDelta * (if (right) +1 else 0).toDouble() * (if (steering < 0) 2 else 1).toDouble()
        steering += turnLeftDelta + turnRightDelta
        if (!left && !right) {
            // inertia of steering
            steeringDelta = min(steeringDelta, abs(steering))
            steeringDelta = if (steering > 0) -steeringDelta else if (steering < 0) steeringDelta else 0
            steering += steeringDelta
        } else {
            steering = if (steering < -1) -1 else if (steering > 1) 1 else steering
        }
    }

    override fun accelerating(): Double {
        return accelerating
    }

    override fun breaking(): Double {
        return breaking
    }

    override fun steering(): Double {
        return steering
    }

    override fun update(seconds: Double) {

    }
}
