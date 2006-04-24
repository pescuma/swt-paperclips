/*
 * Created on Apr 22, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.decorator;

import net.sf.paperclips.Border;
import net.sf.paperclips.BorderPrint;
import net.sf.paperclips.Print;

/**
 * Decorates prints with a border.
 * @author Matthew
 * @see BorderPrint
 * @see Border
 */
public class BorderDecorator implements PrintDecorator {
  private final Border border;

  /**
   * Constructs a BorderDecorator.
   * @param border the initial border
   */
  public BorderDecorator(Border border) {
    if (border == null) throw new NullPointerException();
    this.border = border;
  }

  public Print decorate (Print target) {
    return new BorderPrint(target, border);
  }
}
