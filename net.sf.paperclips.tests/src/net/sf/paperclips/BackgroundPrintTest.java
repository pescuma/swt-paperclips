package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

public class BackgroundPrintTest extends TestCase {
  public void testConstructor_nullArguments() {
    try {
      new BackgroundPrint( null, new RGB( 0, 0, 0 ) );
      fail( "Expected IllegalArgumentException" );
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new BackgroundPrint( new PrintStub(), null );
      fail( "Expected IllegalArgumentException" );
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals() {
    Print background = new BackgroundPrint( new PrintStub( 0 ), new RGB( 0, 0, 0 ) );
    assertEquals( background, new BackgroundPrint( new PrintStub( 0 ), new RGB( 0, 0, 0 ) ) );
    assertFalse( background.equals( new BackgroundPrint( new PrintStub( 1 ), new RGB( 0, 0, 0 ) ) ) );
    assertFalse( background.equals( new BackgroundPrint( new PrintStub( 0 ), new RGB( 1, 1, 1 ) ) ) );
  }
}
