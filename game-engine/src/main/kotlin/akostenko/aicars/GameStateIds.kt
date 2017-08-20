package akostenko.aicars

import akostenko.aicars.menu.MenuState
import akostenko.aicars.neural.NeuralNetTrainingState
import akostenko.aicars.plots.CarPlotsState
import akostenko.aicars.race.RaceState
import org.newdawn.slick.state.GameState
import java.util.*
import kotlin.reflect.KClass

object GameStateIds {
    private val ids = mapOf(MenuState::class to 1,
            RaceState::class to 2,
            CarPlotsState::class to 3,
            NeuralNetTrainingState::class to 4)

    fun getId(state: KClass<out GameState>): Int {
        return Optional.ofNullable(ids[state])
                .orElseThrow { IllegalArgumentException("ID for game state ${state.simpleName} is unknown.") }
    }
}
