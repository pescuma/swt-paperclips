/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import junit.framework.TestCase;

public class SimplePageDecorationTest extends TestCase {
  public void testEquals() {
    Object obj = new SimplePageDecoration( new PrintStub( 0 ) );
    assertEquals( obj, new SimplePageDecoration( new PrintStub( 0 ) ) );
    assertFalse( obj.equals( new SimplePageDecoration( new PrintStub( 1 ) ) ) );
  }
}
