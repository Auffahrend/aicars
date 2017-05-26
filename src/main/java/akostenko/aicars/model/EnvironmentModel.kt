package akostenko.aicars.model

interface EnvironmentModel {
    companion object {
        /** *m/s^2*  */
        val g = 9.81
        /** *kg / m^3*  */
        val airDensity = 1.204
        val SECONDS_PER_MINUTE = 60
    }
}
