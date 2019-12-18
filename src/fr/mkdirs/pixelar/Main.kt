package fr.mkdirs.pixelar

import fr.mkdirs.pixelar.gui.PixelarWindow
import fr.mkdirs.pixelar.gui.filter.PixelateFilter
import fr.mkdirs.pixelar.gui.filter.UncolorizeFilter

val WINDOW = PixelarWindow(arrayOf(UncolorizeFilter(), PixelateFilter()))

fun main(args:Array<String>){
    WINDOW.initialize()
}