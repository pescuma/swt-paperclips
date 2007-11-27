package net.sf.paperclips.internal;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

public class ImageDataUtilTest extends TestCase {
  public void testAreEqual_opaque() {
    ImageData imageData = createImageData( 100, 0, (byte) 0xFF );
    assertTrue( ImageDataUtil.areEqual( imageData, createImageData( 100, 0, (byte) 0xFF ) ) );
    assertFalse( ImageDataUtil.areEqual( imageData, createImageData( 101, 0, (byte) 0xFF ) ) );
    assertFalse( ImageDataUtil.areEqual( imageData, createImageData( 100, 1, (byte) 0xFF ) ) );
  }

  public void testAreEqual_transparent() {
    assertTrue( ImageDataUtil.areEqual( createImageData( 100, 0xFFFFFF, (byte) 0x7F ),
                                        createImageData( 100, 0xFFFFFF, (byte) 0x7F ) ) );
    assertFalse( ImageDataUtil.areEqual( createImageData( 99, 0xFFFFFF, (byte) 0x7F ),
                                         createImageData( 99, 0xFFFFFF, (byte) 0xFF ) ) );
    assertTrue( ImageDataUtil.areEqual( createImageData( 100, 0x000000, (byte) 0x00 ),
                                        createImageData( 100, 0xFFFFFF, (byte) 0x00 ) ) );
  }

  private ImageData createImageData( int size, int color, byte alpha ) {
    ImageData imageData = new ImageData( size, size, 24, new PaletteData( 0xFF0000, 0x00FF00, 0x0000FF ) );
    int[] pixels = new int[size];
    byte[] alphas = new byte[size];
    for ( int x = 0; x < size; x++ ) {
      pixels[x] = color;
      alphas[x] = alpha;
    }
    for ( int y = 0; y < size; y++ ) {
      imageData.setPixels( 0, y, size, pixels, 0 );
      imageData.setAlphas( 0, y, size, alphas, 0 );
    }
    return imageData;
  }
}
