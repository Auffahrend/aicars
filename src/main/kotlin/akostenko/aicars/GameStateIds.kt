package akostenko.aicars

import akostenko.aicars.menu.MenuState
import akostenko.aicars.plots.CarPlotsState
import akostenko.aicars.race.RaceState
import org.newdawn.slick.state.GameState
import java.util.*
import kotlin.reflect.KClass

object GameStateIds {
    private val ids = HashMap<KClass<out GameState>, Int>()

    init {
        ids.put(MenuState::class, 1)
        ids.put(RaceState::class, 2)
        ids.put(CarPlotsState::class, 3)
    }

    fun getId(state: KClass<out GameState>): Int {
        return Optional.ofNullable(ids[state])
                .orElseThrow { IllegalArgumentException("ID for game state ${state.simpleName} is unknown.") }
    }
}
