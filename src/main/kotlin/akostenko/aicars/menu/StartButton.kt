package akostenko.aicars.menu

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.plots.CarPlotsState
import akostenko.aicars.race.RaceState
import org.newdawn.slick.state.GameState
import org.newdawn.slick.state.transition.EmptyTransition
import org.newdawn.slick.state.transition.FadeInTransition
import kotlin.reflect.KClass

class StartButton : AbstractSubMenu<MenuItem>() {

    override val title: String = "START"

    override fun enter() {
        Game.get()
                .enterState(GameStateIds.getId(modeGameStates[GameSettings.instance.mode]!!),
                        EmptyTransition(), FadeInTransition())
    }

    override val items: List<MenuItem> = emptyList()

    companion object {

        private val modeGameStates = mapOf<Mode, KClass<out GameState>>(
                WithPlayer() to RaceState::class,
                NeuralNetDemo() to RaceState::class,
                CarPerformanceTests() to RaceState::class,
                CarPhysicsTests() to CarPlotsState::class
                )
    }
}
