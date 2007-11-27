package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

public class AlignPrintTest extends TestCase {
  public void testConstructor_nullArgument() {
    try {
      new AlignPrint( null, SWT.DEFAULT, SWT.DEFAULT );
      fail( "Expected IllegalArgumentException" );
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testConstructor_applyDefaultAlignments() {
    AlignPrint print = new AlignPrint( new PrintStub(), SWT.DEFAULT, SWT.DEFAULT );
    Point alignment = print.getAlignment();
    assertEquals( SWT.LEFT, alignment.x );
    assertEquals( SWT.TOP, alignment.y );
  }

  public void testEquals_equivalent() {
    Print p1 = new AlignPrint( new PrintStub(), SWT.CENTER, SWT.BOTTOM );
    Print p2 = new AlignPrint( new PrintStub(), SWT.CENTER, SWT.BOTTOM );
    assertEquals( p1, p2 );
  }

  public void testEquals_different() {
    final PrintStub target = new PrintStub( 0 );

    Print print = new AlignPrint( target, SWT.DEFAULT, SWT.DEFAULT );
    assertFalse( print.equals( new AlignPrint( new PrintStub( 1 ), SWT.DEFAULT, SWT.DEFAULT ) ) );
    assertFalse( print.equals( new AlignPrint( new PrintStub( 0 ), SWT.CENTER, SWT.DEFAULT ) ) );
    assertFalse( print.equals( new AlignPrint( new PrintStub( 0 ), SWT.DEFAULT, SWT.CENTER ) ) );
  }
}
