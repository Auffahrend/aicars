package akostenko.aicars.neural;

import akostenko.aicars.race.car.Car
import org.apache.commons.math3.util.FastMath
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom

class TanHyperWith2LayersNN(override val generation: Int,
                            override val mutations: Int,
                            override val crosses: Int) : NeuralNet() {
    override val name: String = "Tanh2L G$generation C$crosses M$mutations"
    override var fitness: Double = 0.0

    private val inputs: MutableList<Double> = mutableListOf()
    private val layer1Size = inputCount
    private val layer2Size = inputCount
    private val layer1: MutableList<Double> = mutableListOf()
    private val layer2: MutableList<Double> = mutableListOf()
    private val outputs: MutableList<Double> = mutableListOf()

    private var layer1Weights: MutableList<MutableList<Double>> = mutableListOf()
    private var layer2Weights: MutableList<MutableList<Double>> = mutableListOf()
    private var outputWeights: MutableList<MutableList<Double>> = mutableListOf()

    init {
        for (i in 0 until outputCount) {
            outputs.add(0.0)
        }

        for (i in 0..inputCount) {
            if (i < inputCount) inputs.add(0.0)
            layer1Weights.add(mutableListOf())
            for (o in 0..layer1Size) {
                layer1Weights[i].add(0.0)
            }
        }

        for (i in 0..layer1Size) {
            if (i < inputCount) layer1.add(0.0)
            layer2Weights.add(mutableListOf())
            for (o in 0..layer2Size) {
                layer2Weights[i].add(0.0)
            }
        }

        for (i in 0..layer2Size) {
            if (i < inputCount) layer2.add(0.0)
            outputWeights.add(mutableListOf())
            for (o in 0..outputCount) {
                outputWeights[i].add(0.0)
            }
        }
    }

    private val layer1GenomeSize = layer1Weights.size * layer1Weights.first().size
    private val layer2GenomeSize = layer2Weights.size * layer2Weights.first().size
    private val outputsGenomeSize = outputWeights.size * outputWeights.first().size
    override val genomeSize: Int = layer1GenomeSize + layer2GenomeSize + outputsGenomeSize

    override fun calculateOutput() {
        calculateLayer(inputs, layer1, layer1Weights)
        calculateLayer(layer1, layer2, layer2Weights)
        calculateLayer(layer2, outputs, outputWeights)

    }

    private fun calculateLayer(inputLayer: MutableList<Double>, outputLayer: MutableList<Double>, weights: MutableList<MutableList<Double>>) {
        for (o in 0 until outputLayer.size) {
            val sum = (0 until inputLayer.size).sumByDouble { i -> weights[i][o] * inputLayer[i] } +
                    weights.last()[o] // offset
            outputLayer[o] = FastMath.tanh(sum)
        }
    }

    override fun output(index: Int): Double {
        return outputs[index]
    }

    override fun readCarParametersToInput(car: Car<*>) {
        val parameters = normalizeCarParameters(car)
        for (i in 0 until inputCount) {
            inputs[i] = parameters[i]
        }
    }

    override fun copyAndMutate(mutationsFactor: Double): TanHyperWith2LayersNN {
        val copy = copy(true, false)
        val random = ThreadLocalRandom.current()

        for (mutationIndex in 1..(mutationsFactor * genomeSize).toInt()) {
            val mutationPoint = random.nextInt(genomeSize)
            copy.getGeneByGlobalIndex(mutationPoint)
                    .let { (i, o, genome) ->
                        {
                            genome[i][o] += random.nextDouble() - 0.5 // TODO it's not going to find optimum with such mutations
                        }
                    }
        }
        return copy
    }

    private fun getGeneByGlobalIndex(globalGeneIndex: Int): Triple<Int, Int, MutableList<MutableList<Double>>> {
        return when {
            globalGeneIndex < layer1GenomeSize -> globalGeneIndex to layer1Weights
            globalGeneIndex < layer2GenomeSize -> (globalGeneIndex - layer1GenomeSize) to layer2Weights
            else -> (globalGeneIndex - layer1GenomeSize - layer2GenomeSize) to outputWeights
        }.let { (index, genome) ->
            val i = index / genome.first().size
            val o = index % genome.first().size
            Triple(i, o, genome)
        }
    }

    override fun <N : NeuralNet> breed(second: N): N {
        if (second !is TanHyperWith2LayersNN) {
            throw IllegalArgumentException("Breeding is possible only between same classes of nets. Found $second")
        }
        val child = copy(false, true)
        val crossOverPoint = ThreadLocalRandom.current().nextInt(genomeSize - 1) + 1
        for (index in crossOverPoint .. genomeSize) {
            second.getGeneByGlobalIndex(index)
                    .let { (i, o, source) -> child.getGeneByGlobalIndex(index)
                            .let { (_, _, destination) -> destination[i][o] = source[i][o]}}
        }

        return child as N
    }

    override fun copy(isMutant: Boolean, isCrossOver: Boolean): TanHyperWith2LayersNN {
        val copy = TanHyperWith2LayersNN(generation + if (!isMutant && !isCrossOver) 1 else 0,
                mutations + if (isMutant) 1 else 0,
                crosses + if (isCrossOver) 1 else 0)
        copy.fitness = if (!isMutant && !isCrossOver) fitness else 0.0
        copy.layer1Weights = layer1Weights.map { row -> row.toMutableList() }.toMutableList()
        copy.layer2Weights = layer2Weights.map { row -> row.toMutableList() }.toMutableList()
        copy.outputWeights = outputWeights.map { row -> row.toMutableList() }.toMutableList()
        return copy
    }

    override fun serialize(): String {
        return TanHyperWith2LayersNN.serialize(this)
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(this.javaClass)

        val type = "TanH2L"

        private val nameDelimiter = ":"
        private val geneDelimiter = ","

        fun serialize(net: TanHyperWith2LayersNN): String {
            return buildString {
                append(net.generation).append(nameDelimiter)
                append(net.mutations).append(nameDelimiter)
                append(net.crosses).append(nameDelimiter)
                append(net.fitness).append(nameDelimiter)

                for (i in 0 until net.genomeSize) {
                    net.getGeneByGlobalIndex(i)
                            .let { (i, o, genome) -> genome[i][o] }
                            .let { value -> append(value.toString()).append(geneDelimiter)}
                }
            }
        }

        fun deserialize(line: String): TanHyperWith2LayersNN {
            val nameParts = line.split(nameDelimiter.toRegex())
            val net: TanHyperWith2LayersNN
            if (nameParts.size >= 4) {
                net = TanHyperWith2LayersNN(nameParts[0].toInt(), nameParts[1].toInt(), nameParts[2].toInt())
                net.fitness = nameParts[3].toDouble()
            } else {
                logger.warn("Invalid format, there is only ${nameParts.size} parts for name")
                net = TanHyperWith2LayersNN(0, 0, 0)
            }

            val genes = nameParts.last().split(geneDelimiter)

            for (geneIndex in 0 until net.genomeSize) {
                net.getGeneByGlobalIndex(geneIndex)
                        .let { (i, o, genome) -> genome[i][o] = genes[geneIndex].toDouble() }
            }

            return net
        }
    }
}
