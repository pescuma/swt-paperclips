package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class LayerEntryTest extends TestCase {
  public void testConstructor_invalidArguments() {
    try {
      new LayerEntry( null, SWT.LEFT );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    assertEquals( SWT.LEFT, new LayerEntry( new PrintStub(), 0 ).align );
  }

  public void testEquals() {
    LayerEntry entry = new LayerEntry( new PrintStub( 0 ), SWT.LEFT );
    assertEquals( entry, new LayerEntry( new PrintStub( 0 ), SWT.LEFT ) );
    assertFalse( entry.equals( new LayerEntry( new PrintStub( 1 ), SWT.LEFT ) ) );
    assertFalse( entry.equals( new LayerEntry( new PrintStub( 0 ), SWT.CENTER ) ) );
  }
}
