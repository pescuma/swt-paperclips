package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class LayerPrintTest extends TestCase {
  public void testEquals() {
    LayerPrint lp1 = new LayerPrint();
    LayerPrint lp2 = new LayerPrint();
    assertEquals( lp1, lp2 );

    lp1.add( new PrintStub() );
    assertFalse( lp1.equals( lp2 ) );
    lp2.add( new PrintStub() );
    assertEquals( lp1, lp2 );

    lp1.add( new PrintStub(), SWT.CENTER );
    assertFalse( lp1.equals( lp2 ) );
    lp2.add( new PrintStub(), SWT.CENTER );
    assertEquals( lp1, lp2 );
  }
}
