package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class GridColumnTest extends TestCase {
  public void testEquals_equivalent() {
    GridColumn c1 = GridColumn.parse( "l:p:g" );
    GridColumn c2 = new GridColumn( SWT.LEFT, GridPrint.PREFERRED, 1 );
    assertEquals( c1, c2 );
  }

  public void testEquals_different() {
    GridColumn gc = new GridColumn( SWT.LEFT, SWT.DEFAULT, 0 );
    assertFalse( gc.equals( new GridColumn( SWT.CENTER, SWT.DEFAULT, 0 ) ) );
    assertFalse( gc.equals( new GridColumn( SWT.LEFT, GridPrint.PREFERRED, 0 ) ) );
    assertFalse( gc.equals( new GridColumn( SWT.LEFT, SWT.DEFAULT, 1 ) ) );
  }
}
