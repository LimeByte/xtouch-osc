package me.limebyte.xtouchosc.util

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.Transmitter

fun MidiDevice.getReceiverOrNull(): Receiver? {
    return try {
        receiver
    } catch (exception: MidiUnavailableException) {
        null
    }
}

fun MidiDevice.getTransmitterOrNull(): Transmitter? {
    return try {
        transmitter
    } catch (exception: MidiUnavailableException) {
        null
    }
}
