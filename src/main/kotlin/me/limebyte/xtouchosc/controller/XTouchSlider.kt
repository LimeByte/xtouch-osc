package me.limebyte.xtouchosc.controller

import java.lang.IllegalArgumentException
import javax.sound.midi.MidiMessage
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage


class XTouchSlider(
    val id: Int,
    private val totalSliders: Int,
    private val sendMessage: (MidiMessage) -> Unit
) {

    companion object {
        private const val CHARACTERS_PER_SCRIBBLE = 7
        private val updateScribbleHeader = byteArrayOf(0x00, 0x00, 0x66, 0x15, 0x12)
    }

    fun setScribbleTop(text: String) = updateLabel(text = text, row = 0)
    fun setScribbleBottom(text: String) = updateLabel(text = text, row = 1)

    fun clearScribble() {
        setScribbleTop(text = "")
        setScribbleBottom(text = "")
    }

    fun testVolume() = sendVolumeChange(0x70, 0x7f)

    private fun updateLabel(text: String, row: Int) {
        if (text.length == CHARACTERS_PER_SCRIBBLE) {
            val rowOffset = row * totalSliders
            val position = (id + rowOffset) * CHARACTERS_PER_SCRIBBLE
            sendSysex(message = byteArrayOf(*updateScribbleHeader, position.toByte(), *text.toByteArray()))
        } else {
            throw IllegalArgumentException("Title must be exactly $CHARACTERS_PER_SCRIBBLE characters.")
        }
    }

    private fun sendVolumeChange(data1: Int, data2: Int) {
        val message = ShortMessage()
        message.setMessage(ShortMessage.PITCH_BEND, id, data1, data2)
        sendMessage(message)
    }

    private fun sendSysex(message: ByteArray) {
        val prefix = 0xf0.toByte()
        val suffix = 0xf7.toByte()
        val appendedMessage = byteArrayOf(prefix, *message, suffix)

        val sysMsg = SysexMessage()
        sysMsg.setMessage(appendedMessage, appendedMessage.size)
        sendMessage(sysMsg)
    }
}
