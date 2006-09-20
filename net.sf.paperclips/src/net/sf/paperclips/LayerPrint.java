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
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays its child Prints on top each other.
 * @author Matthew
 */
public class LayerPrint implements Print {
  /**
   * Constant for the default alignment of child Prints. Value is SWT.LEFT.
   */
  public static final int DEFAULT_ALIGN = SWT.LEFT;

  final List entries = new ArrayList ();

  /**
   * Constructs a new LayerPrint.
   */
  public LayerPrint () {}

  /**
   * Adds the given Print to this LayerPrint using the default alignment.
   * @param print the Print to add.
   * @see #DEFAULT_ALIGN
   */
  public void add (Print print) {
    entries.add (new LayerEntry (print, DEFAULT_ALIGN));
  }

  /**
   * Adds the given Print to this LayerPrint using the specified alignment.
   * @param print the Print to add.
   * @param align the alignment for the Print. May be one of SWT.LEFT,
   *          SWT.CENTER, or SWT.RIGHT.
   */
  public void add (Print print, int align) {
    entries.add (new LayerEntry (print, align));
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new LayerIterator (this, device, gc);
  }

  /**
   * Returns "PaperClips print job". This method is invoked by PrintUtil to
   * determine the name of the print job. Override this method to change this
   * default.
   */
  public String toString () {
    return "PaperClips print job";
  }
}

class LayerEntry {
  final Print print;
  final int align;
  PrintIterator iterator;

  LayerEntry (Print print, int align) {
    if (print == null) throw new NullPointerException();
    this.print = print;
    this.align = checkAlign (align);
  }

  LayerEntry (LayerEntry that) {
    this.print = that.print;
    this.align = that.align;
    this.iterator = that.iterator;
    if (iterator != null)
      iterator = iterator.copy();
  }

  private static int checkAlign (int align) {
    if (align == SWT.LEFT || align == SWT.CENTER || align == SWT.RIGHT)
      return align;

    throw new IllegalArgumentException (
        "Alignment must be one of SWT.LEFT, SWT.CENTER, or SWT.RIGHT");
  }

  LayerEntry copy () {
    return new LayerEntry (this);
  }
}

class LayerIterator implements PrintIterator {
  LayerEntry[] entries;

  LayerIterator (LayerPrint print, Device device, GC gc) {
    entries = new LayerEntry[print.entries.size ()];

    for (int i = 0; i < entries.length; i++) {
      entries[i] = ((LayerEntry) print.entries.get (i)).copy ();
      entries[i].iterator = entries[i].print.iterator (device, gc);
    }
  }

  LayerIterator (LayerIterator that) {
    this.entries = (LayerEntry[]) that.entries.clone ();
    for (int i = 0; i < entries.length; i++)
      entries[i] = entries[i].copy();
  }

  public boolean hasNext () {
    for (int i = 0; i < entries.length; i++)
      if (entries[i].iterator.hasNext ())
        return true;
    return false;
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();
    List pieces = new ArrayList ();
    LayerEntry[] entries = (LayerEntry[]) this.entries.clone();

    for (int i = 0; i < entries.length; i++) {
      LayerEntry entry = entries[i];
      if (entry.iterator.hasNext ()) {
        PrintPiece piece = PaperClips.next(entry.iterator, width, height);

        if (piece == null) {
          for (Iterator iter = pieces.iterator(); iter.hasNext(); )
            ((PrintPiece) iter.next()).dispose();
          return null;
        }

        CompositeEntry c_entry;
        switch (entry.align) {
        case SWT.CENTER:
          c_entry = new CompositeEntry (piece, new Point ((width - piece
              .getSize ().x) / 2, 0));
          break;
        case SWT.RIGHT:
          c_entry = new CompositeEntry (piece, new Point (width
              - piece.getSize ().x, 0));
          break;
        case SWT.LEFT:
        default:
          c_entry = new CompositeEntry (piece, new Point (0, 0));
          break;
        }
        pieces.add (c_entry);
      }
    }

    // Replace instance entries with the entries that were just consumed.
    this.entries = entries;

    return new CompositePiece (pieces);
  }

  Point computeSize (PrintSizeStrategy strategy) {
    Point size = new Point (0, 0);
    for (int i = 0; i < entries.length; i++) {
      LayerEntry entry = entries[i];
      Point entrySize = strategy.computeSize (entry.iterator);
      size.x = Math.max (size.x, entrySize.x);
      size.y = Math.max (size.y, entrySize.y);
    }
    return size;
  }

  public Point minimumSize () {
    return computeSize (PrintSizeStrategy.MINIMUM);
  }

  public Point preferredSize () {
    return computeSize (PrintSizeStrategy.PREFERRED);
  }

  public PrintIterator copy () {
    return new LayerIterator (this);
  }
}