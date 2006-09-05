/*
 * Created on Apr 24, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.decorator;

import net.sf.paperclips.Print;

/**
 * Decorates prints with multiple decorators.
 * @author Administrator
 */
public class CompoundDecorator implements PrintDecorator {
  private final PrintDecorator[] decorators;

  /**
   * Constructs a CompoundDecorator.
   * @param decorators the decorators, in order from innermost to outermost.
   */
  public CompoundDecorator(PrintDecorator[] decorators) {
    if (decorators == null) throw new NullPointerException();
    for (int i = 0; i < decorators.length; i++)
      if (decorators[i] == null) throw new NullPointerException();
    this.decorators = (PrintDecorator[]) decorators.clone ();
  }

  public Print decorate (Print target) {
    Print result = target;
    for (int i = 0; i < decorators.length; i++)
      result = decorators[i].decorate (target);
    return result;
  }
}
