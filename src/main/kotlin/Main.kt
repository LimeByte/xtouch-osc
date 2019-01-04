import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class Main : Application() {

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

        val root = StackPane()
        root.children.add(btn)

        val scene = Scene(root, 300.0, 250.0)

        primaryStage.title = "Hello World!"
        primaryStage.scene = scene
        primaryStage.show()
    }
}
