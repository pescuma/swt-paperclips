package net.sf.paperclips;

import junit.framework.TestCase;

public class ScalePrintTest extends TestCase {
  public void testEquals() {
    ScalePrint scale1 = new ScalePrint( new PrintStub( 0 ), null );
    ScalePrint scale2 = new ScalePrint( new PrintStub( 0 ), null );
    assertEquals( scale1, scale2 );

    scale1 = new ScalePrint( new PrintStub( 1 ), null );
    assertFalse( scale1.equals( scale2 ) );
    scale2 = new ScalePrint( new PrintStub( 1 ), null );
    assertEquals( scale1, scale2 );

    scale1 = new ScalePrint( new PrintStub( 1 ), new Double( 0.5 ) );
    assertFalse( scale1.equals( scale2 ) );
    scale2 = new ScalePrint( new PrintStub( 1 ), new Double( 0.5 ) );
    assertEquals( scale1, scale2 );
  }
}
