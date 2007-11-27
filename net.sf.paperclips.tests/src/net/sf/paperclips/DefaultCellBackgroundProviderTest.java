package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;


public class DefaultCellBackgroundProviderTest extends TestCase {
  public void testEquals_equivalent() {
    DefaultCellBackgroundProvider provider1 = new DefaultCellBackgroundProvider();
    DefaultCellBackgroundProvider provider2 = new DefaultCellBackgroundProvider();
    assertEquals( provider1, provider2 );

    provider1 = new DefaultCellBackgroundProvider( new CellBackgroundProviderStub() );
    assertFalse( provider1.equals( provider2 ) );
    provider2 = new DefaultCellBackgroundProvider( new CellBackgroundProviderStub() );
    assertEquals( provider1, provider2 );

    provider1.setBackground( new RGB( 0, 0, 0 ) );
    assertFalse( provider1.equals( provider2 ) );
    provider2.setBackground( new RGB( 0, 0, 0 ) );
    assertEquals( provider1, provider2 );
  }
}
