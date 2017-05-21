package akostenko.aicars.model;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.toRadians;

public interface CarModel {
    /** <i>m</i> */
    double tyreRadius = 0.3;
    double frontTyreWidth = 0.25;
    double readTyreWidth = 0.35;
    /** height of center of mass, <i>m</i> */
    double massCenterHeight = 0.2;

    /** <i>kg</i> */
    double mass = 700;
    /** <i>kg*m</i> */
    double yawInertia = 500;
    /** <i>1/s</i> */
    double max_rpm = 12000;
    /** <i>1/s</i> */
    double min_rpm = 3500;
    /** non-dimensional */
    double cx = 0.8; // Cx
    /** non-dimensional */
    double cy = 1.0033; // Cy
    /** <i>m^2</i> */
    double frontArea = 1.1;
    /** <i>m^2</i> */
    double wingArea = 5;
    /** stiction coefficient between hot slick tyre and dry asphalt, non-dimensional */
    double tyreStiction = 1.3;
    /** rolling friction coefficient between rubber and asphalt, <i>m</i> */
    double tyreRollingFriction = 0.010;
    double axleTrack = 2; // m
    double wheelbase = 4.5; // m
    double massCenterOffset = .45; // from rear axle, measured as offset/wheelbase
    double length = 5.5; // m
    double maxSteering = 0.2*PI;
    double peakLateralForceAngle = toRadians(3);
}
