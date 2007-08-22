/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print for drawing horizontal and vertical lines.
 * <p>
 * LinePrints are either horizontally or vertically greedy, according to the orientation of the line. Greedy
 * prints take up all the available space on the page.
 * @author Matthew Hall
 */
public class LinePrint implements Print {
  final int orientation;

  double    thickness;

  RGB       rgb = new RGB( 0, 0, 0 );

  /**
   * Constructs a horizontal LinePrint.
   */
  public LinePrint() {
    this( SWT.HORIZONTAL );
  }

  /**
   * Constructs a LinePrint with the given orientation and 1-point thickness.
   * @param orientation one of SWT#HORIZONTAL or SWT#VERTICAL.
   */
  public LinePrint( int orientation ) {
    this( orientation, 1.0 );
  }

  /**
   * Constructs a LinePrint with the given orientation and thickness.
   * @param orientation one of SWT#HORIZONTAL or SWT#VERTICAL.
   * @param thickness the line thickness, expressed in points.
   */
  public LinePrint( int orientation, double thickness ) {
    this.orientation = checkOrientation( orientation );
    this.thickness = checkThickness( thickness );
  }

  /**
   * Returns the line orientation (one of {@link SWT#HORIZONTAL} or {@link SWT#VERTICAL}).
   * @return the line orientation.
   */
  public int getOrientation() {
    return orientation;
  }

  private int checkOrientation( int orientation ) {
    if ( ( orientation & SWT.HORIZONTAL ) == SWT.HORIZONTAL )
      return SWT.HORIZONTAL;
    else if ( ( orientation & SWT.VERTICAL ) == SWT.VERTICAL )
      return SWT.VERTICAL;
    else
      return SWT.HORIZONTAL;
  }

  private double checkThickness( double thickness ) {
    if ( thickness < 0 )
      return 0;
    return thickness;
  }

  /**
   * Returns the line thickness, in points. 72 points = 1".
   * @return the line thickness, in points.
   */
  public double getThickness() {
    return thickness;
  }

  /**
   * Sets the line thickness, in points. 72 points = 1".
   * @param thickness the line thickness, in points.
   */
  public void setThickness( double thickness ) {
    this.thickness = thickness;
  }

  /**
   * Sets the line color to the argument.
   * @param foreground the new line color.
   */
  public void setRGB( RGB foreground ) {
    if ( foreground == null )
      throw new NullPointerException();
    this.rgb = foreground;
  }

  /**
   * Returns the line color.
   * @return the line color.
   */
  public RGB getRGB() {
    return rgb;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return new LineIterator( this, device, gc );
  }
}

class LineIterator extends AbstractIterator {
  final int       orientation;
  final Point     thickness;
  final RGB       rgb;

  private boolean hasNext = true;

  LineIterator( LinePrint print, Device device, GC gc ) {
    super( device, gc );
    this.orientation = print.orientation;
    this.rgb = print.rgb;
    Point dpi = device.getDPI();

    // (convert from points to pixels on device)
    this.thickness =
        new Point( Math.max( 1, (int) Math.round( print.thickness * dpi.x / 72 ) ),
                   Math.max( 1, (int) Math.round( print.thickness * dpi.y / 72 ) ) );
  }

  LineIterator( LineIterator that ) {
    super( that );
    this.orientation = that.orientation;
    this.rgb = that.rgb;
    this.hasNext = that.hasNext;
    this.thickness = that.thickness;
  }

  public boolean hasNext() {
    return hasNext;
  }

  Point getSize( int width, int height ) {
    return orientation == SWT.VERTICAL ? new Point( thickness.x, height ) : new Point( width, thickness.y );
  }

  public PrintPiece next( int width, int height ) {
    if ( !hasNext() )
      throw new IllegalStateException();

    // Make sure the line fits :)
    Point size = getSize( width, height );
    if ( size.x > width || size.y > height )
      return null;

    PrintPiece result = new LinePiece( this, size );
    hasNext = false;

    return result;
  }

  public Point minimumSize() {
    return new Point( thickness.x, thickness.y );
  }

  public Point preferredSize() {
    return new Point( thickness.x, thickness.y );
  }

  public PrintIterator copy() {
    return new LineIterator( this );
  }
}

class LinePiece extends AbstractPiece {
  private final RGB rgb;

  private Color     background;

  LinePiece( LineIterator iter, Point size ) {
    super( iter, size );
    this.rgb = iter.rgb;
  }

  private Color getBackground() {
    if ( background == null )
      background = new Color( device, rgb );
    return background;
  }

  public void paint( GC gc, int x, int y ) {
    Color bg_old = gc.getBackground();
    Point size = getSize();
    try {
      gc.setBackground( getBackground() );
      gc.fillRectangle( x, y, size.x, size.y );
    }
    finally {
      gc.setBackground( bg_old );
    }
  }

  public void dispose() {
    if ( background != null ) {
      background.dispose();
      background = null;
    }
  }
}