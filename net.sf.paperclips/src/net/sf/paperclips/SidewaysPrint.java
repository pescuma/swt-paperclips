/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import net.sf.paperclips.internal.NullUtil;

/**
 * A decorator print that rotates it's target by increments of 90 degrees.
 * <p>
 * <em>Note</em>: On Windows, this class depends on a bugfix available as of Eclipse build 3.2, release
 * candidate 3 (2006-04-28). Prior to this release, using SidewaysPrint triggers the bug, causing the
 * document to scale very large on paper. This bug only manifests itself on paper, not with on-screen
 * viewing.
 * <p>
 * SidewaysPrint, unlike RotatePrint, is neither horizontally nor vertically greedy. Greedy prints take up
 * all the available space on the page.
 * @author Matthew Hall
 */
public final class SidewaysPrint implements Print {
  private final Print target;
  private final int   angle;

  /**
   * Constructs a SidewaysPrint that rotates it's target 90 degrees counter-clockwise.
   * @param target the print to rotate.
   */
  public SidewaysPrint( Print target ) {
    this( target, 90 );
  }

  /**
   * Constructs a SidewaysPrint.
   * @param target the print to rotate.
   * @param angle the angle by which the target will be rotated, expressed in degrees counter-clockwise.
   *        Positive values rotate counter-clockwise, and negative values rotate clockwise. Must be a
   *        multiple of 90.
   */
  public SidewaysPrint( Print target, int angle ) {
    NullUtil.notNull( target );
    this.target = target;
    this.angle = checkAngle( angle );
  }

  private static int checkAngle( int angle ) {
    // Make sure angle is a multiple of 90.
    if ( Math.abs( angle ) % 90 != 0 )
      PaperClips.error( SWT.ERROR_INVALID_ARGUMENT, "Angle must be a multiple of 90 degrees" );

    // Bring angle within the range [0, 360)
    while ( angle < 0 )
      angle += 360;
    while ( angle >= 360 )
      angle -= 360;

    return angle;
  }

  /**
   * Returns the print to be rotated.
   * @return the print to be rotated.
   */
  public Print getTarget() {
    return target;
  }

  /**
   * Returns the angle by which the target will be rotated (one of 0, 90, 180, or 270).
   * @return the angle by which the target will be rotated.
   */
  public int getAngle() {
    return angle;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    if ( angle == 0 )
      return target.iterator( device, gc );
    return new SidewaysIterator( target, angle, device, gc );
  }
}

final class SidewaysIterator implements PrintIterator {
  private final Device        device;
  private final PrintIterator target;
  private final int           angle;

  private final Point         minimumSize;
  private final Point         preferredSize;

  SidewaysIterator( Print target, int angle, Device device, GC gc ) {
    NullUtil.notNull( target, device, gc );

    this.device = device;
    this.target = target.iterator( device, gc );
    this.angle = checkAngle( angle ); // returns 90, 180, or 270 only

    Point min = this.target.minimumSize();
    Point pref = this.target.preferredSize();

    if ( this.angle == 180 ) {
      this.minimumSize = new Point( min.x, min.y );
      this.preferredSize = new Point( pref.x, pref.y );
    } else { // flip x and y sizes if rotating by 90 or 270 degrees
      this.minimumSize = new Point( min.y, min.x );
      this.preferredSize = new Point( pref.y, pref.x );
    }
  }

  private SidewaysIterator( SidewaysIterator that ) {
    this.device = that.device;
    this.target = that.target.copy();
    this.angle = that.angle;
    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;
  }

  private static int checkAngle( int angle ) {
    switch ( angle ) {
      case 90:
      case 180:
      case 270:
        break;
      default:
        PaperClips.error( SWT.ERROR_INVALID_ARGUMENT, "Angle must be 90, 180, or 270" );
    }
    return angle;
  }

  public Point minimumSize() {
    return new Point( minimumSize.x, minimumSize.y );
  }

  public Point preferredSize() {
    return new Point( preferredSize.x, preferredSize.y );
  }

  public boolean hasNext() {
    return target.hasNext();
  }

  public PrintPiece next( int width, int height ) {
    PrintPiece target;
    if ( angle == 180 )
      target = PaperClips.next( this.target, width, height );
    else
      // flip width and height if rotating by 90 or 270
      target = PaperClips.next( this.target, height, width );

    if ( target == null )
      return null;

    Point size = target.getSize();
    if ( ( angle / 90 ) % 2 == 1 )
      size = new Point( size.y, size.x );

    return new RotatePiece( device, target, angle, size );
  }

  public PrintIterator copy() {
    return new SidewaysIterator( this );
  }
}