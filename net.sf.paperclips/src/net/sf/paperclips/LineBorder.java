/*
 * Created on Oct 18, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A border that draws a rectangle around a print.
 * @author Matthew
 */
public class LineBorder implements Border {
  RGB rgb;

  int lineWidth = 1; // in points

  int gapSize = 5; // in points

  /**
   * Constructs a LineBorder with a black border and 5-pt insets. (72 pts = 1")
   */
  public LineBorder () {
    this (new RGB (0, 0, 0)); // black
  }

  /**
   * Constructs a LineBorder with 5-pt insets. (72 pts = 1")
   * @param rgb the color to use for the border.
   */
  public LineBorder (RGB rgb) {
    setRGB (rgb);
  }

  /**
   * Sets the border color to the argument.
   * @param rgb the new border color.
   */
  public void setRGB (RGB rgb) {
    this.rgb = new RGB (rgb.red, rgb.green, rgb.blue);
  }

  /**
   * Returns the border color.
   * @return the border color.
   */
  public RGB getRGB () {
    return new RGB (rgb.red, rgb.green, rgb.blue);
  }

  /**
   * Sets the line width to the argument.
   * @param points the line width, in points.
   */
  public void setLineWidth (int points) {
    if (points < 1) points = 1;

    this.lineWidth = points;
  }

  /**
   * Returns the line width of the border, expressed in points.
   * @return the line width of the border, expressed in points.
   */
  public int getLineWidth () {
    return lineWidth;
  }

  /**
   * Sets the size of the gap between the line border and the target print.
   * @param points the gap size, expressed in points.
   */
  public void setGapSize (int points) {
    if (points < 1) points = 1;

    this.gapSize = points;
  }

  /**
   * Returns the size of the gap between the line border and the target print,
   * expressed in points.
   * @return the gap size between the line border and the target print.
   */
  public int getGapSize () {
    return Math.max (lineWidth, gapSize);
  }

  public BorderPainter createPainter (Device device, GC gc) {
    return new LineBorderPainter (this, device, gc);
  }
}

class LineBorderPainter extends AbstractBorderPainter {
  final Device device;

  final RGB rgb;

  final Point lineWidth;

  final Point borderWidth;

  LineBorderPainter (LineBorder border, Device device, GC gc) {
    this.rgb = border.rgb;
    this.device = BeanUtils.checkNull (device);

    int lineWidthPoints = border.getLineWidth ();
    int borderWidthPoints = border.getGapSize ();

    Point dpi = device.getDPI ();
    lineWidth = new Point (Math.round (lineWidthPoints * dpi.x / 72f), Math
        .round (lineWidthPoints * dpi.y / 72f));
    borderWidth = new Point (Math.round (borderWidthPoints * dpi.x / 72f), Math
        .round (borderWidthPoints * dpi.y / 72f));
  }

  @Override
  public int getLeft () {
    return borderWidth.x;
  }

  @Override
  public int getRight () {
    return borderWidth.x;
  }

  @Override
  public int getTop (boolean open) {
    return open ? 0 : borderWidth.y;
  }

  @Override
  public int getBottom (boolean open) {
    return open ? 0 : borderWidth.y;
  }

  @Override
  public void paint (GC gc,
                     int x,
                     int y,
                     int width,
                     int height,
                     boolean topOpen,
                     boolean bottomOpen) {
    Color oldColor = gc.getBackground ();
    Color color = new Color (device, rgb);

    try {
      gc.setBackground (color);

      // Left & right
      gc.fillRectangle (x, y, lineWidth.x, height);
      gc.fillRectangle (x + width - lineWidth.x, y, lineWidth.x, height);

      // Top & bottom
      if (!topOpen) gc.fillRectangle (x, y, width, lineWidth.y);
      if (!bottomOpen)
        gc.fillRectangle (x, y + height - lineWidth.y, width, lineWidth.y);
    } finally {
      gc.setBackground (oldColor);
      color.dispose ();
    }
  }

  public Point getOverlap () {
    return new Point (lineWidth.x, lineWidth.y);
  }
}