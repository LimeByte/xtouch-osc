package me.limebyte.xtouchosc.util

import javafx.fxml.FXMLLoader

fun <ParentType> Class<*>.loadFxml(name: String): ParentType {
    val url = classLoader.getResource("$name.fxml")
    return FXMLLoader.load<ParentType>(url)
}
