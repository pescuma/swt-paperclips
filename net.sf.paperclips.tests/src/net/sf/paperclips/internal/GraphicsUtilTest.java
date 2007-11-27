package net.sf.paperclips.internal;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

public class GraphicsUtilTest extends TestCase {
  public void testDeriveRGB() {
    assertEquals( new RGB( 255, 0, 0 ), GraphicsUtil.deriveRGB( 0xFF0000 ) );
    assertEquals( new RGB( 0, 255, 0 ), GraphicsUtil.deriveRGB( 0x00FF00 ) );
    assertEquals( new RGB( 0, 0, 255 ), GraphicsUtil.deriveRGB( 0x0000FF ) );
  }
}
