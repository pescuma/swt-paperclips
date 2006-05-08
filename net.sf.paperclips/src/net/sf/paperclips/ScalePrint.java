/*
 * Created on May 8, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

/**
 * A decorator print that scales it's target larger or smaller.
 * @author Matthew
 */
public class ScalePrint implements Print {
  final Print target;
  final Double scale;

  /**
   * Constructs a ScalePrint which scales down it's target to print at it's preferred size.  This
   * constructor is equivalent to calling new ScalePrint(target, null). 
   * @param target the print to scale down.
   */
  public ScalePrint(Print target) {
    this.target = BeanUtils.checkNull(target);
    this.scale = null;
  }

  /**
   * Constructs a ScalePrint which scales it's target by the given factor.
   * @param target
   * @param scale the scale factor (must be >0).  A value of 2.0 draws at double the size, and a
   *        value of 0.5 draws at twice the size.  A null value automatically scales down so the
   *        target is rendered at it's preferred size.
   * @throws IllegalArgumentException if scale is not > 0.
   */
  public ScalePrint(Print target, Double scale) {
    if (scale != null && !(scale > 0))
      throw new IllegalArgumentException("Scale "+scale+" must be > 0");

    this.target = BeanUtils.checkNull (target);
    this.scale = scale;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new ScaleIterator(this, device, gc);
  }
}

class ScaleIterator implements PrintIterator {
  private final Device device;
  private final PrintIterator target;
  private final Double scale;

  private final Point minimumSize;
  private final Point preferredSize;

  ScaleIterator(ScalePrint print, Device device, GC gc) {
    this.device = BeanUtils.checkNull(device);
    this.target = print.target.iterator(device, gc);
    this.scale  = print.scale;

    Point min = target.minimumSize ();
    Point pref = target.preferredSize ();
    if (scale == null) { // auto-scale
      minimumSize = new Point(Math.min(1, min.x), Math.min(1, min.y));
      preferredSize = new Point(Math.min(1, pref.x), Math.min(1, pref.y));
    } else { // specific scale
      double s = scale.doubleValue();
      minimumSize = new Point(
          (int) Math.ceil (min.x * s),
          (int) Math.ceil (min.y * s));
      preferredSize = new Point(
          (int) Math.ceil (pref.x * s),
          (int) Math.ceil (pref.y * s));
    }
  }

  private ScaleIterator(ScaleIterator that) {
    this.device = that.device;
    this.target = that.target.copy();
    this.scale  = that.scale;

    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;
  }

  public Point minimumSize () {
    return minimumSize;
  }

  public Point preferredSize () {
    return preferredSize;
  }

  public boolean hasNext () {
    return target.hasNext ();
  }

  public PrintPiece next (int width, int height) {
    // Find out what scale we're going to iterate at.
    double scale;
    Point pref = target.preferredSize ();
    if (this.scale == null)
      scale = Math.min(
          Math.min(
            (double) width  / (double) pref.x,
            (double) height / (double) pref.y),
          1.0);
    else
      scale = this.scale.doubleValue ();

    // Calculate the width and height to be passed to the target.
    final int scaledWidth  = (int) Math.ceil (width  / scale);
    final int scaledHeight = (int) Math.ceil (height / scale);

    PrintPiece target = this.target.next(scaledWidth, scaledHeight);

    if (target == null) return null;

    return new ScalePiece(device, target, scale, width, height);
  }

  public PrintIterator copy () {
    return new ScaleIterator(this);
  }
}

final class ScalePiece implements PrintPiece {
  private final Device device;
  private final PrintPiece target;
  private final double scale;
  private final Point size;

  private Transform oldTransform;
  private Transform transform;

  ScalePiece(Device device, PrintPiece target, double scale, int maxWidth, int maxHeight) {
    this.device = BeanUtils.checkNull (device);
    this.target = BeanUtils.checkNull (target);
    this.scale = scale;
    Point targetSize = target.getSize();
    this.size = new Point(
        Math.min ((int) Math.ceil (targetSize.x * scale), maxWidth),
        Math.min ((int) Math.ceil (targetSize.y * scale), maxHeight));
  }

  public Point getSize () {
    return new Point(size.x, size.y);
  }

  private Transform getOldTransform() {
    if (oldTransform == null)
      oldTransform = new Transform(device);
    return oldTransform;
  }

  private Transform getTransform() {
    if (transform == null)
      transform = new Transform(device);
    return transform;
  }

  public void paint (GC gc, int x, int y) {
    // Get the transforms
    Transform oldTransform = getOldTransform();
    Transform transform = getTransform();

    gc.getTransform(oldTransform);
    gc.getTransform(transform);

    // Setup the transform for the scale.
    transform.translate(x, y);
    transform.scale ((float)scale, (float)scale);

    gc.setTransform(transform);
    target.paint (gc, 0, 0);
    gc.setTransform (oldTransform);
  }

  public void dispose () {
    if (oldTransform != null) {
      oldTransform.dispose();
      oldTransform = null;
    }
    if (transform != null) {
      transform.dispose();
      transform = null;
    }
    target.dispose();
  }
}