/*
 * Created on Mar 20, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A print which inserts a page break (or a column break, if inside a
 * ColumnPrint).
 * @author Matthew
 */
public class BreakPrint implements Print {
  public PrintIterator iterator (Device device, GC gc) {
    return new BreakIterator();
  }
}

class BreakIterator implements PrintIterator {
  boolean hasNext;

  BreakIterator() {
    hasNext = true;
  }

  public PrintIterator copy () {
    return hasNext ? new BreakIterator() : this;
  }

  public boolean hasNext () {
    return hasNext;
  }

  public Point minimumSize () {
    return new Point(0, 0);
  }

  public Point preferredSize () {
    return new Point(0, 0);
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext)
      throw new IllegalStateException();

    hasNext = false;
    return new EmptyPiece(new Point(width, height));
  }
}