/*
 * Created on Apr 24, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.decorator;

import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.BackgroundColorPrint;
import net.sf.paperclips.Print;

/**
 * Decorates prints with a background color.
 * @author Administrator
 */
public class BackgroundColorDecorator implements PrintDecorator {
  private final RGB background;

  /**
   * Constructs a BackgroundColorDecorator with the given background.
   * @param background the background color.
   */
  public BackgroundColorDecorator(RGB background) {
    if (background == null) throw new NullPointerException();
    this.background = background;
  }

  public Print decorate (Print target) {
    return new BackgroundColorPrint(target, background);
  }
}
