/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays its child prints in series. Each element in the series
 * is displayed one at a time (no more than one child per page, although
 * one Print may span several pages).
 * <p>
 * Use this class as the top-level Print when several distinct Prints should be
 * batched into one print job, but printed on separate pages.
 * @author Matthew Hall
 */
public class SeriesPrint implements Print {
  final List items = new ArrayList ();

  /**
   * Adds the given prints to this SeriesPrint.
   * @param items the Prints to add
   */
  public void add (Print[] items) {
    // Check for nulls first.
    if (items == null)
      throw new NullPointerException();
    for (int i = 0; i < items.length; i++)
      if (items[i] == null)
        throw new NullPointerException();

    // OK, add all
    for (int i = 0; i < items.length; i++)
      this.items.add (items[i]);
  }

  /**
   * Adds the given print to this SeriesPrint.
   * @param item the Print to add
   */
  public void add (Print item) {
    if (item == null)
      throw new NullPointerException();
    items.add (item);
  }

  /**
   * Returns the number of Prints that have been added to this SeriesPrint.
   * @return the number of Prints that have been added to this SeriesPrint.
   */
  public int size () {
    return items.size ();
  }

  /**
   * Returns an array of items in the series.
   * @return an array of items in the series.
   */
  public Print[] getItems() {
  	return (Print[]) items.toArray(new Print[items.size()]);
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new SeriesIterator (this, device, gc);
  }
}

class SeriesIterator implements PrintIterator {
  final PrintIterator[] iters;

  // This is the cursor!
  int index;

  SeriesIterator (SeriesPrint print, Device device, GC gc) {
    this.iters = new PrintIterator[print.items.size ()];
    for (int i = 0; i < iters.length; i++) {
      iters[i] = ((Print) print.items.get (i)).iterator (device, gc);
    }
    this.index = 0;
  }

  SeriesIterator (SeriesIterator that) {
    this.iters = (PrintIterator[]) that.iters.clone ();
    this.index = that.index;

    // Start at index since the previous iterators are already consumed.
    for (int i = index; i < iters.length; i++)
      this.iters[i] = that.iters[i].copy ();
  }

  public boolean hasNext () {
    return index < iters.length;
  }

  private Point computeSize (PrintSizeStrategy strategy) {
    Point size = new Point (0, 0);
    for (int i = 0; i < iters.length; i++) {
      PrintIterator iter = iters[i];
      Point printSize = strategy.computeSize (iter);
      size.x = Math.max (size.x, printSize.x);
      size.y = Math.max (size.y, printSize.y);
    }
    return size;
  }

  public Point minimumSize () {
    return computeSize (PrintSizeStrategy.MINIMUM);
  }

  public Point preferredSize () {
    return computeSize (PrintSizeStrategy.PREFERRED);
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    PrintIterator iter = iters[index];
    PrintPiece printPiece = PaperClips.next(iter, width, height);

    if (printPiece != null && !iter.hasNext ()) index++;

    return printPiece;
  }

  public PrintIterator copy () {
    return new SeriesIterator (this);
  }
}