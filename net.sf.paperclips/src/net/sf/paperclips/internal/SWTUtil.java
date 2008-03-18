package net.sf.paperclips.internal;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Utility methods for dealing with SWT objects
 * 
 * @author Matthew Hall
 */
public class SWTUtil {

  /**
   * Returns a defensive copy of the passed in FontData.
   * @param fontData the FontData to copy. May be null.
   * @return a copy of the passed in FontData, or null if the argument was null.
   */
  public static FontData copy( FontData fontData ) {
    return fontData == null ? null : new FontData( fontData.getName(),
                                                   fontData.getHeight(),
                                                   fontData.getStyle() );
  }

  /**
   * Returns a defensive copy of the passed in RGB.
   * @param rgb the RGB to copy. May be null.
   * @return a copy of the passed in RGB, or null if the argument was null.
   */
  public static RGB copy( RGB rgb ) {
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

  /**
   * Returns whether the PaletteData arguments are equivalent.
   * @param left the left PaletteData
   * @param right the right PaletteData
   * @return whether the PaletteData arguments are equivalent.
   */
  public static boolean equal( PaletteData left, PaletteData right ) {
    if ( left == right )
      return true;
    if ( left == null || right == null )
      return false;
    return left.isDirect == right.isDirect && left.blueMask == right.blueMask
        && left.blueShift == right.blueShift && left.greenMask == right.greenMask
        && left.greenShift == right.greenShift && left.redMask == right.redMask
        && left.redShift == right.redShift && Util.equal( left.colors, right.colors );
  }

  /**
   * Returns whether the ImageData arguments are equivalent.
   * @param left the left ImageData
   * @param right the right ImageData
   * @return whether the ImageData arguments are equivalent.
   */
  public static boolean equal( ImageData left, ImageData right ) {
    if ( left == right )
      return true;
    if ( left == null || right == null )
      return false;
    if ( left.width != right.width || left.height != right.height )
      return false;
    if ( !equal( left.palette, right.palette ) )
      return false;

    final int width = left.width;
    int[] leftPixels = new int[width];
    int[] rightPixels = new int[width];
    byte[] leftAlphas = new byte[width];
    byte[] rightAlphas = new byte[width];
    for ( int y = 0; y < left.height; y++ ) {
      left.getAlphas( 0, y, width, leftAlphas, 0 );
      right.getAlphas( 0, y, width, rightAlphas, 0 );
      if ( !Util.equal( leftAlphas, rightAlphas ) )
        return false;

      left.getPixels( 0, y, width, leftPixels, 0 );
      right.getPixels( 0, y, width, rightPixels, 0 );
      if ( !Util.equal( leftPixels, rightPixels ) ) {
        for ( int x = 0; x < width; x++ ) {
          if ( leftAlphas[x] != 0 && leftPixels[x] != rightPixels[x] )
            return false;
        }
      }
    }

    return true;
  }
}
