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
 * A print wrapper which prevents its target from being broken into multiple
 * pieces when printed.  If there isn't enough room to print the target in one
 * piece on the current page (or column, if it's inside a ColumnPrint), it
 * will be printed on the next page (or column).
 * 
 * <p>Care must be taken when using this class to avoid unprintable documents.
 * If the target of a NoBreakPrint does not fit in the available space on the
 * print device, the entire document will fail to print.
 * @author Matthew
 */
public class NoBreakPrint implements Print {
  private final Print target;

  /**
   * Constructs a NoBreakPrint with the given target.
   * @param target the print to 
   */
  public NoBreakPrint(Print target) {
    if (target == null)
      throw new NullPointerException();
    this.target = target;
  }

  /**
   * Returns the print which will not be broken across pages.
   * @return the print which will not be broken across pages.
   */
  public Print getTarget() {
  	return target;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new NoBreakIterator(target.iterator(device, gc));
  }
}

class NoBreakIterator implements PrintIterator {
  private PrintIterator target;

  NoBreakIterator(PrintIterator target) {
    if (target == null)
      throw new NullPointerException();
    this.target = target;
  }

  public PrintIterator copy () {
    return new NoBreakIterator(target.copy());
  }

  public boolean hasNext () {
    return target.hasNext();
  }

  public Point minimumSize () {
    return target.minimumSize();
  }

  public Point preferredSize () {
    return target.preferredSize();
  }

  public PrintPiece next (int width, int height) {
    // Use a test iterator so we preserve the original iterator
    PrintIterator testIterator = target.copy();

    PrintPiece result = PaperClips.next(testIterator, width, height);
    if (result == null)
      return result;

    if (testIterator.hasNext()) // Failed to print the whole thing
      return null;

    this.target = testIterator;
    return result;
  }
}