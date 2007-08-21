/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Instances of this class represent a single cell in a GridPrint.
 * @author Matthew Hall
 */
public class GridCell {
  final int   hAlignment;
  final int   vAlignment;
  final Print target;
  final int   colspan;

  GridCell( int hAlignment, int vAlignment, Print target, int colspan ) {
    if ( target == null )
      throw new NullPointerException();
    this.hAlignment = checkHorizontalAlignment( hAlignment );
    this.vAlignment = checkVerticalAlignment( vAlignment );
    this.target = target;
    this.colspan = checkColspan( colspan );
  }

  /**
   * Returns a Point representing the horizontal and vertical alignment applied to the cell's content.
   * @return a Point representing the horizontal and vertical alignment applied to the cell's content.
   */
  public Point getAlignment() {
    return new Point( hAlignment, vAlignment );
  }

  /**
   * Returns the horizontal alignment applied to the cell content.
   * @return the horizontal alignment applied to the cell content.
   */
  public int getHorizontalAlignment() {
    return hAlignment;
  }

  /**
   * Returns the vertical alignment applied to the cell content.
   * @return the vertical alignment applied to the cell content.
   */
  public int getVerticalAlignment() {
    return vAlignment;
  }

  /**
   * Returns the content print of the cell.
   * @return the content print of the cell.
   */
  public Print getContent() {
    return target;
  }

  /**
   * Returns the number of columns this cell spans across.
   * @return the number of columns this cell spans across.
   */
  public int getColSpan() {
    return colspan;
  }

  private static int checkHorizontalAlignment( int hAlignment ) {
    if ( ( hAlignment & SWT.DEFAULT ) == SWT.DEFAULT )
      return SWT.DEFAULT;
    else if ( ( hAlignment & SWT.LEFT ) == SWT.LEFT )
      return SWT.LEFT;
    else if ( ( hAlignment & SWT.CENTER ) == SWT.CENTER )
      return SWT.CENTER;
    else if ( ( hAlignment & SWT.RIGHT ) == SWT.RIGHT )
      return SWT.RIGHT;
    else
      throw new IllegalArgumentException( "Align must be one of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT" );
  }

  private static int checkVerticalAlignment( int vAlignment ) {
    if ( ( vAlignment & SWT.DEFAULT ) == SWT.DEFAULT )
      return SWT.DEFAULT;
    else if ( ( vAlignment & SWT.TOP ) == SWT.TOP )
      return SWT.TOP;
    else if ( ( vAlignment & SWT.CENTER ) == SWT.CENTER )
      return SWT.CENTER;
    else if ( ( vAlignment & SWT.BOTTOM ) == SWT.BOTTOM )
      return SWT.BOTTOM;
    else if ( ( vAlignment & SWT.FILL ) == SWT.FILL )
      return SWT.FILL;
    else
      throw new IllegalArgumentException( "Align must be one of SWT.TOP, SWT.CENTER, SWT.BOTTOM, SWT.DEFAULT, or SWT.FILL" );
  }

  private int checkColspan( int colspan ) {
    if ( colspan > 0 || colspan == GridPrint.REMAINDER )
      return colspan;

    throw new IllegalArgumentException( "colspan must be a positive number or GridPrint.REMAINDER" );
  }

  GridCellIterator iterator( Device device, GC gc ) {
    return new GridCellIterator( this, device, gc );
  }
}