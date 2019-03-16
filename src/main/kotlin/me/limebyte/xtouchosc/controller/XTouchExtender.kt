package me.limebyte.xtouchosc.controller

import javax.sound.midi.*

class XTouchExtender(
    private val transmitter: Transmitter,
    private val receiver: Receiver
) : Controller {

    companion object {
        private const val SLIDER_COUNT = 8
    }

    val sliders = (0 until SLIDER_COUNT).map {
        it to XTouchSlider(
            id = it,
            totalSliders = SLIDER_COUNT,
            sendMessage = ::sendMessage
        )
    }.toMap()

    private fun sendMessage(message: MidiMessage) = receiver.send(message, -1)

}
