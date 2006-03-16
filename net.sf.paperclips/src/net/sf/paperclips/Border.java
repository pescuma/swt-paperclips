/*
 * Created on Oct 18, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * Interface for drawing borders, used by BorderPaint and GridPrint for drawing
 * borders a child print and grid cells, respectively.
 * @author Matthew
 */
public interface Border {
  /**
   * Creates a BorderPainter which uses the given Device and GC.
   * @param device the print device.
   * @param gc a GC for drawing to the print device.
   * @return a BorderPainter for painting the border on the given Device and GC.
   */
  public BorderPainter createPainter (Device device, GC gc);
}
