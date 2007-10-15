/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.decorator;

import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.BackgroundPrint;
import net.sf.paperclips.Print;
import net.sf.paperclips.internal.NullUtil;

/**
 * Decorates prints with a background color.
 * @author Matthew Hall
 */
public class BackgroundDecorator implements PrintDecorator {
  private final RGB background;

  /**
   * Constructs a BackgroundDecorator with the given background.
   * @param background the background color.
   */
  public BackgroundDecorator( RGB background ) {
    NullUtil.notNull( background );
    this.background = background;
  }

  public Print decorate( Print target ) {
    return new BackgroundPrint( target, background );
  }
}
