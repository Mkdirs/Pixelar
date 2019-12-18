package fr.mkdirs.pixelar.gui

import fr.mkdirs.pixelar.gui.filter.PixelarFilter
import fr.mkdirs.pixelar.gui.filter.PixelateFilter
import fr.mkdirs.pixelar.gui.filter.UncolorizeFilter
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class PixelarWindow(val filters:Array<PixelarFilter>) : JFrame() {
    private val menuBar = JMenuBar()
    private val fileMenu = JMenu("File")
    private val effectsMenu = JMenu("Effects")
    private val label = JLabel(ImageIcon(ImageIO.read(this.javaClass.getResourceAsStream("/img/pixelar_icon.png"))))
    private var fileStream = this.javaClass.getResourceAsStream("/img/pixelar_icon.png")

    companion object{
        var tempFile:File? = null

        fun createTempFileFrom(image:BufferedImage, prefix:String, suffix:String=".png"){
            tempFile = File.createTempFile(prefix, suffix)
            tempFile?.deleteOnExit()
            ImageIO.write(image, "PNG", tempFile)
        }
    }

    init{
        this.iconImage = ImageIO.read(fileStream)
        fileStream = this.javaClass.getResourceAsStream("/img/pixelar_icon.png")
        this.title = "Pixelar"
        this.size = Dimension(1000, 720)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.extendedState = JFrame.MAXIMIZED_BOTH

        initMenus()
        menuBar.add(fileMenu)
        menuBar.add(effectsMenu)

        val center = JPanel(FlowLayout(FlowLayout.CENTER))
        center.add(label)
        center.background = Color.decode("#333333")

        val scroll = JScrollPane(center)

        this.contentPane.add(menuBar, BorderLayout.NORTH)
        this.contentPane.add(scroll)

    }

    fun initialize() {this.isVisible = true}

    inline fun <reified T:PixelarFilter> getFilter() = filters.find { it is T}

    private fun initMenus(){
        menuBar.background = Color.DARK_GRAY

        fun initFileMenu(){
            fileMenu.foreground = Color.WHITE
            val openFileMenu = JMenuItem("Open")
            openFileMenu.addActionListener{ showImage(openFile()) }
            openFileMenu.foreground = fileMenu.foreground
            openFileMenu.background = menuBar.background

            val saveFileMenu = JMenuItem("Save As")
            saveFileMenu.addActionListener{saveFile()}
            saveFileMenu.foreground = fileMenu.foreground
            saveFileMenu.background = menuBar.background
            //saveFileMenu.isEnabled = false

            fileMenu.add(openFileMenu)
            fileMenu.add(saveFileMenu)
        }

        fun initEffectsMenu(){
            effectsMenu.foreground = Color.WHITE
            val uncolorizeMenu = JMenuItem("Black And White")
            uncolorizeMenu.addActionListener{
                getFilter<UncolorizeFilter>()!!.apply(fileStream)
                showImage()
            }
            uncolorizeMenu.foreground = effectsMenu.foreground
            uncolorizeMenu.background = menuBar.background

            val pixelateMenu = JMenuItem("Pixelate")
            pixelateMenu.addActionListener{
                getFilter<PixelateFilter>()!!.apply(fileStream)
                showImage()
            }
            pixelateMenu.foreground = effectsMenu.foreground
            pixelateMenu.background = menuBar.background

            effectsMenu.add(uncolorizeMenu)
            effectsMenu.add(pixelateMenu)
            //effectsMenu.isEnabled = false
        }

        initFileMenu()
        initEffectsMenu()
    }

    private fun showImage(file:File?){
        if(file != null){label.icon = ImageIcon(file.absolutePath); fileStream = file.inputStream()}
    }

    private fun showImage() = showImage(tempFile)


    private fun openFile() : File?{
        val chooser = JFileChooser()
        val filter = FileNameExtensionFilter("Images (*.JPG, *.PNG)", "jpg", "jpeg", "png")
        chooser.fileFilter = filter
        chooser.dialogTitle = "Open a file"
        when(chooser.showOpenDialog(this)){
            JFileChooser.APPROVE_OPTION -> {
                //fileMenu.getItem(1).isEnabled = true
                //effectsMenu.isEnabled = true
                return chooser.selectedFile
            }
            JFileChooser.CANCEL_OPTION -> return null
        }

        return null
    }

    private fun saveFile(){
        val chooser = JFileChooser()
        chooser.removeChoosableFileFilter(chooser.acceptAllFileFilter)
        chooser.addChoosableFileFilter(FileNameExtensionFilter("Image *.PNG", "png"))
        chooser.addChoosableFileFilter(FileNameExtensionFilter("Image *.JPG", "jpg", "jpeg"))
        chooser.dialogTitle = "Save a file"
        when(chooser.showSaveDialog(this)){
            JFileChooser.APPROVE_OPTION -> {
                println("Saving file")
                val file = File(chooser.selectedFile.absolutePath+"."+chooser.fileFilter.description.substring(8).toLowerCase())
                file.createNewFile()
                ImageIO.write(ImageIO.read(fileStream), chooser.fileFilter.description.substring(8), file)
                println("Saved ${file.absoluteFile}")
                fileStream = tempFile?.inputStream()

            }
            JFileChooser.CANCEL_OPTION -> println("Saving file canceled")
            JFileChooser.ERROR_OPTION -> println("An error occurred")
        }
    }


}