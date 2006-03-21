/*
 * Created on Mar 20, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

class EmptyPiece implements PrintPiece {
  private Point size;

  EmptyPiece (Point size) {
    this.size = BeanUtils.checkNull (size);
  }

  public Point getSize () {
    return new Point (size.x, size.y);
  }

  public void paint (GC gc, int x, int y) {
  // Nothing to paint
  }

  public void dispose () {
  // Nothing to dispose
  }
}