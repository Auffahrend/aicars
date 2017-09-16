package akostenko.aicars.neural

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.GraphicsGameState
import akostenko.aicars.evolution.EvolutionEngine
import akostenko.aicars.keyboard.SingleKeyAction
import akostenko.aicars.race.car.Car
import org.lwjgl.input.Keyboard
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.StateBasedGame
import org.slf4j.LoggerFactory
import java.awt.Font
import java.util.concurrent.*

class NeuralNetTrainingState : GraphicsGameState() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val timeStep = 30 // ms
    private val assessmentTime = 60000 // ms
    private val stationaryMaxTime = 5000 // ms

    private val listeners = mutableListOf<KeyListener>()
    private val lineWidth = 3f
    private val fontColor = Color(220, 200, 50)
    private val subMenuItemHeight = 24f
    private val itemFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, subMenuItemHeight.toInt()), true)
    private val leftMargin = Game.screenWidth * 0.2f
    private val topMargin = Game.screenHeight * 0.2f
    private val verticalSpacing = (1.5*subMenuItemHeight).toInt()

    private val evolutionEngine = EvolutionEngine.instance

    private var population: List<DriverTracker> = emptyList()
    private lateinit var cars: List<Car<NNDriver>>
    private var generation = 0
    private var currentProgress = 0
    private var currentTasks: List<ForkJoinTask<*>> = emptyList()
    private var run = true

    private val executor = ForkJoinPool.commonPool()

    override fun getID(): Int {
        return GameStateIds.getId(this::class)
    }

    override fun enter(container: GameContainer, game: StateBasedGame?) {
        super.enter(container, game)
        listeners.forEach { listener -> container.input.addKeyListener(listener) }
        run = true
        reset()
    }

    private fun reset() {
        newPopulation(GameSettings.instance.readPopulation(), false)
    }

    private fun newPopulation(newPopulation: List<NNDriver>, save : Boolean = true) {
        if (save) {
            GameSettings.instance.savePopulation(population.map { it.driver })
        }
        population = newPopulation.map { DriverTracker(it) }
        generation = population.map { it.driver.neural.generation }.max() ?: 0

        cars = population.map { Car(it.driver, GameSettings.instance.track) }
        runTraining()
    }

    override fun leave(container: GameContainer, game: StateBasedGame?) {
        run = false
        pauseTraining()
        listeners.forEach { listener -> container.input.removeKeyListener(listener) }
        super.leave(container, game)
    }

    override fun init(container: GameContainer, game: StateBasedGame?) {
        container.setTargetFrameRate(10)
        listeners.add(SingleKeyAction({ startStop() }, Keyboard.KEY_RETURN))
    }

    private fun startStop() {
        run = !run

        if (run) runTraining()
        else pauseTraining()
    }

    private fun runTraining() {
        currentTasks = population
                .filter { !it.isDone }
                .map {
                    executor.submit({
                        try {
                            while (run && !it.isDone) {
                                it.driver.car.update(timeStep)
                                it.timeDriven += timeStep
                                if (it.driver.car.speed.module() < 1.0) {
                                    it.timeStationary += timeStep
                                }
                                it.isDone = it.timeDriven >= assessmentTime
                                        || it.timeStationary >= stationaryMaxTime
                            }
                        } catch (e : Throwable) {
                            log.error("Calculation error", e)
                        }
                    })
                }

        executor.submit({
            try {
                log.info("Calculating population #$generation")
                currentTasks.filter { !it.isCancelled }.map { it.join() }
                log.info("Population #$generation calculated")
                if (run) {
                    log.info("Evolving population #$generation")
                    updatePopulationStatistics()
                    newPopulation(evolutionEngine.getNextPopulation(cars))
                }
            } catch (e : Throwable) {
                log.error("Next population generation error", e)
            }
        })
    }

    private fun updatePopulationStatistics() {
        var minDistance = Int.MAX_VALUE
        var maxDistance = 0
        var averageFitness = 0.0

        population.forEach {
            minDistance = minOf(minDistance, it.driver.car.odometer.toInt())
            maxDistance = maxOf(maxDistance, it.driver.car.odometer.toInt())
            averageFitness += it.driver.car.odometer
        }
        averageFitness /= population.size

        val genomeSize = population.first().driver.neural.genomeSize

        populationStatistics = listOf("distance $minDistance - $maxDistance m",
                "average fitness $averageFitness",
                "population size ${population.size}",
                "genome size $genomeSize",
                "selection ${1.0 - evolutionEngine.crossOverFraction - evolutionEngine.mutationFraction}",
                "crossover ${evolutionEngine.crossOverFraction}",
                "mutants ${evolutionEngine.mutationFraction}",
                "mutations ${evolutionEngine.mutationsFactor * genomeSize}"
        )
    }

    private fun pauseTraining() {
        currentTasks
                .filter { !it.isDone }
                .map { it.cancel(true) }
    }

    override fun update(container: GameContainer?, game: StateBasedGame?, delta: Int) {
        // 1/1000 is to remove overflow of Int on big populations
        currentProgress = 100 * population.sumBy { it.timeDriven / 1000 } / (population.size * assessmentTime / 1000)
    }

    private var populationStatistics: List<String> = emptyList()

    override fun render(container: GameContainer, game: StateBasedGame, g: Graphics) {
        g.font = itemFont
        g.color = fontColor
        g.lineWidth = lineWidth
        var y = topMargin
        g.drawString("Population #$generation, $currentProgress %", leftMargin, y)
        y += verticalSpacing

        g.drawString("Population statistics:", leftMargin, y)
        y += verticalSpacing

        var i = 0
        populationStatistics.forEach {
            s -> g.drawString(s, 2*leftMargin, y + verticalSpacing * i++)
        }
    }
}

internal class DriverTracker(val driver: NNDriver) {
    var timeDriven: Int = 0 // ms
    var timeStationary: Int = 0 // ms
    var isDone = false
}