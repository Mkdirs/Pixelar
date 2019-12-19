package fr.mkdirs.pixelar.gui.filter

import fr.mkdirs.pixelar.gui.History
import fr.mkdirs.pixelar.gui.PixelarWindow
import java.awt.Color
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

class UncolorizeFilter : PixelarFilter {
    override fun apply(image: BufferedImage): BufferedImage {
        for(y in 0 until image.height){
            for(x in 0 until image.width){
                val pixel = Color(image.getRGB(x,y), true)
                val tint = (pixel.red + pixel.green + pixel.blue) / 3
                image.setRGB(x, y, Color(tint, tint, tint, pixel.alpha).rgb)
            }
        }
        return image
    }

    override fun askParameters(owner:Frame, vararg parameters: String): Map<String, Any> {
        return emptyMap()
    }

    override fun apply(input: InputStream): BufferedImage {
        return apply(ImageIO.read(input))
    }

}