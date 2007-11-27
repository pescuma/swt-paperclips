package net.sf.paperclips;

import junit.framework.TestCase;

public class SeriesPrintTest extends TestCase {
  public void testEquals() {
    SeriesPrint series1 = new SeriesPrint();
    SeriesPrint series2 = new SeriesPrint();
    assertEquals( series1, series2 );

    series1.add( new PrintStub() );
    assertFalse( series1.equals( series2 ) );
    series2.add( new PrintStub() );
    assertEquals( series1, series2 );
  }
}
