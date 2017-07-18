package akostenko.aicars.menu

import akostenko.aicars.Game
import akostenko.aicars.GameSettings
import akostenko.aicars.GameStateIds
import akostenko.aicars.keyboard.KeyboardHelper
import akostenko.aicars.keyboard.SingleKeyAction
import org.lwjgl.input.Keyboard.KEY_DOWN
import org.lwjgl.input.Keyboard.KEY_LEFT
import org.lwjgl.input.Keyboard.KEY_RETURN
import org.lwjgl.input.Keyboard.KEY_RIGHT
import org.lwjgl.input.Keyboard.KEY_UP
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.KeyListener
import org.newdawn.slick.SlickException
import org.newdawn.slick.TrueTypeFont
import org.newdawn.slick.state.BasicGameState
import org.newdawn.slick.state.StateBasedGame
import java.awt.Font

class MenuState : BasicGameState() {

    //////////////////////////////////////////
    //   |     |       20%              |   //
    //---+------------------------------+---//
    //   |SubM1  item1  item2           |   //
    //   |                              |   //
    //20%|SubM2  item3  item4           |20%//
    //   |                              |   //
    //   |SubMN                         |   //
    //---+------------------------------+---//
    //   |             20%              |   //
    //////////////////////////////////////////


    private val startButton = StartButton()
    private val modeMenu = ModeMenu()
    private val trackMenu = TrackMenu()
    private val debugMenu = DebugMenu()
    private val collisionsMenu = CollisionsMenu()
    private val menu = listOf(startButton, modeMenu, trackMenu, collisionsMenu, debugMenu)

    private var currentMenu = 0

    private val lineWidth = 3f
    private val fontColor = Color(220, 200, 50)
    private val leftMargin = Game.screenWidth * 0.2f
    private val rightMargin = Game.screenWidth * 0.2f
    private val topMargin = Game.screenHeight * 0.2f
    private val bottomMargin = Game.screenHeight * 0.2f
    private val subMenuHeight = 36f
    private val subMenuItemHeight = 24f
    private val subMenuWidth = Game.screenWidth * 0.2f
    private val submenuFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, subMenuHeight.toInt()), true)
    private val itemFont = TrueTypeFont(Font(Font.SANS_SERIF, Font.BOLD, subMenuItemHeight.toInt()), true)
    private val listeners = mutableListOf<KeyListener>()

    override fun getID(): Int {
        return GameStateIds.getId(this::class)
    }

    @Throws(SlickException::class)
    override fun init(container: GameContainer, game: StateBasedGame) {
        val input = container.input
        GameSettings.instance.globalListeners.forEach( { input.addKeyListener(it) })
        KeyboardHelper.listeners.forEach( { input.addKeyListener(it) })

        listeners.add(SingleKeyAction({ -> menuChange(-1) }, KEY_UP))
        listeners.add(SingleKeyAction({ -> menuChange(+1) }, KEY_DOWN))
        listeners.add(SingleKeyAction({ -> selectionChange(-1) }, KEY_LEFT))
        listeners.add(SingleKeyAction({ -> selectionChange(+1) }, KEY_RIGHT))
        listeners.add(SingleKeyAction({ -> enter() }, KEY_RETURN))

        container.setTargetFrameRate(60)
    }

    private fun menuChange(delta: Int) {
        currentMenu += delta + menu.size
        currentMenu %= menu.size
        updateGameSettings()
    }

    private fun selectionChange(delta: Int) {
        menu[currentMenu].change(delta)
        updateGameSettings()
    }

    private fun enter() {
        updateGameSettings()
        startButton.enter()
    }

    @Throws(SlickException::class)
    override fun render(container: GameContainer, game: StateBasedGame, g: Graphics) {
        g.lineWidth = lineWidth
        g.color = fontColor

        menu.forEach { submenu ->
            val currentY = topMargin + menu.indexOf(submenu) * (subMenuHeight + 3 * lineWidth)
            drawSubMenu(g, submenu, currentY)

            drawSubmenuItems(g, submenu, currentY)
        }
    }

    private fun drawSubMenu(g: Graphics, submenu: SubMenu<*>, currentY: Float) {
        g.font = submenuFont
        g.drawString(submenu.title, leftMargin, currentY)
        if (currentMenu == menu.indexOf(submenu)) {
            g.drawLine(leftMargin, currentY + subMenuHeight + lineWidth,
                    leftMargin + subMenuWidth - 5 * lineWidth, currentY + subMenuHeight + lineWidth)
        }
    }

    private fun drawSubmenuItems(g: Graphics, submenu: SubMenu<*>, currentY: Float) {
        g.font = itemFont
        val itemY = currentY + subMenuHeight - subMenuItemHeight
        val items = submenu.items
        val itemWidth = (Game.screenWidth - leftMargin - rightMargin - subMenuWidth) / items.size
        items.forEach { item ->
            val currentX = leftMargin + subMenuWidth + submenu.items.indexOf(item) * itemWidth
            g.drawString(item.title, currentX, itemY)
            if (submenu.isCurrent(item)) {
                g.drawLine(currentX, itemY + subMenuItemHeight + lineWidth,
                        currentX + itemWidth - 5 * lineWidth, itemY + subMenuItemHeight + lineWidth)
            }
        }
    }

    @Throws(SlickException::class)
    override fun update(container: GameContainer, game: StateBasedGame, delta: Int) {

    }

    @Throws(SlickException::class)
    override fun leave(container: GameContainer?, game: StateBasedGame?) {
        super.leave(container, game)
        listeners.forEach { container!!.input.removeKeyListener(it) }
        GameSettings.instance.save()
    }

    private fun updateGameSettings() {
        GameSettings.instance.track = trackMenu.current
        GameSettings.instance.mode = modeMenu.current
        GameSettings.instance.debug = debugMenu.current
        GameSettings.instance.collisions = collisionsMenu.current
    }

    @Throws(SlickException::class)
    override fun enter(container: GameContainer?, game: StateBasedGame?) {
        super.enter(container, game)
        trackMenu.current = GameSettings.instance.track
        modeMenu.current = GameSettings.instance.mode
        debugMenu.current = GameSettings.instance.debug
        collisionsMenu.current = GameSettings.instance.collisions
        listeners.forEach { listener -> container!!.input.addKeyListener(listener) }

    }


}
