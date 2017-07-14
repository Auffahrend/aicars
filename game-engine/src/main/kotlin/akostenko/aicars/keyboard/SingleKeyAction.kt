package main.kotlin.akostenko.aicars.keyboard

import org.newdawn.slick.Input
import org.newdawn.slick.KeyListener

import java.util.function.Consumer

class SingleKeyAction(private val action: () -> Unit, private val key: Int) : KeyListener {

    override fun keyPressed(key: Int, c: Char) {
        if (this.key == key) {
            action()
        }
    }

    override fun keyReleased(key: Int, c: Char) {

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
