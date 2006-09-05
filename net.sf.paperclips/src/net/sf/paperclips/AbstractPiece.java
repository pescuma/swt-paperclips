/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * An abstract PrintPiece class.
 * @author Matthew
 */
public abstract class AbstractPiece implements PrintPiece {
  /**
   * The device being printed to.
   */
  protected final Device device;

  /**
   * A GC for drawing on the print device.
   */
  protected final GC gc;

  private final Point size;

  /**
   * Constructs an AbstractPiece.
   * @param device the device being printed to.
   * @param gc a GC for drawing on the print device.
   * @param size the value to be returned by getSize().
   */
  protected AbstractPiece (Device device, GC gc, Point size) {
    if (device == null || gc == null || size == null)
      throw new NullPointerException();
    this.device = device;
    this.gc = gc;
    this.size = size;
  }

  /**
   * Constructos an AbstractPiece.
   * @param iter an AbstractIterator containing references to a Device and GC
   *          which will be used for printing.
   * @param size the value to be returned by getSize().
   */
  protected AbstractPiece (AbstractIterator iter, Point size) {
    this (iter.device, iter.gc, size);
  }

  public final Point getSize () {
    return new Point (size.x, size.y);
  }
}
