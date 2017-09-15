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
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask

class NeuralNetTrainingState : GraphicsGameState() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val timeStep = 30 // ms
    private val assessmentTime = 60000 // ms

    private val listeners = mutableListOf<KeyListener>()
    private val lineWidth = 3f
    private val fontColor = Color(220, 200, 50)
    private val subMenuHeight = 36f
    private val subMenuItemHeight = 24f
    private val itemFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, subMenuItemHeight.toInt()), true)
    private val leftMargin = Game.screenWidth * 0.2f
    private val topMargin = Game.screenHeight * 0.2f

    private val evolutionEngine = EvolutionEngine.instance

    private var population: List<DriverTracker> = emptyList()
    private lateinit var cars: List<Car<NNDriver>>
    private var populationIndex = 0
    private var currentProgress = 0
    private var currentTasks: List<ForkJoinTask<*>> = emptyList()
    private var run = true

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
        populationIndex = population.map { it.driver.neural.generation }.max() ?: 0

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
                    ForkJoinPool.commonPool().submit({
                        try {
                            while (run && !it.isDone) {
                                it.driver.car.update(timeStep)
                                it.timeDriven += timeStep
                                it.isDone = it.timeDriven >= assessmentTime
                            }
                        } catch (e : Throwable) {
                            log.error("Calculation error", e)
                        }
                    })
                }

        ForkJoinPool.commonPool().submit({
            try {
                log.info("Calculating population #$populationIndex")
                currentTasks.filter { !it.isCancelled }.map { it.join() }
                log.info("Population #$populationIndex calculated")
                if (run) {
                    log.info("Evolving population #$populationIndex")
                    newPopulation(evolutionEngine.getNextPopulation(cars))
                }
            } catch (e : Throwable) {
                log.error("Next population generation error", e)
            }
        })
    }

    private fun pauseTraining() {
        currentTasks
                .filter { !it.isDone }
                .map { it.cancel(true) }
    }

    override fun update(container: GameContainer?, game: StateBasedGame?, delta: Int) {
        currentProgress = 100 * population.sumBy { it.timeDriven } / (population.size * assessmentTime)
    }

    override fun render(container: GameContainer, game: StateBasedGame, g: Graphics) {
        g.font = itemFont
        g.color = fontColor
        g.drawString("Population #$populationIndex, $currentProgress %", leftMargin, topMargin)
    }
}

internal class DriverTracker(val driver: NNDriver) {
    var timeDriven: Int = 0
    var isDone = false
}