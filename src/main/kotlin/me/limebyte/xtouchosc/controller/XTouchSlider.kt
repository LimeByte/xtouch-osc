package me.limebyte.xtouchosc.controller

import javax.sound.midi.MidiMessage
import javax.sound.midi.ShortMessage
import kotlin.math.roundToInt


class XTouchSlider(
    val id: Int,
    private val sendMessage: (MidiMessage) -> Unit
) {

    var scribbleTextTop = ""
        private set

    var scribbleTextBottom = ""
        private set

    companion object {
        const val CHARACTERS_PER_SCRIBBLE = 7
    }

    fun setScribbleTop(text: String, textAlign: Alignment = Alignment.LEFT) = updateLabel(text = text, row = 0, textAlign = textAlign)
    fun setScribbleBottom(text: String, textAlign: Alignment = Alignment.LEFT) = updateLabel(text = text, row = 1, textAlign = textAlign)

    fun clearScribble() {
        updateLabel(text = "", row = 0, textAlign = Alignment.LEFT)
        updateLabel(text = "", row = 1, textAlign = Alignment.LEFT)
    }

    fun setSlider(value: Float) {
        setSliderPosition(value = value)
    }

    private fun updateLabel(text: String, row: Int, textAlign: Alignment) {
        val correctLength = text.take(CHARACTERS_PER_SCRIBBLE)

        val paddedText = when (textAlign) {
            Alignment.LEFT -> correctLength.padEnd(CHARACTERS_PER_SCRIBBLE)
            Alignment.RIGHT -> correctLength.padStart(CHARACTERS_PER_SCRIBBLE)
        }

        when (row) {
            0 -> scribbleTextTop = paddedText
            1 -> scribbleTextBottom = paddedText
        }
    }

    /**
     * Sets the slider position.
     *
     * @param value 0 to 15748
     */
    private fun setSliderPosition(value: Float) {
        val intValue = (value * 16383).roundToInt()
        val course = intValue / 129
        val fine = intValue % 128

        setScribbleTop(text = course.toString(), textAlign = Alignment.RIGHT)
        setScribbleBottom(text = fine.toString(), textAlign = Alignment.RIGHT)

        val message = ShortMessage()
        message.setMessage(ShortMessage.PITCH_BEND, id, fine, course)
        sendMessage(message)
    }

    enum class Alignment {
        LEFT, RIGHT
    }

}
