package akostenko.aicars.evolution

import akostenko.aicars.neural.LinearNN
import akostenko.aicars.neural.NNDriver
import akostenko.aicars.neural.NeuralNet
import akostenko.aicars.race.car.Car
import java.util.concurrent.ThreadLocalRandom

class EvolutionEngine {

    private val crossingoverFraction = 0.40
    private val mutationFraction = 0.1
    private val mutationsAmount = 0.05

    fun getNextPopulation(cars: List<Car<NNDriver>>): List<NNDriver> {
        val best = cars.sortedByDescending { it.trackDistance }
                .map { it.driver }
                .take(cars.size * (1-crossingoverFraction - mutationFraction).toInt())

        val mutants = best.takeRandom((cars.size*mutationFraction).toInt())
                .map { it.neural }
                .map { it.copyAndMutate(mutationsAmount) }
                .map { NNDriver(it) }

        val children = getPairForBreeding(best, cars.size - best.size - mutants.size)
                .map { breed(it.first.neural, it.second.neural)}
                .map { NNDriver(it) }

        return best + mutants + children
    }

    private fun getPairForBreeding(drivers: List<NNDriver>, amount: Int) : List<Pair<NNDriver, NNDriver>> {
        return drivers.takeRandom(amount)
                .map { first -> first to drivers.filter { it != first }
                        .get( ThreadLocalRandom.current().nextInt(drivers.size - 1 )) }
    }

    private fun <N: NeuralNet> breed(first: N, second: N): N {
        return when (first) {
            is LinearNN -> {
                first.breed(second)
            }
            else -> throw IllegalArgumentException("Breeding for ${first.javaClass} is not implemented.")
        }
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
