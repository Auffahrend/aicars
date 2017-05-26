package akostenko.aicars.menu

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.plots.CarPlotsState
import akostenko.aicars.race.RaceState

import org.newdawn.slick.state.GameState
import org.newdawn.slick.state.transition.EmptyTransition
import org.newdawn.slick.state.transition.FadeInTransition

import java.util.Collections
import java.util.HashMap

class StartButton : AbstractSubMenu<MenuItem>() {

    override val title: String
        get() = "START"

    override fun enter() {
        Game.get()
                .enterState(GameStateIds.getId(modeGameStates[GameSettings.get().mode]),
                        EmptyTransition(), FadeInTransition())
    }

    override fun items(): List<MenuItem> {
        return emptyList<MenuItem>()
    }

    companion object {

        private val modeGameStates = HashMap<Mode, Class<out GameState>>()

        init {
            modeGameStates.put(WithPlayer(), RaceState::class.java)
            modeGameStates.put(CarPerformanceTests(), RaceState::class.java)
            modeGameStates.put(CarPhysicsTests(), CarPlotsState::class.java)
        }
    }
}
