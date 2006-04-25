/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print for drawing horizontal and vertical lines.
 * @author Matthew
 */
public class LinePrint implements Print {
  final int orientation;

  RGB rgb = new RGB (0, 0, 0);

  /**
   * Constructs a horizontal LinePrint.
   */
  public LinePrint () {
    this (SWT.HORIZONTAL);
  }

  /**
   * Constructs a LinePrint with the given orientation.
   * @param orientation one of SWT#HORIZONTAL or SWT#VERTICAL.
   */
  public LinePrint (int orientation) {
    this.orientation = checkOrientation (orientation);
  }

  private int checkOrientation (int orientation) {
    if ((orientation & SWT.HORIZONTAL) == SWT.HORIZONTAL)
      return SWT.HORIZONTAL;
    else if ((orientation & SWT.VERTICAL) == SWT.VERTICAL)
      return SWT.VERTICAL;
    else
      return SWT.HORIZONTAL;
  }

  /**
   * Sets the line color to the argument.
   * @param foreground the new line color.
   */
  public void setRGB (RGB foreground) {
    this.rgb = BeanUtils.checkNull (foreground);
  }

  /**
   * Returns the line color.
   * @return the line color.
   */
  public RGB getRGB () {
    return rgb;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new LineIterator (this, device, gc);
  }
}

class LineIterator extends AbstractIterator {
  final int orientation;

  final RGB rgb;

  private boolean hasNext = true;

  LineIterator (LinePrint print, Device device, GC gc) {
    super (device, gc);
    this.orientation = print.orientation;
    this.rgb = print.rgb;
  }

  LineIterator (LineIterator that) {
    super (that);
    this.orientation = that.orientation;
    this.rgb = that.rgb;
    this.hasNext = that.hasNext;
  }

  public boolean hasNext () {
    return hasNext;
  }

  Point getSize (int width, int height) {
    Point dpi = device.getDPI ();
    switch (orientation) {
    case SWT.VERTICAL:
      return new Point (Math.max (Math.round (dpi.x / 72f), 1), height);
    default:
      return new Point (width, Math.max (Math.round (dpi.y / 72f), 1));
    }
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    // Make sure the line fits :)
    Point size = getSize (width, height);
    if (size.x > width || size.y > height) return null;

    hasNext = false;

    return new LinePiece (this, size);
  }

  public Point minimumSize () {
    return getSize (1, 1);
  }

  public Point preferredSize () {
    return getSize (1, 1);
  }

  public PrintIterator copy () {
    return new LineIterator (this);
  }
}

class LinePiece extends AbstractPiece {
  private final RGB rgb;

  private Color background;

  LinePiece (LineIterator iter, Point size) {
    super (iter, size);
    this.rgb = iter.rgb;
  }

  private Color getBackground() {
    if (background == null)
      background = new Color(device, rgb);
    return background;
  }

  public void paint (GC gc, int x, int y) {
    Color bg_old = gc.getBackground ();
    Point size = getSize ();
    try {
      gc.setBackground (getBackground());
      gc.fillRectangle (x, y, size.x, size.y);
    } finally {
      gc.setBackground (bg_old);
    }
  }

  public void dispose () {
    if (background != null) {
      background.dispose();
      background = null;
    }
  }
}