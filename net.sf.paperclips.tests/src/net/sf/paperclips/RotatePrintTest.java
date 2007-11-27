package net.sf.paperclips;

import junit.framework.TestCase;

public class RotatePrintTest extends TestCase {
  public void testEquals() {
    RotatePrint rotate1 = new RotatePrint( new PrintStub( 0 ), 90 );
    RotatePrint rotate2 = new RotatePrint( new PrintStub( 0 ), 90 );
    assertEquals( rotate1, rotate2 );

    rotate1 = new RotatePrint( new PrintStub( 1 ), 90 );
    assertFalse( rotate1.equals( rotate2 ) );
    rotate2 = new RotatePrint( new PrintStub( 1 ), 90 );
    assertEquals( rotate1, rotate2 );

    rotate1 = new RotatePrint( new PrintStub( 1 ), 180 );
    assertFalse( rotate1.equals( rotate2 ) );
    rotate2 = new RotatePrint( new PrintStub( 1 ), 180 );
    assertEquals( rotate1, rotate2 );
  }
}
