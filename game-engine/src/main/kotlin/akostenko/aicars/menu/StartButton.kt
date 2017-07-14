package main.kotlin.akostenko.aicars.menu

import main.kotlin.akostenko.aicars.Game
import main.kotlin.akostenko.aicars.GameSettings
import main.kotlin.akostenko.aicars.GameStateIds
import main.kotlin.akostenko.aicars.plots.CarPlotsState
import main.kotlin.akostenko.aicars.race.RaceState
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
