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

/**
 * An abstract PrintIterator class which maintains references to the device and
 * gc arguments passed to {@link Print#iterator(Device, GC) }.
 * @author Matthew
 */
public abstract class AbstractIterator implements PrintIterator {
  /**
   * The device being printed to.
   */
  protected final Device device;

  /**
   * A GC used for drawing on the print device.
   */
  protected final GC gc;

  /**
   * Constructs an AbstractIterator with the given Device and GC.
   * @param device the device being printed to.
   * @param gc a GC used for drawing on the print device.
   */
  protected AbstractIterator (Device device, GC gc) {
    this.device = BeanUtils.checkNull (device);
    this.gc = BeanUtils.checkNull (gc);
  }

  /**
   * Copy constructor.
   * @param that the AbstractIterator being copied.
   */
  protected AbstractIterator (AbstractIterator that) {
    this.device = that.device;
    this.gc = that.gc;
  }
}