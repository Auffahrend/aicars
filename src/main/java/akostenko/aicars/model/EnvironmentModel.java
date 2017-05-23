package akostenko.aicars.model;

public interface EnvironmentModel {
    /** <i>m/s^2</i> */
    double g = 9.81;
    /** <i>kg / m^3</i> */
    double airDensity = 1.204;
    int SECONDS_PER_MINUTE = 60;
}
