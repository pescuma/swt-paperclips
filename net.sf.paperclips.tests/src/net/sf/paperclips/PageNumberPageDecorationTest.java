package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class PageNumberPageDecorationTest extends TestCase {
  public void testEquals() {
    PageNumberPageDecoration decoration1 = new PageNumberPageDecoration();
    PageNumberPageDecoration decoration2 = new PageNumberPageDecoration();
    assertEquals( decoration1, decoration2 );

    decoration1.setRGB( new RGB( 127, 127, 127 ) );
    assertFalse( decoration1.equals( decoration2 ) );
    decoration2.setRGB( new RGB( 127, 127, 127 ) );
    assertEquals( decoration1, decoration2 );

    decoration1.setAlign( SWT.CENTER );
    assertFalse( decoration1.equals( decoration2 ) );
    decoration2.setAlign( SWT.CENTER );
    assertEquals( decoration1, decoration2 );

    decoration1.setFontData( new FontData( "Arial", 12, SWT.BOLD ) );
    assertFalse( decoration1.equals( decoration2 ) );
    decoration2.setFontData( new FontData( "Arial", 12, SWT.BOLD ) );
    assertEquals( decoration1, decoration2 );

    decoration1.setFormat( new PageNumberFormatStub() );
    assertFalse( decoration1.equals( decoration2 ) );
    decoration2.setFormat( new PageNumberFormatStub() );
    assertEquals( decoration1, decoration2 );
  }
}