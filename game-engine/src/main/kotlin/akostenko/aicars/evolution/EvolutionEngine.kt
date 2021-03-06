package akostenko.aicars.evolution

import akostenko.aicars.neural.NNDriver
import akostenko.aicars.neural.NeuralNet
import akostenko.aicars.race.car.Car
import java.util.concurrent.ThreadLocalRandom

class EvolutionEngine {

    val crossOverFraction = 0.3
    val mutationFraction = 0.4
    val mutationsFactor = 0.01

    fun getNextPopulation(cars: List<Car<NNDriver>>): List<NNDriver> {
        cars.forEach { it.driver.neural.fitness = it.odometer }
        val best = cars.sortedByDescending { it.driver.neural.fitness }
                .map { it.driver }
                .map { it.neural.copy(false, false) }
                .take((cars.size * (1-crossOverFraction - mutationFraction)).toInt())

        val mutants = best.takeRandom((cars.size*mutationFraction).toInt())
                .map { it.copyAndMutate(mutationsFactor) }

        val children = getPairsForBreeding(best, cars.size - best.size - mutants.size)
                .map { (first, second) -> first.breed(second) }

        return (best + mutants + children).map { NNDriver(it) }
    }

    private fun getPairsForBreeding(drivers: List<NeuralNet>, amount: Int) : List<Pair<NeuralNet, NeuralNet>> {
        return drivers.takeRandom(amount)
                .map { first -> first to drivers.filter { it != first }
                        .get( ThreadLocalRandom.current().nextInt(drivers.size - 1 )) }
    }

    companion object {
        val instance = EvolutionEngine()
    }
}

private fun <E> List<E>.takeRandom(n: Int): List<E> {
    if (n < 0) throw IllegalArgumentException("Positive integer expected. Got $n")
    if (n == 0) return emptyList()
    val random = ThreadLocalRandom.current()
    val indexes = mutableListOf<Int>()

    while (indexes.size < n) {
        indexes.add(random.nextInt(this.size))
    }

    return indexes.map { this.elementAt(it) }
}
