package fr.mkdirs.pixelar.gui.filter

import fr.mkdirs.pixelar.WINDOW
import fr.mkdirs.pixelar.gui.History
import fr.mkdirs.pixelar.gui.PixelarWindow
import java.awt.Color
import java.awt.Dimension
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.*

class PixelateFilter : PixelarFilter {

    override fun apply(image: BufferedImage): BufferedImage {
        val size = askParameters(WINDOW,"size")["size"] as Int
        fun chunk(x:Int, y:Int) : Map<Array<Int>, Int>{
            val chunk = mutableMapOf<Array<Int>, Int>()
            val xSize = if(x+size > image.width-1) (image.width-1)-x else x+size
            val ySize = if(y+size > image.height-1) (image.height-1)-y else y+size

            for(tileY in y..ySize){
                for(tileX in x..xSize){
                    chunk[arrayOf(tileX, tileY)] = image.getRGB(tileX,tileY)
                }
            }

            return chunk
        }
        fun average(colors:List<Color>): Color {
            var red = 0; var green = 0; var blue = 0; var alpha = 0
            for(color in colors){
                red+=color.red
                green+=color.green
                blue+=color.blue
                alpha+=color.alpha
            }
            if(colors.isNotEmpty()){
                red/=colors.size
                green/=colors.size
                blue/=colors.size
                alpha/=colors.size
            }

            return Color(red, green, blue, alpha)
        }

        if(size != 0){
            for(y in 0 until image.height step size){
                for(x in 0 until image.width step size){
                    val chunk = chunk(x,y)
                    var averageColor = average(chunk.map { Color(it.value, true) })

                    for(pixel in chunk.keys){
                        image.setRGB(pixel[0], pixel[1], averageColor.rgb)
                    }
                }
            }
        }


        return image
    }

    override fun apply(input: InputStream): BufferedImage {
        return apply(ImageIO.read(input))
    }

    override fun askParameters(owner:Frame, vararg parameters: String): Map<String, Any> {
        val pan = JPanel()
        val label = JLabel("Size")
        val slider = JSlider(1, 10)
        slider.labelTable = slider.createStandardLabels(1, 1)
        slider.paintLabels = true
        pan.add(label)
        pan.add(slider)
        val option = JOptionPane.showConfirmDialog(owner, pan,  "Pixelate Parameters", JOptionPane.OK_CANCEL_OPTION)

        return when(option){
            JOptionPane.OK_OPTION -> mapOf(Pair("size", slider.value))
            else -> mapOf(Pair("size", 0))
        }
    }
}