package net.sf.paperclips;

import junit.framework.TestCase;

public class DefaultPageNumberFormatTest extends TestCase {
  public void testEquals() {
    assertEquals( new DefaultPageNumberFormat(), new DefaultPageNumberFormat() );
  }
}
