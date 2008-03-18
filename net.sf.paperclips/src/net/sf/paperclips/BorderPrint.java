/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import net.sf.paperclips.decorator.BorderDecorator;
import net.sf.paperclips.internal.Util;

/**
 * A decorator that draws a border around the target print.
 * @see BorderDecorator
 * @author Matthew Hall
 */
public class BorderPrint implements Print {
  final Print  target;
  final Border border;

  /**
   * Constructs a BorderPrint with the given target and border.
   * @param target the print to decorate with a border.
   * @param border the border which will be drawn around the target.
   */
  public BorderPrint( Print target, Border border ) {
    Util.notNull( target, border );
    this.target = target;
    this.border = border;
  }

  public boolean equals( Object obj ) {
    if ( !Util.sameClass( this, obj ) )
      return false;
    BorderPrint that = (BorderPrint) obj;
    return Util.equal( this.target, that.target ) && Util.equal( this.border, that.border );
  }

  /**
   * Returns the wrapped print to which the border is being applied.
   * @return the wrapped print to which the border is being applied.
   */
  public Print getTarget() {
    return target;
  }

  /**
   * Returns the border being applied to the target.
   * @return the border being applied to the target.
   */
  public Border getBorder() {
    return border;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return new BorderIterator( this, device, gc );
  }
}
