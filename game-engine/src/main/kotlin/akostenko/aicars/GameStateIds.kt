package main.kotlin.akostenko.aicars

import main.kotlin.akostenko.aicars.menu.MenuState
import main.kotlin.akostenko.aicars.plots.CarPlotsState
import main.kotlin.akostenko.aicars.race.RaceState
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
