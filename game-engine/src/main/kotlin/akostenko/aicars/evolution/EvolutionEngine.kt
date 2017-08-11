package akostenko.aicars.evolution

import akostenko.aicars.neural.NNDriver
import akostenko.aicars.neural.NeuralNet
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
                .map { it.neural.copy(true, false) }
                .map { applyMutations(it) }
                .map { NNDriver(it) }

        val children = cars.takeRandom(cars.size - best.size - mutants.size)
                .zip( cars.takeRandom(cars.size - best.size - mutants.size) )
                .map { breed(it.first.driver.neural, it.second.driver.neural)}
                .map { NNDriver(it) }

        return best + mutants + children
    }

    private fun <N: NeuralNet> breed(first: N, second: N): N {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <N: NeuralNet> applyMutations(net: N): N {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
