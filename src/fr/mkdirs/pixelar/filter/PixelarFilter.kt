package fr.mkdirs.pixelar.filter

import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.InputStream

/**
 * An interface representing a filter/effect.
 * @author Mkdirs.
 */
interface PixelarFilter {
    /**
     * Applies this filter on an image.
     * @param image The image that will receive the filter.
     * @return The image with this filter applied on.
     * @author Mkdirs.
     */
    fun apply(image:BufferedImage):BufferedImage

    /**
     * @param input The InputStream of the target image.
     * @see PixelarFilter.apply
     * @author Mkdirs.
     */
    fun apply(input:InputStream):BufferedImage

    /**
     * Opens a pop-up containing the parameters needed by this filter.
     * @param owner The frame parent of the pop-up.
     * @param parameters The parameters names.
     * @return A Map of the parameters values by their names.
     * @author Mkdirs.
     */
    fun askParameters(owner:Frame, vararg parameters:String):Map<String, Any>
}