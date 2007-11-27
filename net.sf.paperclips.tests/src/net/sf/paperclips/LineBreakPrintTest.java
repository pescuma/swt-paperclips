/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public class LineBreakPrintTest extends TestCase {
  public void testConstructor_invalidArgument() {
    try {
      new LineBreakPrint( null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals() {
    FontData fontData = new FontData( "Arial", 14, SWT.NORMAL );
    LineBreakPrint lineBreakPrint = new LineBreakPrint( fontData );

    assertEquals( lineBreakPrint, new LineBreakPrint( fontData ) );
    assertFalse( lineBreakPrint.equals( new LineBreakPrint( new FontData( "Arial", 12, SWT.NORMAL ) ) ) );
  }
}
