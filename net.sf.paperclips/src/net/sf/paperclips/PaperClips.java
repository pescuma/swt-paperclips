package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

/**
 * This class contains static contants and methods for preparing and printing documents. 
 * @author Matthew Hall
 */
public class PaperClips {
  private PaperClips() {} // no instances

  /**
   * Constant int value for portrait page orientation.
   */
  public static final int PORTRAIT = SWT.VERTICAL;

  /**
   * Constant int value for landscape page orientation.
   */
  public static final int LANDSCAPE = SWT.HORIZONTAL;

  /**
   * Calls iterator.next(width, height) and returns the result, or throws a RuntimeException if
   * the returned PrintPiece is larger than the width or height given.
   * @param iterator the PrintIterator
   * @param width the available width.
   * @param height the available height.
   * @return the next portion of the Print, or null if the width and height are not enough to
   *         display any of the iterator's contents.
   * @throws RuntimeException if the iterator returns a PrintPiece that is larger than the width
   *         or height given.
   */
  public static PrintPiece next(PrintIterator iterator, int width, int height) {
    PrintPiece result = iterator.next(width, height);
    if (result != null) {
      Point size = result.getSize();
      if (size.x > width || size.y > height)
        throw new RuntimeException(
            iterator+" produced a "+size+" piece for a "+width+"x"+height+" area.");
    }
    return result;
  }
}
