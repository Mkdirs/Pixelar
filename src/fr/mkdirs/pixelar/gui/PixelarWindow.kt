package fr.mkdirs.pixelar.gui

import fr.mkdirs.pixelar.HISTORY_DIR
import fr.mkdirs.pixelar.filter.PixelarFilter
import fr.mkdirs.pixelar.filter.PixelateFilter
import fr.mkdirs.pixelar.filter.UncolorizeFilter
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * The main window of the program.
 * @param filters The list of filters.
 * @author Mkdirs.
 */
class PixelarWindow(val filters:Array<PixelarFilter>) : JFrame() {
    private val menuBar = JMenuBar()
    private val fileMenu = JMenu("File")
    private val effectsMenu = JMenu("Effects")
    private val label = JLabel()
    private var fileStream = this.javaClass.getResourceAsStream("/img/pixelar_icon.png")
    private val historyPanel = JPanel(FlowLayout(FlowLayout.LEFT))


    companion object{
        val HISTORY = History()
    }

    init{
        this.iconImage = ImageIO.read(fileStream)
        fileStream = this.javaClass.getResourceAsStream("/img/pixelar_icon.png")
        this.title = "Pixelar"
        this.size = Dimension(1000, 720)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.extendedState = JFrame.MAXIMIZED_BOTH

        class KeyListenerImpl : KeyListener{
            override fun keyTyped(e: KeyEvent?) {

            }

            override fun keyPressed(e: KeyEvent?) {

            }

            override fun keyReleased(e: KeyEvent?) {
                if(e!!.keyCode == KeyEvent.VK_Z && e!!.isControlDown || e!!.keyCode == KeyEvent.VK_LEFT){
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                    HISTORY.currentKey = if(HISTORY.keys.indexOf(HISTORY.currentKey)-1 != -1) HISTORY.keys[HISTORY.keys.indexOf(HISTORY.currentKey)-1] else HISTORY.keys[0]
                    showImage()
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
                }else if(e!!.keyCode == KeyEvent.VK_RIGHT){
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                    HISTORY.currentKey = if(HISTORY.keys.indexOf(HISTORY.currentKey)+1 != HISTORY.keys.size) HISTORY.keys[HISTORY.keys.indexOf(HISTORY.currentKey)+1] else HISTORY.keys[HISTORY.keys.size-1]
                    showImage()
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
                }
            }
        }

        class WindowListenerImpl : WindowListener{
            override fun windowDeiconified(e: WindowEvent?) {

            }

            override fun windowClosing(e: WindowEvent?) {
                fileStream.close()
                HISTORY.keys.forEach{ HISTORY.removeFile(it)}
                HISTORY.keys.clear()
            }

            override fun windowClosed(e: WindowEvent?) {

            }

            override fun windowActivated(e: WindowEvent?) {

            }

            override fun windowDeactivated(e: WindowEvent?) {

            }

            override fun windowOpened(e: WindowEvent?) {

            }

            override fun windowIconified(e: WindowEvent?) {

            }
        }

        this.addKeyListener(KeyListenerImpl())
        this.addWindowListener(WindowListenerImpl())

        initMenus()
        menuBar.add(fileMenu)
        menuBar.add(effectsMenu)

        historyPanel.background = Color.DARK_GRAY
        val historyScroll = JScrollPane(historyPanel)
        historyScroll.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER

        val center = JPanel(FlowLayout(FlowLayout.CENTER))
        center.add(label)
        center.background = Color.decode("#333333")

        val centerScroll = JScrollPane(center)

        this.contentPane.add(menuBar, BorderLayout.NORTH)
        this.contentPane.add(historyScroll, BorderLayout.SOUTH)
        this.contentPane.add(centerScroll)

    }

    /**
     * Initializes and shows the window with the default image.
     * @author Mkdirs.
     */
    fun initialize() {
        HISTORY.createFile(HISTORY.currentKey)
        showImage()
        addHistoryKeyUI()
        this.isVisible = true
    }

    /**
     * @param T The type of the wanted filter.
     * @return The filter matching with T.
     * @author Mkdirs.
     * @see PixelarFilter
     */
    private inline fun <reified T:PixelarFilter> getFilter() = filters.find { it is T}

    /**
     * Initializes the menus.
     * @author Mkdirs.
     */
    private fun initMenus(){
        menuBar.background = Color.DARK_GRAY

        //Initializes the file menu.
        fun initFileMenu(){
            fileMenu.foreground = Color.WHITE
            val openFileMenu = JMenuItem("Open")
            openFileMenu.addActionListener{
                val file = openFile()
                if(file != null){
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                    val key = History.Key(ImageIO.read(file))
                    HISTORY.keys.add(key)
                    HISTORY.currentKey = key
                    HISTORY.createFile(key)
                    showImage()
                    addHistoryKeyUI()
                    (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
                }

            }
            openFileMenu.foreground = fileMenu.foreground
            openFileMenu.background = menuBar.background

            val saveFileMenu = JMenuItem("Save As")
            saveFileMenu.addActionListener{saveFile()}
            saveFileMenu.foreground = fileMenu.foreground
            saveFileMenu.background = menuBar.background

            fileMenu.add(openFileMenu)
            fileMenu.add(saveFileMenu)
        }

        //Initializes the effects/filters menu.
        fun initEffectsMenu(){
            effectsMenu.foreground = Color.WHITE
            val uncolorizeMenu = JMenuItem("Black And White")
            uncolorizeMenu.addActionListener{
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                val key = History.Key(getFilter<UncolorizeFilter>()!!.apply(fileStream))
                HISTORY.keys.add(key)
                HISTORY.currentKey = key
                HISTORY.createFile(key)
                showImage()
                addHistoryKeyUI()
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
            }
            uncolorizeMenu.foreground = effectsMenu.foreground
            uncolorizeMenu.background = menuBar.background

            val pixelateMenu = JMenuItem("Pixelate")
            pixelateMenu.addActionListener{
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                val key = History.Key(getFilter<PixelateFilter>()!!.apply(fileStream))
                HISTORY.keys.add(key)
                HISTORY.currentKey = key
                HISTORY.createFile(key)
                showImage()
                addHistoryKeyUI()
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
            }
            pixelateMenu.foreground = effectsMenu.foreground
            pixelateMenu.background = menuBar.background

            effectsMenu.add(uncolorizeMenu)
            effectsMenu.add(pixelateMenu)
        }

        initFileMenu()
        initEffectsMenu()
    }


    /**
     * Shows the current image.
     * @author Mkdirs.
     */
    private fun showImage() {
        fileStream.close()
        label.icon = ImageIcon(HISTORY.getFile(HISTORY.currentKey)!!.absolutePath)
        fileStream = HISTORY.getFile(HISTORY.currentKey)!!.inputStream()

    }


    /**
     * Adds graphic representation of a history key.
     * @see History.Key
     * @author Mkdirs.
     */
    private fun addHistoryKeyUI(){

        class MouseListenerImpl : MouseListener{
            override fun mouseReleased(e: MouseEvent?) {

            }

            override fun mouseEntered(e: MouseEvent?) {

            }

            override fun mouseClicked(e: MouseEvent?) {
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = ""
                HISTORY.currentKey = HISTORY.keys[historyPanel.components.indexOf(e!!.component)]
                (historyPanel.getComponent(HISTORY.keys.indexOf(HISTORY.currentKey)) as JLabel).text = "<"
                showImage()
            }

            override fun mouseExited(e: MouseEvent?) {

            }

            override fun mousePressed(e: MouseEvent?) {

            }
        }

        val keyLabel = JLabel("<",label.icon,SwingConstants.CENTER)
        keyLabel.preferredSize = Dimension(140,128)
        keyLabel.foreground = Color.YELLOW
        keyLabel.addMouseListener(MouseListenerImpl())
        historyPanel.add(keyLabel)

        if(historyPanel.componentCount-1 != 0){
            (historyPanel.getComponent(historyPanel.componentCount-2) as JLabel).text = ""
        }


        historyPanel.revalidate()
        historyPanel.repaint()
    }


    /**
     * Opens the open file dialog.
     * @return The choosen file or null if no file has been selected.
     * @author Mkdirs.
     */
    private fun openFile() : File?{
        val chooser = JFileChooser()
        val filter = FileNameExtensionFilter("Images (*.JPG, *.PNG)", "jpg", "jpeg", "png")
        chooser.fileFilter = filter
        chooser.dialogTitle = "Open a file"
        when(chooser.showOpenDialog(this)){
            JFileChooser.APPROVE_OPTION -> return chooser.selectedFile
            JFileChooser.CANCEL_OPTION -> return null
        }

        return null
    }

    /**
     * Opens the save file dialog.
     * @author Mkdirs.
     */
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
                fileStream = HISTORY.getFile(HISTORY.currentKey)!!.inputStream()

            }
            JFileChooser.CANCEL_OPTION -> println("Saving file canceled")
            JFileChooser.ERROR_OPTION -> println("An error occurred")
        }
    }


}

/**
 * The history of the program.
 * @author Mkdirs.
 * @see Key
 */
class History{
    /**
     * A key of the history.
     * @param image The image saved in this Key.
     * @author Mkdirs.
     * @see History
     */
    class Key(val image:BufferedImage){}
    val keys = mutableListOf<Key>()
    var currentKey = Key(ImageIO.read(this.javaClass.getResourceAsStream("/img/pixelar_icon.png")))
    init{
        keys.add(currentKey)
    }

    /**
     * Creates a file from the image of the given Key.
     * @param key The Key containing the image to be saved.
     * @return true if the file has been created successfully, else otherwise.
     * @author Mkdirs.
     * @see Key
     */
    fun createFile(key:Key):Boolean{
        if(!keys.contains(key))
            return false

        val file = File(HISTORY_DIR, "img_${keys.indexOf(key)}.png")
        try{
            file.createNewFile()
            ImageIO.write(key.image, "PNG", file)
        }catch(e:IOException){
            if(file.exists())
                file.delete()

            return false
        }


        return true
    }

    /**
     * Deletes the file matching with the given Key.
     * @param key The key matching with the file to be deleted.
     * @author Mkdirs.
     * @return true if the file has been deleted successfully, else otherwise.
     * @see Key
     */
    fun removeFile(key:Key):Boolean{
        if(!keys.contains(key))
            return false

        val file = HISTORY_DIR.listFiles().find { it.name.replace(".png", "").substring(4).toInt() == keys.indexOf(key) }
            ?: return false


        return file.delete()
    }

    /**
     * @param key The key matching with the wanted file.
     * @return The file matching the given Key or null.
     * @author Mkdirs.
     * @see Key
     */
    fun getFile(key:Key) = HISTORY_DIR.listFiles().find { it.name.replace(".png", "").substring(4).toInt() == keys.indexOf(key) }

}