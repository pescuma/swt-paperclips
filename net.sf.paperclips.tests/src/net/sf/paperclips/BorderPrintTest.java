package net.sf.paperclips;

import junit.framework.TestCase;

public class BorderPrintTest extends TestCase {
  public void testConstructor_nullArguments() {
    try {
      new BorderPrint( null, new BorderStub() );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new BorderPrint( new PrintStub(), null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals_equivalent() {
    assertEquals( new BorderPrint( new PrintStub(), new BorderStub() ), new BorderPrint( new PrintStub(),
                                                                                         new BorderStub() ) );
  }

  public void testEquals_different() {
    BorderPrint borderPrint = new BorderPrint( new PrintStub( 0 ), new BorderStub() );
    assertFalse( borderPrint.equals( new BorderPrint( new PrintStub( 1 ), new BorderStub() ) ) );
    assertFalse( borderPrint.equals( new BorderPrint( new PrintStub( 0 ), new BorderStub() {} ) ) );
  }
}
