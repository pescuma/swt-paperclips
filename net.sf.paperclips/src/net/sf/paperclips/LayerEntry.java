/*******************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Device;

/**
 * Instances in this class represent an entry in a LayerPrint.
 * @author Matthew Hall
 */
public class LayerEntry {
  final Print target;
  final int align;

  LayerEntry (Print target, int align) {
    if (target == null) throw new NullPointerException();
    this.target = target;
    this.align = checkAlign (align);
  }

  LayerEntry (LayerEntry that) {
    this.target = that.target;
    this.align = that.align;
  }

  /**
   * Returns the target print of this entry.
   * @return the target print of this entry.
   */
  public Print getTarget() {
  	return target;
  }

  /**
   * Returns the horizontal alignment applied to the target.
   * @return the horizontal alignment applied to the target.
   */
  public int getHorizontalAlignment() {
  	return align;
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

  LayerEntryIterator iterator(Device device, GC gc) {
  	return new LayerEntryIterator(this, device, gc);
  }
}