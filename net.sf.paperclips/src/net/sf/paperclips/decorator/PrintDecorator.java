/*
 * Created on Apr 22, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.decorator;

import net.sf.paperclips.Print;

/**
 * Interface for wrapping a print in a decoration.  This is
 * @author Matthew
 */
public interface PrintDecorator {
  /**
   * Wraps the target in a decoration.  The decoration depends on the runtime class
   * of the decorator.
   * @param target the print to wrap with a decoration.
   * @return the target wrapped in a decoration.
   */
  public Print decorate(Print target);
}
