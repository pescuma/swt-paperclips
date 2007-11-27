package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class StyledTextPrintTest extends TestCase {
  public void testEquals() {
    StyledTextPrint styled1 = new StyledTextPrint();
    StyledTextPrint styled2 = new StyledTextPrint();
    assertEquals( styled1, styled2 );

    styled1.append( new PrintStub() );
    assertFalse( styled1.equals( styled2 ) );
    styled2.append( new PrintStub() );
    assertEquals( styled1, styled2 );

    styled1.setStyle( new TextStyle().align( SWT.CENTER ) );
    assertFalse( styled1.equals( styled2 ) );
    styled2.setStyle( new TextStyle().align( SWT.CENTER ) );
    assertEquals( styled1, styled2 );
  }
}
