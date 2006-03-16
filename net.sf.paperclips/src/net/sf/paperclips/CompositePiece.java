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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A composite PrintPiece for displaying child PrintPieces. This class is
 * especially useful for Print implementations that perform layout of multiple
 * child Prints.
 * @author Matthew
 */
public class CompositePiece implements PrintPiece {
  private final Point size;

  private final CompositeEntry[] entries;

  /**
   * Constructs a CompositePiece with the given entries.
   * @param entries an array of entries that make up this PrintPiece.
   */
  public CompositePiece (CompositeEntry... entries) {
    this (createList (entries));
  }

  /**
   * Constructs a CompositePrintPiece with the given entries and explicit size.
   * This constructor will increase the explicit size to completely contain any
   * child entries which extend outside the given size.
   * @param entries an array of entries that make up this PrintPiece.
   * @param size
   */
  public CompositePiece (CompositeEntry[] entries, Point size) {
    this (createList (entries), size);
  }

  private static List <CompositeEntry> createList (CompositeEntry[] entries) {
    List <CompositeEntry> result = new ArrayList <CompositeEntry> ();
    for (CompositeEntry entry : entries)
      result.add (entry);
    return result;
  }

  /**
   * Constructs a composite PrintPiece with the given entries.
   * @param entries an array of entries that make up this PrintPiece.
   */
  public CompositePiece (List <CompositeEntry> entries) {
    this (entries, new Point (0, 0));
  }

  /**
   * Constructs a composite PrintPiece with the given entries and minimum size.
   * @param entries a list of CompositeEntry objects describing the child
   *          PrintPieces.
   * @param size a hint indicating the minimum size that should be reported from
   *          getSize(). This constructor increase this size to fit any entries
   *          that extend outside the given size.
   */
  public CompositePiece (List <CompositeEntry> entries, Point size) {
    BeanUtils.checkNull (entries);
    for (CompositeEntry entry : entries)
      BeanUtils.checkNull (entry);

    this.entries = entries.toArray (new CompositeEntry[entries.size ()]);
    this.size = new Point (size.x, size.y);

    for (CompositeEntry entry : this.entries) {
      Point pieceSize = entry.piece.getSize ();
      this.size.x = Math.max (this.size.x, entry.offset.x + pieceSize.x);
      this.size.y = Math.max (this.size.y, entry.offset.y + pieceSize.y);
    }
  }

  public Point getSize () {
    return new Point (size.x, size.y);
  }

  public void paint (GC gc, int x, int y) {
    Rectangle clip = gc.getClipping ();
    for (CompositeEntry entry : entries) {
      Point size = entry.piece.getSize ();
      if (clip.intersects (x + entry.offset.x, y + entry.offset.y, size.x,
          size.y))
        entry.piece.paint (gc, x + entry.offset.x, y + entry.offset.y);
    }
  }

  public void dispose () {
    for (CompositeEntry entry : entries)
      entry.piece.dispose ();
  }
}
