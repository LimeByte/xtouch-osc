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
import me.limebyte.xtouchosc.controller.XTouchExtender
import me.limebyte.xtouchosc.util.getReceiverOrNull
import me.limebyte.xtouchosc.util.getTransmitterOrNull
import me.limebyte.xtouchosc.util.loadFxml
import java.net.InetAddress
import javax.sound.midi.*


class Main : Application() {

    private var client: OSCPortIn? = null
    private lateinit var server: OSCPortOut

    private var xTouchExtender: XTouchExtender? = null
    private val connectedDevices = mutableListOf<MidiDevice>()

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
        // TODO client = OSCPortIn(7001)
        client?.addListener("/composition/layers/5/video/opacity") { time, message ->
            println("[$time] ${message.address} ${message.arguments}")

            val opacity = message.arguments.firstOrNull() as? Float
            opacity?.let {
                progress.progress = it.toDouble()
            }
        }
        client?.startListening()

        server = OSCPortOut(InetAddress.getLocalHost(), 7000)

        slider.valueProperty().addListener { observable, oldValue, newValue ->
            val packet = OSCMessage("/composition/layers/5/video/opacity", listOf(newValue.toFloat()))
            server.send(packet)
        }

        connectXTouch()

        xTouchExtender?.let { xTouchExtender ->
            xTouchExtender.sliders.values.forEach {
                it.setScribbleTop("Hello ${it.id}")
                it.setScribbleBottom("Labels ")
                it.testVolume()
            }

            client?.addListener("/composition/layers/?/name") { time, message ->
                val name = message.arguments.first().toString()
                println("${message.address} $name")
                // TODO updateLabel(column = 1, row = 1, title = name)
            }
        }
    }

    private fun connectXTouch() = findTransmitterAndReceiver(name = "X-Touch-Ext")

    private fun findTransmitter(devices: List<MidiDevice.Info>): Pair<MidiDevice, Transmitter>? {
        devices.forEach { info ->
            try {
                val device = MidiSystem.getMidiDevice(info)

                device.getTransmitterOrNull()?.let {
                    return Pair(device, it)
                }

            } catch (exception: MidiUnavailableException) {
                exception.printStackTrace()
            }
        }

        return null
    }

    private fun findReceiver(devices: List<MidiDevice.Info>): Pair<MidiDevice, Receiver>? {
        devices.forEach { info ->
            try {
                val device = MidiSystem.getMidiDevice(info)

                device.getReceiverOrNull()?.let {
                    return Pair(device, it)
                }

            } catch (exception: MidiUnavailableException) {
                exception.printStackTrace()
            }
        }

        return null
    }

    private fun findTransmitterAndReceiver(name: String) {
        val devices = MidiSystem.getMidiDeviceInfo().filter { it.name == name }
        val transmitterPair = findTransmitter(devices = devices)
        val receiverPair = findReceiver(devices = devices)

        if (transmitterPair == null || receiverPair == null) {
            println("Unable to connect to X-Touch")
        } else {
            val (transmitterDevice, transmitter) = transmitterPair
            val (receiverDevice, receiver) = receiverPair

            transmitterDevice.open()
            receiverDevice.open()

            connectedDevices.add(transmitterDevice)
            connectedDevices.add(receiverDevice)

            xTouchExtender = XTouchExtender(
                transmitter = transmitter,
                receiver = receiver
            )
        }
    }

    override fun stop() {
        super.stop()
        client?.stopListening()

        connectedDevices.forEach {
            it.close()
        }

        connectedDevices.clear()
    }
}
