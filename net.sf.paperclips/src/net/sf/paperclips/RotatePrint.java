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
 * A decorator print that rotates it's target by increments of 90 degrees.
 * <p><em>Note</em>: On Windows, this class depends on a bugfix available as of Eclipse build 3.2,
 * release candidate 3 (2006-04-28).  Prior to this release, using RotatePrint triggers the bug,
 * causing the document to scale very large on paper.  This bug only manifests itself on paper, not
 * with on-screen viewing.  
 * @author Matthew
 */
public final class RotatePrint implements Print {
  private final Print target;
  private final int   angle;

  /**
   * Constructs a RotatePrint that rotates it's target 90 degrees counter-clockwise.
   * @param target the print to rotate.
   */
  public RotatePrint(Print target) {
    this(target, 90);
  }

  /**
   * Constructs a RotatePrint.
   * @param target the print to rotate.
   * @param angle the angle by which the target will be rotated, expressed in degrees
   *        counter-clockwise.  A negative number rotates clockwise.
   */
  public RotatePrint(Print target, int angle) {
    this.target = BeanUtils.checkNull(target);
    this.angle = checkAngle(angle);
  }

  private static int checkAngle(int angle) {
    // Make sure angle is a multiple of 90.
    if (Math.abs(angle) % 90 != 0)
      throw new IllegalArgumentException("Angle must be a multiple of 90 degrees");

    // Bring angle within the range [0, 360) 
    if (angle < 0)
      angle = 360 - angle;
    if (angle >= 360)
      angle -= (angle / 360) * 360;

    return angle;
  }

  public PrintIterator iterator (Device device, GC gc) {
    if (angle == 0)
      return target.iterator(device, gc);
    return new RotateIterator(target, angle, device, gc);
  }
}

final class RotateIterator implements PrintIterator {
  private final Device device;
  private final PrintIterator target;
  private final int angle;

  private final Point minimumSize;
  private final Point preferredSize;

  RotateIterator(Print target, int angle, Device device, GC gc) {
    this.device = BeanUtils.checkNull(device);
    this.target = target.iterator(device, gc);
    this.angle = checkAngle(angle);

    Point min  = this.target.minimumSize ();
    Point pref = this.target.preferredSize ();

    if (angle % 180 == 0) {
      this.minimumSize   = new Point(min.x,  min.y);
      this.preferredSize = new Point(pref.x, pref.y);
    } else { // flip x and y sizes if rotating by 90 or 270 degrees
      this.minimumSize   = new Point(min.y,  min.x);
      this.preferredSize = new Point(pref.y, pref.x);
    }
  }

  private RotateIterator(RotateIterator that) {
    this.device = that.device;
    this.target = that.target.copy();
    this.angle = that.angle;
    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;
  }

  private static int checkAngle(int angle) {
    switch (angle) {
    case 90:
    case 180:
    case 270:
      return angle;
    default:
      throw new IllegalArgumentException("Angle must be 90, 180, or 270");
    }
  }

  public Point minimumSize () {
    return new Point(minimumSize.x, minimumSize.y);
  }

  public Point preferredSize () {
    return new Point(preferredSize.x, preferredSize.y);
  }

  public boolean hasNext () {
    return target.hasNext();
  }

  public PrintPiece next (int width, int height) {
    PrintPiece target;
    if (angle % 180 == 0)
      target = this.target.next (width, height);
    else // flip width and height if rotating by 90 or 270
      target = this.target.next (height, width);

    if (target == null) return null;

    return new RotatePiece(device, target, angle, new Point(width, height));
  }

  public PrintIterator copy () {
    return new RotateIterator(this);
  }
}

final class RotatePiece implements PrintPiece {
  private final Device device;
  private final PrintPiece target;
  private final int angle;
  private final Point size;

  private Transform oldTransform;
  private Transform transform;

  RotatePiece(Device device, PrintPiece target, int angle, Point size) {
    this.device = BeanUtils.checkNull (device);
    this.target = BeanUtils.checkNull(target);
    this.angle = angle;
    this.size = BeanUtils.checkNull(size);
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
      transform = new Transform (device);
    return transform;
  }

  public void paint (GC gc, int x, int y) {
    // Remember old transform so it can be restored after painting.
    Transform oldTransform = getOldTransform();
    gc.getTransform (oldTransform);

    // Get current transform
    Transform transform = getTransform();
    gc.getTransform (transform);

    // Prep the transform
    transform.translate(x, y);
    switch (angle) {
    case 90:
      transform.translate(0, size.y);
      break;
    case 180:
      transform.translate(size.x, size.y);
      break;
    case 270:
      transform.translate(size.x, 0);
      break;
    default:
      throw new IllegalStateException("Illegal degrees value of "+angle);
    }
    transform.rotate (-angle); // reverse the angle since Transform.rotate goes clockwise

    gc.setTransform (transform);
    target.paint (gc, 0, 0);
    gc.setTransform(oldTransform);
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