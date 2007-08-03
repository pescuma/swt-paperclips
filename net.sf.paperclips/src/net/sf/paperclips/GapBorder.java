/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A border which leaves a gap around the target Print.
 * @author Matthew Hall
 */
public class GapBorder implements Border {
  /** The top gap of a closed border, expressed in points. */
  public int top = 0;

  /** The bottom gap of a closed border, expressed in points. */
  public int bottom = 0;

  /** The left side gap, expressed in points. */
  public int left = 0;

  /** The right side gap, expressed in points. */
  public int right = 0;

  /** The top gap of an open border, expressed in points. */
  public int openTop = 0;

  /** The bottom gap of an open border, expressed in points. */
  public int openBottom = 0;

  /**
   * Constructs a GapBorder with 0 gap around all sides.
   */
  public GapBorder () {
    this (0);
  }

  /**
   * Constructs a GapBorder with the given gap around all sides.
   * @param gap the gap, expressed in points.
   */
  public GapBorder (int gap) {
    setGap (gap);
  }

  /**
   * Sets the left, right, closed top and closed bottom gaps to he argument.
   * @param gap the gap, expressed in points.
   */
  public void setGap (int gap) {
    top = left = bottom = right = checkGap (gap);
  }

  int checkGap (int gap) {
    if (gap < 0) throw new IllegalArgumentException ();
    return gap;
  }

  public BorderPainter createPainter (Device device, GC gc) {
    return new GapBorderPainter (this, device);
  }
}

class GapBorderPainter extends AbstractBorderPainter {
  final int top;
  final int openTop;
  final int bottom;
  final int openBottom;
  final int left;
  final int right;

  GapBorderPainter (GapBorder target, Device device) {
    Point dpi = device.getDPI ();

    this.top = toPixels (target.top, dpi.y);
    this.bottom = toPixels (target.bottom, dpi.y);
    this.openTop = toPixels (target.openTop, dpi.y);
    this.openBottom = toPixels (target.openBottom, dpi.y);

    this.left = toPixels (target.left, dpi.x);
    this.right = toPixels (target.right, dpi.x);
  }

  GapBorderPainter (GapBorderPainter that) {
    this.top = that.top;
    this.bottom = that.bottom;
    this.left = that.left;
    this.right = that.right;

    this.openTop = that.openTop;
    this.openBottom = that.openBottom;
  }

  static int toPixels (int points, int dpi) {
    return Math.max (0, points) * dpi / 72;
  }

  public int getBottom (boolean open) {
    return open ? openBottom : bottom;
  }

  public int getLeft () {
    return left;
  }

  public int getRight () {
    return right;
  }

  public int getTop (boolean open) {
    return open ? openTop : top;
  }

  public Point getOverlap () {
    return new Point (Math.min (left, right), Math.max (top, bottom));
  }

  public void paint (GC gc,
                     int x,
                     int y,
                     int width,
                     int height,
                     boolean topOpen,
                     boolean bottomOpen) {
    // Nothing to paint.
  }

  public void dispose () {
    // Nothing to dispose
  }
}