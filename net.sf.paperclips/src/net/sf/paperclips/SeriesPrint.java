/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays its child prints in series. Each element in the series
 * is displayed one at a time (no more than one child per iteration, although
 * one Print may span several iterations). Use this class as the top-level Print
 * when several distinct Prints should be batched into one print job, but
 * printed on separate pages.
 * @author Matthew
 */
public class SeriesPrint implements Print {
  final List prints = new ArrayList ();

  /**
   * Adds the given prints to this SeriesPrint.
   * @param prints the Prints to add
   */
  public void add (Print[] prints) {
    // Check for nulls first.
    if (prints == null)
      throw new NullPointerException();
    for (int i = 0; i < prints.length; i++)
      if (prints[i] == null)
        throw new NullPointerException();

    // OK, add all
    for (int i = 0; i < prints.length; i++)
      this.prints.add (prints[i]);
  }

  /**
   * Adds the given print to this SeriesPrint.
   * @param print the Print to add
   */
  public void add (Print print) {
    if (print == null)
      throw new NullPointerException();
    prints.add (print);
  }

  /**
   * Returns the number of Prints that have been added to this SeriesPrint.
   * @return the number of Prints that have been added to this SeriesPrint.
   */
  public int size () {
    return prints.size ();
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new SeriesIterator (this, device, gc);
  }

  /**
   * Returns "PaperClips print job". This method is invoked by PrintUtil to
   * determine the name of the print job. Override this method to change this
   * default.
   */
  public String toString () {
    if (size () == 0) return "PaperClips print job";
    if (size () == 1) return prints.get (0).toString ();
    return "Multiple Print Jobs";
  }
}

class SeriesIterator implements PrintIterator {
  final PrintIterator[] iters;

  // This is the cursor!
  int index;

  SeriesIterator (SeriesPrint print, Device device, GC gc) {
    this.iters = new PrintIterator[print.prints.size ()];
    for (int i = 0; i < iters.length; i++) {
      iters[i] = ((Print) print.prints.get (i)).iterator (device, gc);
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
    PrintPiece printPiece = iter.next (width, height);

    if (printPiece != null && !iter.hasNext ()) index++;

    return printPiece;
  }

  public PrintIterator copy () {
    return new SeriesIterator (this);
  }
}