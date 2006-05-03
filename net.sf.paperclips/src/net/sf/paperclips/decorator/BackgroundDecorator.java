/*
 * Created on Apr 24, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.decorator;

import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.BackgroundPrint;
import net.sf.paperclips.Print;

/**
 * Decorates prints with a background color.
 * @author Administrator
 */
public class BackgroundDecorator implements PrintDecorator {
  private final RGB background;

  /**
   * Constructs a BackgroundDecorator with the given background.
   * @param background the background color.
   */
  public BackgroundDecorator(RGB background) {
    if (background == null) throw new NullPointerException();
    this.background = background;
  }

  public Print decorate (Print target) {
    return new BackgroundPrint(target, background);
  }
}
