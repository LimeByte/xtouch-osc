package me.limebyte.xtouchosc.controller

import java.lang.IllegalArgumentException
import javax.sound.midi.*

class XTouchExtender(
    private val transmitter: Transmitter,
    private val receiver: Receiver
) : Controller {

    companion object {
        private const val SLIDER_COUNT = 8
        private val updateScribbleHeader = byteArrayOf(0x00, 0x00, 0x66, 0x15, 0x12)
    }

    val sliders = (0 until SLIDER_COUNT).map {
        it to XTouchSlider(
            id = it,
            sendMessage = ::sendMessage
        )
    }.toMap()

    fun drawDisplay() {
        sliders.forEach { id, slider ->
            updateLabel(text = slider.scribbleTextTop, slider = id, row = 0)
            updateLabel(text = slider.scribbleTextBottom, slider = id, row = 1)
        }
    }

    private fun updateLabel(text: String, slider: Int, row: Int) {
        if (text.length == XTouchSlider.CHARACTERS_PER_SCRIBBLE) {
            val rowOffset = row * SLIDER_COUNT
            val position = (slider + rowOffset) * XTouchSlider.CHARACTERS_PER_SCRIBBLE
            sendSysex(message = byteArrayOf(*updateScribbleHeader, position.toByte(), *text.toByteArray()))
        } else {
            throw IllegalArgumentException("Title must be exactly $XTouchSlider.CHARACTERS_PER_SCRIBBLE characters.")
        }
    }

    private fun sendSysex(message: ByteArray) {
        val prefix = 0xf0.toByte()
        val suffix = 0xf7.toByte()
        val appendedMessage = byteArrayOf(prefix, *message, suffix)

        val sysMsg = SysexMessage()
        sysMsg.setMessage(appendedMessage, appendedMessage.size)
        sendMessage(sysMsg)
    }

    private fun sendMessage(message: MidiMessage) = receiver.send(message, System.nanoTime() / 1000)

}
