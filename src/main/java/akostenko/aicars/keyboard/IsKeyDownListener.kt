package akostenko.aicars.keyboard

import org.newdawn.slick.Input
import org.newdawn.slick.KeyListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IsKeyDownListener(private val key: Int) : KeyListener {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    var isDown = false
        private set

    override fun keyPressed(key: Int, c: Char) {
        if (this.key == key) {
            isDown = true
            logger.debug("Key is down {}", key)
        }
    }

    override fun keyReleased(key: Int, c: Char) {
        if (this.key == key) {
            isDown = false
            logger.debug("Key is up {}", key)
        }
    }

    override fun setInput(input: Input) {

    }

    override fun isAcceptingInput(): Boolean {
        return true
    }

    override fun inputEnded() {

    }

    override fun inputStarted() {

    }
}
