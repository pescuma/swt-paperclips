package net.sf.paperclips;

import junit.framework.TestCase;

public class NoBreakPrintTest extends TestCase {
  public void testConstructor() {
    try {
      new NoBreakPrint( null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals() {
    NoBreakPrint noBreak = new NoBreakPrint( new PrintStub( 0 ) );
    assertEquals( noBreak, new NoBreakPrint( new PrintStub( 0 ) ) );
    assertFalse( noBreak.equals( new NoBreakPrint( new PrintStub( 1 ) ) ) );
  }
}
