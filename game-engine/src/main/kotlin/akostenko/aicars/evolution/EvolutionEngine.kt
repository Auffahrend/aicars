package akostenko.aicars.evolution

import akostenko.aicars.neural.LinearNN
import akostenko.aicars.neural.NNDriver
import akostenko.aicars.neural.NeuralNet
import akostenko.aicars.race.car.Car
import java.util.concurrent.ThreadLocalRandom

class EvolutionEngine {

    private val crossOverFraction = 0.40
    private val mutationFraction = 0.1
    private val mutationsFactor = 0.05

    fun getNextPopulation(cars: List<Car<NNDriver>>): List<NNDriver> {
        val best = cars.sortedByDescending { it.fitness }
                .map { it.driver }
                .map { it.neural.copy(false, false) }
                .take((cars.size * (1-crossOverFraction - mutationFraction)).toInt())

        val mutants = best.takeRandom((cars.size*mutationFraction).toInt())
                .map { it.copyAndMutate(mutationsFactor) }

        val children = getPairsForBreeding(best, cars.size - best.size - mutants.size)
                .map { breed(it.first, it.second)}

        return (best + mutants + children).map { NNDriver(it) }
    }

    private fun getPairsForBreeding(drivers: List<NeuralNet>, amount: Int) : List<Pair<NeuralNet, NeuralNet>> {
        return drivers.takeRandom(amount)
                .map { first -> first to drivers.filter { it != first }
                        .get( ThreadLocalRandom.current().nextInt(drivers.size - 1 )) }
    }

    private fun <N: NeuralNet> breed(first: N, second: N): N {
        return when (first) {
            is LinearNN -> first.breed(second)
            else -> throw IllegalArgumentException("Breeding for ${first.javaClass} is not implemented.")
        }
    }

    companion object {
        val instance = EvolutionEngine()
    }
}

private fun <E> List<E>.takeRandom(n: Int): List<E> {
    if (n < 0) throw IllegalArgumentException("Positive integer expected. Got $n")
    if (n == 0) return emptyList()
    val random = ThreadLocalRandom.current()
    val indexes = mutableSetOf<Int>()

    while (indexes.size < n) {
        indexes.add(random.nextInt(this.size))
    }

    return indexes.map { this.elementAt(it) }
}
