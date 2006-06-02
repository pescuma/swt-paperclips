/*
 * Created on May 12, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * A pluggable "look" for a GridPrint.
 * @author Matthew Hall
 */
public interface GridLook {
  /**
   * Returns a GridLookPainter for painting the GridLook.
   * @param device the device to paint on.
   * @param gc the graphics context for painting.
   * @return a GridLookPainter for painting the GridLook.
   */
  public GridLookPainter getPainter(Device device, GC gc);
}
