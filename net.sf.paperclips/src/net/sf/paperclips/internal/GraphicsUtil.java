package net.sf.paperclips.internal;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Convenience methods for working with SWT graphics objects.
 * @author Matthew Hall
 */
public class GraphicsUtil {
  private GraphicsUtil() {} // no instances

  /**
   * Returns a defensive copy of the passed in FontData.
   * @param fontData the FontData to copy. May be null.
   * @return a copy of the passed in FontData, or null if the argument was null.
   */
  public static FontData defensiveCopy( FontData fontData ) {
    return fontData == null ? null : new FontData( fontData.getName(),
                                                   fontData.getHeight(),
                                                   fontData.getStyle() );
  }

  /**
   * Returns a defensive copy of the passed in RGB.
   * @param rgb the RGB to copy. May be null.
   * @return a copy of the passed in RGB, or null if the argument was null.
   */
  public static RGB defensiveCopy( RGB rgb ) {
    return rgb == null ? null : new RGB( rgb.red, rgb.green, rgb.blue );
  }

  /**
   * Returns an RGB representing the color described by the argument.
   * <p>
   * Sample colors:<br>
   * 0xFFFFFF: white<br>
   * 0x000000: black<br>
   * 0xFF0000: red<br>
   * 0x00FF00: green<br>
   * 0x0000FF: blue
   * 
   * @param rgb an integer containing the red, green and blue components in the 0xFF0000, 0x00FF00, and
   *        0x0000FF positions, respectively.
   * @return an RGB representing the color described by the argument.
   */
  public static RGB deriveRGB( final int rgb ) {
    int red = ( rgb >> 16 ) & 0xFF;
    int green = ( rgb >> 8 ) & 0xFF;
    int blue = rgb & 0xFF;
    return new RGB( red, green, blue );
  }
}
