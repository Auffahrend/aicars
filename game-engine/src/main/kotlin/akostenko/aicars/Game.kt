package main.kotlin.akostenko.aicars

import main.kotlin.akostenko.aicars.keyboard.GameAction
import main.kotlin.akostenko.aicars.menu.MenuState
import main.kotlin.akostenko.aicars.plots.CarPlotsState
import main.kotlin.akostenko.aicars.race.RaceState
import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.SlickException
import org.newdawn.slick.state.StateBasedGame

class Game : StateBasedGame(GAME_NAME) {

    @Throws(SlickException::class)
    override fun initStatesList(container: GameContainer) {
        addState(MenuState())
        addState(RaceState())
        addState(CarPlotsState())
    }

    fun noticeAction(gameAction: GameAction) {
        try {
            when (gameAction) {
                GameAction.RESTART -> restartGame()
                GameAction.QUIT -> quitState()
            }
        } catch (e: SlickException) {
            throw RuntimeException(e)
        }

    }

    @Throws(SlickException::class)
    private fun restartGame() {
        enterState(GameStateIds.getId(MenuState::class))
    }

    @Throws(SlickException::class)
    private fun quitState() {
        val id = currentState.id
        if (GameStateIds.getId(MenuState::class) == id) {
            // quit the game
            container.exit()
        } else if (GameStateIds.getId(RaceState::class) == id || GameStateIds.getId(CarPlotsState::class) == id) {
            enterState(GameStateIds.getId(MenuState::class))
        }
    }

    companion object {

        var screenWidth = 0.0f
        var screenHeight = 0.0f
        private val GAME_NAME = "AI Cars game"

        private lateinit var instance: Game

        fun get(): Game {
            return instance
        }

        @Throws(SlickException::class)
        @JvmStatic fun main(args: Array<String>) {
            instance = Game()
            val app = AppGameContainer(instance)
            screenHeight = app.screenHeight.toFloat()
            screenWidth = app.screenWidth.toFloat()
            app.setDisplayMode(screenWidth.toInt(), screenHeight.toInt(), false)
            app.setMinimumLogicUpdateInterval(1)
            app.setMaximumLogicUpdateInterval(10)
            app.start()
            instance.enterState(GameStateIds.getId(MenuState::class))
        }
    }
}
