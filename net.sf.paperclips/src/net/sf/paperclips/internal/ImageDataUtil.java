package net.sf.paperclips.internal;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

/**
 * Convenience methods for working with ImageData objects.
 * @author Matthew Hall
 */
public class ImageDataUtil {

  /**
   * Returns whether the ImageData arguments are equivalent.
   * @param left the left ImageData
   * @param right the right ImageData
   * @return whether the ImageData arguments are equivalent.
   */
  public static boolean areEqual( ImageData left, ImageData right ) {
    if ( left == right )
      return true;
    if ( left == null || right == null )
      return false;
    if ( left.width != right.width || left.height != right.height )
      return false;
    if ( !areEqual( left.palette, right.palette ) )
      return false;

    final int width = left.width;
    int[] leftPixels = new int[width];
    int[] rightPixels = new int[width];
    byte[] leftAlphas = new byte[width];
    byte[] rightAlphas = new byte[width];
    for ( int y = 0; y < left.height; y++ ) {
      left.getAlphas( 0, y, width, leftAlphas, 0 );
      right.getAlphas( 0, y, width, rightAlphas, 0 );
      if ( !EqualsUtil.areEqual( leftAlphas, rightAlphas ) )
        return false;

      left.getPixels( 0, y, width, leftPixels, 0 );
      right.getPixels( 0, y, width, rightPixels, 0 );
      if ( !EqualsUtil.areEqual( leftPixels, rightPixels ) ) {
        for ( int x = 0; x < width; x++ ) {
          if ( leftAlphas[x] != 0 && leftPixels[x] != rightPixels[x] )
            return false;
        }
      }
    }

    return true;
  }

  /**
   * Returns whether the PaletteData arguments are equivalent.
   * @param left the left PaletteData
   * @param right the right PaletteData
   * @return whether the PaletteData arguments are equivalent.
   */
  public static boolean areEqual( PaletteData left, PaletteData right ) {
    if ( left == right )
      return true;
    if ( left == null || right == null )
      return false;
    return left.isDirect == right.isDirect && left.blueMask == right.blueMask
        && left.blueShift == right.blueShift && left.greenMask == right.greenMask
        && left.greenShift == right.greenShift && left.redMask == right.redMask
        && left.redShift == right.redShift && EqualsUtil.areEqual( left.colors, right.colors );
  }
}
