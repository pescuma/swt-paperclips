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

public class GridCellTest extends TestCase {
  public void testEquals() {
    GridCell cell = new GridCell( SWT.DEFAULT, SWT.DEFAULT, new PrintStub( 0 ), 1 );
    assertTrue( cell.equals( new GridCell( SWT.DEFAULT, SWT.DEFAULT, new PrintStub( 0 ), 1 ) ) );
    assertFalse( cell.equals( new GridCell( SWT.CENTER, SWT.DEFAULT, new PrintStub( 0 ), 1 ) ) );
    assertFalse( cell.equals( new GridCell( SWT.DEFAULT, SWT.CENTER, new PrintStub( 0 ), 1 ) ) );
    assertFalse( cell.equals( new GridCell( SWT.DEFAULT, SWT.DEFAULT, new PrintStub( 1 ), 1 ) ) );
    assertFalse( cell.equals( new GridCell( SWT.DEFAULT, SWT.DEFAULT, new PrintStub( 0 ), 2 ) ) );
  }
}
