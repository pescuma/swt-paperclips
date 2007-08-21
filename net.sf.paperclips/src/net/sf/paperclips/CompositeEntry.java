/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Point;

/**
 * An entry in a CompositePiece.
 * @author Matthew Hall
 */
public class CompositeEntry {
  final PrintPiece piece;

  final Point      offset;

  /**
   * Constructs a CompositeEntry with the given PrintPiece and offset.
   * @param piece the PrintPiece for this entry.
   * @param offset the painting offset within the CompositePrint.
   */
  public CompositeEntry( PrintPiece piece, Point offset ) {
    if ( piece == null || offset == null )
      throw new NullPointerException();
    this.piece = piece;
    this.offset = offset;

    if ( offset.x < 0 || offset.y < 0 )
      throw new IllegalArgumentException( "PrintPiece offset must be non-negative. (" + offset.x + ", "
          + offset.y + ")" );
  }
}