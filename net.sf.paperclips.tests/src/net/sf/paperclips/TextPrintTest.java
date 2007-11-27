package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class TextPrintTest extends TestCase {
  public void testEquals() {
    TextPrint text1 = new TextPrint( "text" );
    TextPrint text2 = new TextPrint( "text" );
    assertEquals( text1, text2 );

    text1.setStyle( new TextStyle().align( SWT.CENTER ) );
    assertFalse( text1.equals( text2 ) );
    text2.setStyle( new TextStyle().align( SWT.CENTER ) );
    assertEquals( text1, text2 );

    text1.setWordSplitting( false );
    assertFalse( text1.equals( text2 ) );
    text2.setWordSplitting( false );
    assertEquals( text1, text2 );
  }
}
