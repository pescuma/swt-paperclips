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
public class BackgroundColorPrint implements Print {
  Print target;
  RGB background;

  /**
   * Constructs a BackgroundColorPrint with the given target and background color.
   * @param target the 
   * @param background
   */
  public BackgroundColorPrint(Print target, RGB background) {
    this.target = BeanUtils.checkNull(target);
    this.background = BeanUtils.checkNull(background);
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
    this.background = BeanUtils.checkNull(background);
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new BackgroundColorIterator(this, device, gc);
  }
}

class BackgroundColorIterator implements PrintIterator {
  private final PrintIterator target;
  private final RGB background;
  private final Device device;

  BackgroundColorIterator(BackgroundColorPrint print, Device device, GC gc) {
    this.device = BeanUtils.checkNull(device);
    this.target = print.target.iterator (device, gc);
    this.background = print.background;
  }

  BackgroundColorIterator(BackgroundColorIterator that) {
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
    return new BackgroundColorPiece(targetPiece, background, device);
  }

  public PrintIterator copy () {
    return new BackgroundColorIterator(this);
  }
}

class BackgroundColorPiece implements PrintPiece {
  private final PrintPiece target;

  private Color color;

  BackgroundColorPiece(PrintPiece target, RGB background, Device device) {
    this.target = BeanUtils.checkNull(target);
    this.color = new Color(device, background);
  }

  public Point getSize () {
    return target.getSize();
  }

  public void paint (GC gc, int x, int y) {
    // Remember old background
    Color old_bg = gc.getBackground();

    // Paint background
    gc.setBackground(color);
    Point size = getSize();
    gc.fillRectangle (x, y, size.x, size.y);

    // Restore old background
    gc.setBackground(old_bg);

    // Paint target
    target.paint (gc, x, y);
  }

  public void dispose () {
    color.dispose();
  }
}