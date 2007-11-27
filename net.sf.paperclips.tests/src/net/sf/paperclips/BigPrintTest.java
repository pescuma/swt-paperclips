package net.sf.paperclips;

import junit.framework.TestCase;

public class BigPrintTest extends TestCase {
  public void testConstructor_nullArgument() {
    try {
      new BigPrint( null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals() {
    Print print = new BigPrint( new PrintStub( 0 ) );
    assertEquals( print, new BigPrint( new PrintStub( 0 ) ) );
    assertFalse( print.equals( new BigPrint( new PrintStub( 1 ) ) ) );
  }
}
