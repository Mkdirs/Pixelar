package fr.mkdirs.pixelar

import fr.mkdirs.pixelar.gui.PixelarWindow
import fr.mkdirs.pixelar.gui.filter.PixelateFilter
import fr.mkdirs.pixelar.gui.filter.UncolorizeFilter
import java.io.File

val WINDOW = PixelarWindow(arrayOf(UncolorizeFilter(), PixelateFilter()))
val ROOT_DIR = File("${System.getProperty("user.home")}\\AppData\\Roaming\\.Pixelar")
val HISTORY_DIR = File(ROOT_DIR, "history")

fun main(args:Array<String>){
    if(!ROOT_DIR.exists())
        ROOT_DIR.mkdir()
    if(!HISTORY_DIR.exists())
        HISTORY_DIR.mkdir()

    WINDOW.initialize()
}