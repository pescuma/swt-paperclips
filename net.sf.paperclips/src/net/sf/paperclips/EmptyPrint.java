/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which contains empty space. Useful for putting blank cells in a
 * GridPrint.
 * @author Matthew
 */
public class EmptyPrint implements Print {
  final int width;

  final int height;

  /**
   * Constructs an EmptyPrint with size (0, 0).
   */
  public EmptyPrint () {
    this (0, 0);
  }

  /**
   * Constructs an EmptyPrint with the given size.
   * @param width width of the Print, in points (72pts = 1").
   * @param height height of the Print, in points (72pts = 1").
   */
  public EmptyPrint (int width, int height) {
    this.width = checkDimension (width);
    this.height = checkDimension (height);
  }

  private int checkDimension (int dim) {
    if (dim >= 0) return dim;

    throw new IllegalArgumentException ("EmptyPrint dimensions must be >= 0");
  }

  /**
   * Returns "PaperClips print job". This method is invoked by PrintUtil to
   * determine the name of the print job. Override this method to change this
   * default.
   */
  @Override
  public String toString () {
    return "PaperClips print job";
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new EmptyIterator (device);
  }

  private class EmptyIterator implements PrintIterator {
    private final Point size;

    private boolean hasNext = true;

    EmptyIterator (Device dev) {
      BeanUtils.checkNull (dev);
      Point dpi = dev.getDPI ();
      this.size = new Point (Math.round (width * dpi.x / 72f), Math
          .round (height * dpi.y / 72f));
    }

    EmptyIterator (EmptyIterator that) {
      this.size = that.size;
      this.hasNext = that.hasNext;
    }

    public boolean hasNext () {
      return hasNext;
    }

    public PrintPiece next (int width, int height) {
      if (size.x > width || size.y > height) return null;

      hasNext = false;

      return new EmptyPiece (size);
    }

    public Point minimumSize () {
      return new Point (size.x, size.y);
    }

    public Point preferredSize () {
      return new Point (size.x, size.y);
    }

    public PrintIterator copy () {
      return new EmptyIterator (this);
    }
  }

  private class EmptyPiece implements PrintPiece {
    private Point size;

    EmptyPiece (Point size) {
      this.size = BeanUtils.checkNull (size);
    }

    public Point getSize () {
      return new Point (size.x, size.y);
    }

    public void paint (GC gc, int x, int y) {
    // Nothing to paint
    }

    public void dispose () {
    // Nothing to dispose
    }
  }
}
