package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

public class LineBorderTest extends TestCase {
  public void testEquals() {
    LineBorder border1 = new LineBorder();
    LineBorder border2 = new LineBorder();
    assertEquals( border1, border2 );

    border1.setGapSize( 10 );
    assertFalse( border1.equals( border2 ) );
    border2.setGapSize( 10 );
    assertEquals( border1, border2 );

    border1.setLineWidth( 10 );
    assertFalse( border1.equals( border2 ) );
    border2.setLineWidth( 10 );
    assertEquals( border1, border2 );

    border1.setRGB( new RGB( 127, 127, 127 ) );
    assertFalse( border1.equals( border2 ) );
    border2.setRGB( new RGB( 127, 127, 127 ) );
    assertEquals( border1, border2 );
  }
}
