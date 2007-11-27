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

public class GridColumnTest extends TestCase {
  public void testEquals_equivalent() {
    GridColumn c1 = GridColumn.parse( "l:p:g" );
    GridColumn c2 = new GridColumn( SWT.LEFT, GridPrint.PREFERRED, 1 );
    assertEquals( c1, c2 );
  }

  public void testEquals_different() {
    GridColumn gc = new GridColumn( SWT.LEFT, SWT.DEFAULT, 0 );
    assertFalse( gc.equals( new GridColumn( SWT.CENTER, SWT.DEFAULT, 0 ) ) );
    assertFalse( gc.equals( new GridColumn( SWT.LEFT, GridPrint.PREFERRED, 0 ) ) );
    assertFalse( gc.equals( new GridColumn( SWT.LEFT, SWT.DEFAULT, 1 ) ) );
  }
}
