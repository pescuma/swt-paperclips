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
 * release candidate 3 (2006-04-28).  Prior to this release, using SidewaysPrint triggers the bug,
 * causing the document to scale very large on paper.  This bug only manifests itself on paper, not
 * with on-screen viewing.
 * <p>
 * SidewaysPrint, unlike RotatePrint, is neither horizontally nor vertically greedy.  Greedy prints
 * take up all the available space on the page.  
 * @author Matthew
 */
public final class SidewaysPrint implements Print {
  private final Print target;
  private final int   angle;

  /**
   * Constructs a SidewaysPrint that rotates it's target 90 degrees counter-clockwise.
   * @param target the print to rotate.
   */
  public SidewaysPrint(Print target) {
    this(target, 90);
  }

  /**
   * Constructs a SidewaysPrint.
   * @param target the print to rotate.
   * @param angle the angle by which the target will be rotated, expressed in degrees
   *        counter-clockwise.  Positive values rotate counter-clockwise, and negative values
   *        rotate clockwise.  Must be a multiple of 90.
   */
  public SidewaysPrint(Print target, int angle) {
    if (target == null)
      throw new NullPointerException();
    this.target = target;
    this.angle = checkAngle(angle);
  }

  private static int checkAngle(int angle) {
    // Make sure angle is a multiple of 90.
    if (Math.abs(angle) % 90 != 0)
      throw new IllegalArgumentException("Angle must be a multiple of 90 degrees");

    // Bring angle within the range [0, 360)
    while (angle < 0)
      angle += 360;
    while (angle >= 360)
      angle -= 360;

    return angle;
  }

  public PrintIterator iterator (Device device, GC gc) {
    if (angle == 0)
      return target.iterator(device, gc);
    return new SidewaysIterator(target, angle, device, gc);
  }
}

final class SidewaysIterator implements PrintIterator {
  private final Device device;
  private final PrintIterator target;
  private final int angle;

  private final Point minimumSize;
  private final Point preferredSize;

  SidewaysIterator(Print target, int angle, Device device, GC gc) {
    if (device == null)
      throw new NullPointerException();

    this.device = device;
    this.target = target.iterator(device, gc);
    this.angle = checkAngle(angle); // returns 90, 180, or 270 only

    Point min  = this.target.minimumSize ();
    Point pref = this.target.preferredSize ();

    if (this.angle == 180) {
      this.minimumSize   = new Point(min.x,  min.y);
      this.preferredSize = new Point(pref.x, pref.y);
    } else { // flip x and y sizes if rotating by 90 or 270 degrees
      this.minimumSize   = new Point(min.y,  min.x);
      this.preferredSize = new Point(pref.y, pref.x);
    }
  }

  private SidewaysIterator(SidewaysIterator that) {
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
    if (angle == 180) // angle may only be init'd to 90, 180, of 270
      target = PaperClips.next(this.target, width, height);
    else // flip width and height if rotating by 90 or 270
      target = PaperClips.next(this.target, height, width);

    if (target == null) return null;

    return new SidewaysPiece(device, target, angle);
  }

  public PrintIterator copy () {
    return new SidewaysIterator(this);
  }
}

final class SidewaysPiece implements PrintPiece {
  private final Device device;
  private final PrintPiece target;
  private final int angle;
  private final Point size;

  private Transform oldTransform;
  private Transform transform;

  SidewaysPiece(Device device, PrintPiece target, int angle) {
    if (device == null || target == null)
      throw new NullPointerException();

    Point size = target.getSize();
    if ((angle / 90) % 2 == 1)
      size = new Point(size.y, size.x);

    this.device = device;
    this.target = target;
    this.angle = angle;
    this.size = size;
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