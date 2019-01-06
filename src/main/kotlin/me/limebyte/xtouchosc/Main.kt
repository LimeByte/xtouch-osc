package me.limebyte.xtouchosc

import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCPortIn
import com.illposed.osc.OSCPortOut
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.Slider
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import me.limebyte.xtouchosc.util.getReceiverOrNull
import me.limebyte.xtouchosc.util.getTransmitterOrNull
import me.limebyte.xtouchosc.util.loadFxml
import java.net.InetAddress
import javax.sound.midi.*
import javax.sound.midi.SysexMessage




class Main : Application() {

    private lateinit var client: OSCPortIn
    private lateinit var server: OSCPortOut
    private val openDevices = mutableListOf<MidiDevice>()

    private var transmitter: Transmitter? = null
    private var receiver: Receiver? = null

    companion object {
        fun main(args: Array<String>) {
            launch(*args)
        }
    }

    override fun start(primaryStage: Stage) {
        val btn = Button()
        btn.text = "Say 'X-Touch'"

        btn.setOnAction {
            println("X-Touch!")
        }

        val root = javaClass.loadFxml<AnchorPane>(name = "main")
        val scene = Scene(root, 300.0, 250.0)

        primaryStage.title = "Hello FXML!"
        primaryStage.scene = scene
        primaryStage.show()

        val slider = scene.lookup("#slider") as Slider
        val progress = scene.lookup("#progress") as ProgressBar
        slider.min = 0.0
        slider.max = 1.0

        // Use "//" for all messages
        client = OSCPortIn(7001)
        client.addListener("/composition/layers/5/video/opacity") { time, message ->
            println("[$time] ${message.address} ${message.arguments}")

            val opacity = message.arguments.firstOrNull() as? Float
            opacity?.let {
                progress.progress = it.toDouble()
            }
        }
        client.startListening()

        server = OSCPortOut(InetAddress.getLocalHost(), 7000)

        slider.valueProperty().addListener { observable, oldValue, newValue ->
            val packet = OSCMessage("/composition/layers/5/video/opacity", listOf(newValue.toFloat()))
            server.send(packet)
        }

        connectXTouch()

        for (i in 1..8) {
            updateLabel(title = "Top    ", column = i, row = 1)
            updateLabel(title = "Bottom ", column = i, row = 2)
        }
    }

    private fun connectXTouch() {
        val devices = MidiSystem.getMidiDeviceInfo().filter { it.name == "X-Touch-Ext" }
        devices.forEach {
            println("${it.name} - ${it.description}")

            try {
                val device = MidiSystem.getMidiDevice(it)

                if (this.transmitter == null) {
                    device.getTransmitterOrNull()?.let { transmitter ->
                        device.open()
                        openDevices.add(device)
                        this.transmitter = transmitter
                    }
                } else if (this.receiver == null) {
                    device.getReceiverOrNull()?.let { receiver ->
                        device.open()
                        openDevices.add(device)
                        this.receiver = receiver
                    }
                }
            } catch (exception: MidiUnavailableException) {
                exception.printStackTrace()
            }
        }
    }

    private fun updateLabel(column: Int = 5, row: Int = 2, title: String = "Quest  ") {
        val prefix = 0xf0.toByte()
        val header = byteArrayOf(0x00, 0x00, 0x66, 0x15, 0x12)
        val suffix = 0xf7.toByte()

        val charactersPerStrip = 7
        val totalColumns = 8
        val rowOffset = (row - 1) * totalColumns
        val position = (column - 1 + rowOffset) * charactersPerStrip

        val label = title.toByteArray()

        val message = byteArrayOf(prefix, *header, position.toByte(), *label, suffix)

        val sysMsg = SysexMessage()
        sysMsg.setMessage(message, message.size)

        receiver?.send(sysMsg, -1)
    }

    override fun stop() {
        super.stop()
        client.stopListening()

        openDevices.forEach {
            it.close()
        }

        openDevices.clear()
    }
}
