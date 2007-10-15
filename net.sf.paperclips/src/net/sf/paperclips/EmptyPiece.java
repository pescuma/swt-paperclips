/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import net.sf.paperclips.internal.NullUtil;

class EmptyPiece implements PrintPiece {
  private final Point size;

  EmptyPiece( Point size ) {
    NullUtil.notNull( size );
    this.size = size;
  }

  public Point getSize() {
    return new Point( size.x, size.y );
  }

  public void paint( GC gc, int x, int y ) {
  // Nothing to paint
  }

  public void dispose() {
  // Nothing to dispose
  }
}