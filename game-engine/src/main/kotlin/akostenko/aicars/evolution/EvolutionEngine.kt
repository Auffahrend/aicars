package akostenko.aicars.evolution

import akostenko.aicars.neural.LinearNN
import akostenko.aicars.neural.NNDriver
import akostenko.aicars.race.car.Car

class EvolutionEngine {

    val crossingoverFraction = 0.40
    val mutationFraction = 0.1

    fun getNextPopulation(cars: List<Car<NNDriver>>): List<NNDriver> {
        val best = cars.sortedByDescending { it.trackDistance }
                .map { it.driver }
                .take(cars.size * (1-crossingoverFraction - mutationFraction).toInt())

        val mutants = best.takeRandom(cars.size*mutationFraction)
    }

}

private fun <E> Collection<E>.takeRandom(n: int): Any {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
