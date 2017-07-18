package akostenko.aicars.model

import org.apache.commons.math3.util.FastMath.PI
import org.apache.commons.math3.util.FastMath.toRadians

object CarModel {
    /** *m*  */
    val tyreRadius = 0.3
    val frontTyreWidth = 0.25
    val readTyreWidth = 0.35
    /** height of center of mass, *m*  */
    val massCenterHeight = 0.2

    /** *kg*  */
    val mass = 700.0
    /** *kg*m*  */
    val yawInertia = 500.0
    /** *1/s*  */
    val max_rpm = 12000.0
    /** *1/s*  */
    val min_rpm = 3500.0
    /** non-dimensional  */
    val cx = 0.8 // Cx
    /** non-dimensional  */
    val cy = 1.0033 // Cy
    /** *m^2*  */
    val frontArea = 1.1
    /** *m^2*  */
    val wingArea = 5.0
    /** stiction coefficient between hot slick tyre and dry asphalt, non-dimensional  */
    val tyreStiction = 1.3
    /** rolling friction coefficient between rubber and asphalt, *m*  */
    val tyreRollingFriction = 0.010
    val axleTrack = 2.0 // m
    val wheelbase = 4.5 // m
    val rearWeightPercent = .45
    val frontWeightPercent = 1 - rearWeightPercent
    val length = 5.5 // m
    val maxSteering = 0.2 * PI
    val peakLateralForceAngle = toRadians(5.0)
    /** starting from this speed (m/s) pure geometry turning physics is replaced with dynamic forces turning physics.  */
    val highSpeedCorneringThreshold = 10.0
}
