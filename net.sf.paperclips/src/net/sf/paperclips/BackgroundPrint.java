/*
 * Created on Apr 22, 2006
 * Author: Matthew Hall
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A decorator that paints a background color behind it's target.
 * @author Matthew
 */
public class BackgroundPrint implements Print {
  Print target;
  RGB background;

  /**
   * Constructs a BackgroundPrint with the given target and background color.
   * @param target the 
   * @param background
   */
  public BackgroundPrint(Print target, RGB background) {
    if (target == null || background == null)
      throw new NullPointerException();
    this.target = target;
    this.background = background;
  }

  /**
   * Returns the background color.
   * @return the background color.
   */
  public RGB getBackground () {
    return background;
  }

  /**
   * Sets the background color.
   * @param background the new background color.
   */
  public void setBackground (RGB background) {
    if (background == null)
      throw new NullPointerException();
    this.background = background;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new BackgroundIterator(this, device, gc);
  }
}

class BackgroundIterator implements PrintIterator {
  private final PrintIterator target;
  private final RGB background;
  private final Device device;

  BackgroundIterator(BackgroundPrint print, Device device, GC gc) {
    if (device == null)
      throw new NullPointerException();
    this.device = device;
    this.target = print.target.iterator (device, gc);
    this.background = print.background;
  }

  BackgroundIterator(BackgroundIterator that) {
    this.target = that.target.copy();
    this.background = that.background;
    this.device = that.device;
  }

  public Point minimumSize () {
    return target.minimumSize ();
  }

  public Point preferredSize () {
    return target.preferredSize ();
  }

  public boolean hasNext () {
    return target.hasNext ();
  }

  public PrintPiece next (int width, int height) {
    PrintPiece targetPiece = target.next(width, height);
    if (targetPiece == null) return null;
    return new BackgroundPiece(targetPiece, background, device);
  }

  public PrintIterator copy () {
    return new BackgroundIterator(this);
  }
}

class BackgroundPiece implements PrintPiece {
  private final PrintPiece target;
  private final Device device;
  private final RGB background;

  private Color backgroundColor;

  BackgroundPiece(PrintPiece target, RGB background, Device device) {
    if (target == null || device == null || background == null)
      throw new NullPointerException();
    this.target = target;
    this.device = device;
    this.background = background;
  }

  private Color getBackgroundColor() {
    if (backgroundColor == null)
      this.backgroundColor = new Color(device, background);
    return backgroundColor;
  }

  public Point getSize () {
    return target.getSize();
  }

  public void paint (GC gc, int x, int y) {
    // Remember old background
    Color old_bg = gc.getBackground();

    // Paint background
    gc.setBackground(getBackgroundColor());
    Point size = getSize();
    gc.fillRectangle (x, y, size.x, size.y);

    // Restore old background
    gc.setBackground(old_bg);

    // Paint target
    target.paint (gc, x, y);
  }

  public void dispose () {
    if (backgroundColor != null) {
      backgroundColor.dispose();
      backgroundColor = null;
    }
    target.dispose();
  }
}