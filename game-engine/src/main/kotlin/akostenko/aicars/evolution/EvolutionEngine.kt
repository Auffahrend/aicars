package akostenko.aicars.evolution

import akostenko.aicars.neural.NNDriver
import akostenko.aicars.race.car.Car
import java.util.concurrent.ThreadLocalRandom

class EvolutionEngine {

    val crossingoverFraction = 0.40
    val mutationFraction = 0.1

    fun getNextPopulation(cars: List<Car<NNDriver>>): List<NNDriver> {
        val best = cars.sortedByDescending { it.trackDistance }
                .map { it.driver }
                .take(cars.size * (1-crossingoverFraction - mutationFraction).toInt())

        val mutants = best.takeRandom((cars.size*mutationFraction).toInt())
        mutants.map { it.neural.copy() }
    }

}

private fun <E> List<E>.takeRandom(n: Int): List<E> {
    val random = ThreadLocalRandom.current()
    val indexes = mutableSetOf<Int>()

    while (indexes.size < n) {
        indexes.add(random.nextInt(this.size))
    }

    return indexes.map { this.elementAt(it) }
}
