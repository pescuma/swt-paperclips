/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintPiece;

class RotateClockwisePrintPiece implements PrintPiece {
  private final Device     device;
  private final PrintPiece target;
  private final Point      size;

  RotateClockwisePrintPiece( Device device, PrintPiece target ) {
    if ( device == null || target == null )
      PaperClips.error( SWT.ERROR_NULL_ARGUMENT );
    this.device = device;
    this.target = target;
    Point targetSize = target.getSize();
    this.size = new Point( targetSize.y, targetSize.x );
  }

  public void dispose() {
    target.dispose();
  }

  public Point getSize() {
    return new Point( size.x, size.y );
  }

  public void paint( GC gc, int x, int y ) {
    Transform oldTransform = null;
    Transform newTransform = null;
    try {
      oldTransform = new Transform( device );
      gc.getTransform( oldTransform );

      newTransform = new Transform( device );
      gc.getTransform( newTransform );
      newTransform.translate( x, y );
      newTransform.translate( size.x, 0 );
      newTransform.rotate( 90 );
      gc.setTransform( newTransform );

      target.paint( gc, 0, 0 );

      gc.setTransform( oldTransform );
    }
    finally {
      if ( oldTransform != null )
        oldTransform.dispose();
      if ( newTransform != null )
        newTransform.dispose();
    }
  }
}