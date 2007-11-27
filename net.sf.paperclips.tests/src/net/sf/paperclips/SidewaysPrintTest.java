package net.sf.paperclips;

import junit.framework.TestCase;

public class SidewaysPrintTest extends TestCase {
  public void testEquals() {
    SidewaysPrint sideways1 = new SidewaysPrint( new PrintStub( 0 ), 90 );
    SidewaysPrint sideways2 = new SidewaysPrint( new PrintStub( 0 ), 90 );
    assertEquals( sideways1, sideways2 );

    sideways1 = new SidewaysPrint( new PrintStub( 1 ), 90 );
    assertFalse( sideways1.equals( sideways2 ) );
    sideways2 = new SidewaysPrint( new PrintStub( 1 ), 90 );
    assertEquals( sideways1, sideways2 );

    sideways1 = new SidewaysPrint( new PrintStub( 1 ), 180 );
    assertFalse( sideways1.equals( sideways2 ) );
    sideways2 = new SidewaysPrint( new PrintStub( 1 ), 180 );
    assertEquals( sideways1, sideways2 );
  }
}
