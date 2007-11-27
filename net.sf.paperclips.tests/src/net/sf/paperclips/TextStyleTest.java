package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class TextStyleTest extends TestCase {
  public void testEquals() {
    TextStyle style1 = new TextStyle();
    TextStyle style2 = new TextStyle();
    assertEquals( style1, style2 );

    style1 = style1.align( SWT.CENTER );
    assertFalse( style1.equals( style2 ) );
    style2 = style2.align( SWT.CENTER );
    assertEquals( style1, style2 );

    style1 = style1.background( new RGB( 127, 127, 127 ) );
    assertFalse( style1.equals( style2 ) );
    style2 = style2.background( new RGB( 127, 127, 127 ) );
    assertEquals( style1, style2 );

    style1 = style1.font( new FontData( "Arial", 12, SWT.BOLD ) );
    assertFalse( style1.equals( style2 ) );
    style2 = style2.font( new FontData( "Arial", 12, SWT.BOLD ) );
    assertEquals( style1, style2 );

    style1 = style1.foreground( new RGB( 127, 127, 127 ) );
    assertFalse( style1.equals( style2 ) );
    style2 = style2.foreground( new RGB( 127, 127, 127 ) );
    assertEquals( style1, style2 );

    style1 = style1.strikeout();
    assertFalse( style1.equals( style2 ) );
    style2 = style2.strikeout();
    assertEquals( style1, style2 );

    style1 = style1.underline();
    assertFalse( style1.equals( style2 ) );
    style2 = style2.underline();
    assertEquals( style1, style2 );
  }
}
