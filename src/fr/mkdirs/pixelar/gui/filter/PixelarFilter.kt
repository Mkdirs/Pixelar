package fr.mkdirs.pixelar.gui.filter

import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.InputStream

interface PixelarFilter {
    fun apply(image:BufferedImage):BufferedImage
    fun apply(input:InputStream):BufferedImage
    fun askParameters(owner:Frame, vararg parameters:String):Map<String, Any>
}