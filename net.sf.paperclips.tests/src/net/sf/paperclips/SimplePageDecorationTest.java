package net.sf.paperclips;

import junit.framework.TestCase;

public class SimplePageDecorationTest extends TestCase {
  public void testEquals() {
    Object obj = new SimplePageDecoration( new PrintStub( 0 ) );
    assertEquals( obj, new SimplePageDecoration( new PrintStub( 0 ) ) );
    assertFalse( obj.equals( new SimplePageDecoration( new PrintStub( 1 ) ) ) );
  }
}
