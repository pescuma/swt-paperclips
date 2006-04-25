/*
 * Created on Apr 24, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;

/**
 * A wrapper print which allows it's target to be wider than the available page width, spreading
 * the content several pages wide (similar to how very wide spreadsheets print).
 * @author Matthew
 */
public final class WidePrint implements Print {
  private final Print target;

  /**
   * Constructs a WidePrint.
   * @param target
   */
  public WidePrint(Print target) {
    if (target == null) throw new NullPointerException();
    this.target = target;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new WideIterator(device, gc, target);
  }
}

class WideIterator implements PrintIterator {
  private final PrintIterator target;
  private final Device device;

  private PrintPiece currentPiece;
  private int        xOffset;

  WideIterator(Device device, GC gc, Print target) {
    if (device == null || gc == null || target == null)
      throw new NullPointerException();
    this.target = target.iterator(device, gc);
    this.device = device;
    currentPiece = null;
    xOffset = 0;
  }

  WideIterator(WideIterator that) {
    this.target = that.target.copy();
    this.device = that.device;

    // FIXME there is a possibility that a super-iterator could dispose this!
    this.currentPiece = that.currentPiece;
    this.xOffset = that.xOffset;
  }

  public Point minimumSize () {
    return target.minimumSize ();
  }

  public Point preferredSize () {
    return target.preferredSize ();
  }

  public boolean hasNext () {
    return currentPiece != null || target.hasNext();
  }

  private int estimatePagesWide(int width) {
    // Pretend the pages are 1px narrower, in case some idiot is embedding this in a ColumnPrint.
    width--; 

    Point pref = target.preferredSize ();
    return (pref.x + width - 1) / width; 
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext()) throw new IllegalStateException();

    if (currentPiece == null) {
      int pagesWide = estimatePagesWide(width);
      currentPiece = target.next ((width - 1) * pagesWide, height);
      xOffset = 0;

      if (currentPiece == null) return null; // Iteration fails
    }

    PrintPiece result = new WidePiece(currentPiece, new Point(width, currentPiece.getSize ().y), xOffset);
    xOffset += width;
    if (xOffset >= currentPiece.getSize().x) {
      currentPiece = null;
      xOffset = 0;
    }

    return result;
  }

  public PrintIterator copy () {
    return new WideIterator(this);
  }
}

class WidePiece implements PrintPiece {
  private final PrintPiece target;
  private final Point      size;
  private final int        xOffset;

  WidePiece(PrintPiece target, Point size, int xOffset) {
    if (target == null || size == null) throw new NullPointerException();
    this.target = target;
    this.size = new Point(size.x, size.y);
    this.xOffset = xOffset;
  }

  public Point getSize () {
    return new Point(size.x, size.y);
  }

  public void paint (GC gc, int x, int y) {
    // Remember clipping region
    Region region = new Region ();
    gc.getClipping (region);

    // Set clipping region so only the portion of the target we want is printed.
    gc.setClipping (x, y, size.x, size.y);

    // Paint the target.
    target.paint (gc, x-xOffset, y);

    // Restore clipping region
    gc.setClipping (region);
    region.dispose();
  }

  public void dispose () {
    target.dispose();
  }
}