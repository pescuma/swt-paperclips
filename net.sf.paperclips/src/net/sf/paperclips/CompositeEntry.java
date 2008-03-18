/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import net.sf.paperclips.internal.Util;

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
    Util.notNull( piece, offset );
    checkOffset( offset );

    this.piece = piece;
    this.offset = offset;
  }

  private void checkOffset( Point offset ) {
    if ( offset.x < 0 || offset.y < 0 )
      PaperClips.error( SWT.ERROR_INVALID_ARGUMENT, "Offset cannot be negative: " + offset );
  }

  /**
   * Disposes this entry's print piece.
   */
  public void dispose() {
    piece.dispose();
  }
}