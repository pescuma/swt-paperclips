package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.*;

public class ImagePrintTest extends TestCase {
  public void testEquals() {
    ImagePrint image1 = new ImagePrint( createImageData( 100 ) );
    ImagePrint image2 = new ImagePrint( createImageData( 100 ) );
    assertEquals( image1, image2 );

    image1 = new ImagePrint( createImageData( 200 ) );
    assertFalse( image1.equals( image2 ) );
    image2 = new ImagePrint( createImageData( 200 ) );
    assertEquals( image1, image2 );

    image1.setDPI( new Point( 100, 100 ) );
    assertFalse( image1.equals( image2 ) );
    image2.setDPI( 100, 100 );
    assertEquals( image1, image2 );

    image1.setSize( new Point( 100, 100 ) );
    assertFalse( image1.equals( image2 ) );
    image2.setSize( 100, 100 );
    assertEquals( image1, image2 );
  }

  private ImageData createImageData( int size ) {
    return new ImageData( size, size, 24, new PaletteData( 0xFF0000, 0x00FF00, 0x0000FF ) );
  }
}
