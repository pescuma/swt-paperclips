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

class BorderStub implements Border {
  public boolean equals( Object obj ) {
    return EqualsUtil.sameClass( this, obj );
  }

  public BorderPainter createPainter( Device device, GC gc ) {
    return null;
  }
}