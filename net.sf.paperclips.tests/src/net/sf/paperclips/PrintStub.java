/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import net.sf.paperclips.internal.EqualsUtil;

final class PrintStub implements Print {
  private int id;

  public PrintStub() {
    this( 0 );
  }

  public PrintStub( int id ) {
    this.id = id;
  }

  public boolean equals( Object obj ) {
    if ( !EqualsUtil.sameClass( this, obj ) )
      return false;

    PrintStub that = (PrintStub) obj;
    return this.id == that.id;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return null;
  }
}