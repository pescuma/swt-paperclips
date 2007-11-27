package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Point;

public class EmptyPrintTest extends TestCase {
  public void testConstructor_invalidArguments() {
    try {
      new EmptyPrint( null );
      fail();
    }
    catch ( NullPointerException expected ) {}

    try {
      new EmptyPrint( new Point( -1, 0 ) );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new EmptyPrint( new Point( 0, -1 ) );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new EmptyPrint( -1, 0 );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new EmptyPrint( 0, -1 );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals_equivalent() {
    assertEquals( new EmptyPrint(), new EmptyPrint( 0, 0 ) );
  }

  public void testEquals_different() {
    EmptyPrint emptyPrint = new EmptyPrint( 0, 0 );
    assertFalse( emptyPrint.equals( new EmptyPrint( 1, 0 ) ) );
    assertFalse( emptyPrint.equals( new EmptyPrint( 0, 1 ) ) );
  }
}
