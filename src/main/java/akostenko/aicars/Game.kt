package akostenko.aicars

import akostenko.aicars.keyboard.GameAction
import akostenko.aicars.menu.MenuState
import akostenko.aicars.plots.CarPlotsState
import akostenko.aicars.race.RaceState
import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.SlickException
import org.newdawn.slick.state.StateBasedGame

class Game : StateBasedGame(Game.GAME_NAME) {

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

        var screenWidth: Int = 0
        var screenHeight: Int = 0
        private val GAME_NAME = "AI Cars game"

        private var instance: Game? = null

        fun get(): Game {
            return instance!!
        }

        @Throws(SlickException::class)
        @JvmStatic fun main(args: Array<String>) {
            instance = Game()
            val app = AppGameContainer(instance)
            screenHeight = app.screenHeight
            screenWidth = app.screenWidth
            app.setDisplayMode(screenWidth, screenHeight, true)
            app.setMinimumLogicUpdateInterval(1)
            app.setMaximumLogicUpdateInterval(10)
            app.start()
            instance!!.enterState(GameStateIds.getId(MenuState::class))
        }
    }
}
