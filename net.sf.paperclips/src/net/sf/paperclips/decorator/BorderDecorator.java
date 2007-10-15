/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.decorator;

import net.sf.paperclips.*;
import net.sf.paperclips.internal.NullUtil;

/**
 * Decorates prints with a border.
 * @author Matthew Hall
 * @see BorderPrint
 * @see Border
 */
public class BorderDecorator implements PrintDecorator {
  private final Border border;

  /**
   * Constructs a BorderDecorator.
   * @param border the initial border
   */
  public BorderDecorator( Border border ) {
    NullUtil.notNull( border );
    this.border = border;
  }

  public Print decorate( Print target ) {
    return new BorderPrint( target, border );
  }
}
