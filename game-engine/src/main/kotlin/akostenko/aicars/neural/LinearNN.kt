package akostenko.aicars.neural

import akostenko.aicars.race.car.Car
import org.apache.commons.math3.util.FastMath.max
import org.apache.commons.math3.util.FastMath.min
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom


class LinearNN(override val generation: Int,
               override val mutations: Int,
               override val crosses: Int) : NeuralNet() {
    override var fitness: Double = 0.0

    /** all connections are [-1, 1] */
    internal var nodeConnections : MutableList<MutableList<Double>> = mutableListOf()

    private val inputNodes : MutableList<Double> = mutableListOf()
    private val outputNodes : MutableList<Double> = mutableListOf()
    override val name = "LinGen${generation}Cr${crosses}Mu${mutations}"
    override val genomeSize: Int = LinearNN.genomeSize

    init {
        for (i in 1..outputCount) { outputNodes.add(0.0) }

        for (i in 0 until inputCount) {
            inputNodes.add(0.0)
            nodeConnections.add(mutableListOf())
            for (o in 1..outputCount) {
                nodeConnections[i].add(0.0)
            }
        }
    }

    override fun output(index: Int): Double {
        return min(max(outputNodes[index], 0.0), 1.0)
    }

    override fun calculateOutput() {
        for (o in 0 until outputCount) {
            outputNodes[o] = 0.0
            for (i in 0 until inputCount) {
                outputNodes[o] += inputNodes[i] * nodeConnections[i][o]
            }
        }
    }

    override fun readCarParametersToInput(car: Car<*>) {
        val parameters = normalizeCarParameters(car)

        for (i in 0 until inputCount) { inputNodes[i] = parameters[i] }
    }

    override fun copyAndMutate(mutationsFactor: Double): LinearNN {
        val copy = copy(true, false)
        val random = ThreadLocalRandom.current()
        for (i in 1..(mutationsFactor * genomeSize).toInt()) {
            copy.nodeConnections[random.nextInt(inputCount)][random.nextInt(outputCount)] += random.nextDouble() - 0.5
        }
        return copy
    }

    override fun <N: NeuralNet> breed(second: N): N {
        if (second !is LinearNN) {
            throw IllegalArgumentException("Breeding is possible only between same classes of nets. Found $second")
        }
        val child = copy(false, true)
        val crossOverPoint = ThreadLocalRandom.current().nextInt(genomeSize - 1) + 1
        val crossI = crossOverPoint / outputCount
        val crossJ = crossOverPoint % outputCount
        for (j in crossJ until outputCount) {
            child.copyConnectionFrom(second, crossI, j)
        }

        for (i in crossI+1 until inputCount) {
            for (j in 0 until outputCount) {
                child.copyConnectionFrom(second, i, j)
            }
        }
        return child as N
    }

    private fun copyConnectionFrom(source: LinearNN, i: Int, j: Int) {
        nodeConnections[i][j] = source.nodeConnections[i][j]
    }

    override fun serialize(): String {
        return LinearNN.serialize(this)
    }

    override fun copy(isMutant: Boolean, isCrossOver: Boolean): LinearNN {
        val copy = LinearNN(generation + if (!isMutant && !isCrossOver) 1 else 0,
                mutations + if (isMutant) 1 else 0,
                crosses + if (isCrossOver) 1 else 0)
        copy.fitness = if (!isMutant && !isCrossOver) fitness else 0.0
        copy.nodeConnections = nodeConnections.map { row -> row.toMutableList() }.toMutableList()
        return copy
    }

    companion object {
        val genomeSize: Int = inputCount * outputCount

        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(this.javaClass)

        val type = "Linear"

        private val nameDelimiter = ":"
        private val valueDelimiter = ","
        private val rowDelimiter = "~~~"

        fun serialize(net: LinearNN): String {
            return buildString {
                append(net.generation).append(nameDelimiter)
                append(net.mutations).append(nameDelimiter)
                append(net.crosses).append(nameDelimiter)
                append(net.fitness).append(nameDelimiter)

                net.nodeConnections.forEach { row ->
                    row.forEach { value -> append(value.toString()).append(valueDelimiter) }
                    append(rowDelimiter)
                }
            }
        }

        fun deserialize(line: String): LinearNN {
            val nameParts = line.split(nameDelimiter.toRegex())
            val net: LinearNN
            if (nameParts.size >= 4) {
                net = LinearNN(nameParts[0].toInt(), nameParts[1].toInt(), nameParts[2].toInt())
                net.fitness = nameParts[3].toDouble()
            } else {
                logger.warn("Invalid format, there is only ${nameParts.size} parts for name")
                net = LinearNN(0, 0, 0)
            }

            net.nodeConnections = with(nameParts.last()) {
                split(rowDelimiter.toRegex())
                        .takeWhile { it.isNotEmpty() }
                        .map {
                            it.split(valueDelimiter.toRegex())
                                    .takeWhile { it.isNotEmpty() }
                                    .map { it.toDouble() }
                                    .toMutableList()
                        }.toMutableList()
            }

            return net
        }
    }
}