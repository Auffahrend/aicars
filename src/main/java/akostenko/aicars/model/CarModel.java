package akostenko.aicars.model;

public interface CarModel {
    /** <i>m</i> */
    double tyreRadius = 0.3;
    /** <i>kg</i> */
    double mass = 700;
    /** <i>1/s</i> */
    double max_rpm = 12000;
    /** <i>1/s</i> */
    double min_rpm = 3500;
    /** non-dimensional */
    double cx = 0.80; // Cx
    /** non-dimensional */
    double cy = 1.0033; // Cy
    /** <i>m^2</i> */
    double frontArea = 1;
    /** <i>m^2</i> */
    double wingArea = 5;
    /** stiction coefficient between hot slick tyre and dry asphalt, non-dimensional */
    double tyreStiction = 1.25;
    /** rolling friction coefficient between rubber and asphalt, <i>m</i> */
    double tyreRollingFriction = 0.015;
    double axleTrack = 2; // m
    double wheelbase = 3.5; // m
    double length = 5; // m
}
