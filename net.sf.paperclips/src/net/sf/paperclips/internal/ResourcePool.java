package net.sf.paperclips.internal;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import net.sf.paperclips.PaperClips;

/**
 * Manages a pool of graphics resources (fonts, colors.
 * @author Matthew Hall
 */
public class ResourcePool {
  private static Map devices = new WeakHashMap(); // Map <Device, SharedGraphics>

  /**
   * Returns a SharedGraphics which creates resources on the given device.
   * @param device the device which resources will be created on.
   * @return a SharedGraphics which creates resources on the given device.
   */
  public synchronized static ResourcePool forDevice( Device device ) {
    NullUtil.notNull( device );
    notDisposed( device );

    ResourcePool sharedGraphics = (ResourcePool) devices.get( device );
    if ( sharedGraphics == null ) {
      sharedGraphics = new ResourcePool( device );
      devices.put( device, sharedGraphics );
    }
    return sharedGraphics;
  }

  private final Device device;
  private final Map    fonts; // Map <FontData, Font>
  private final Map    colors; // Map <RGB, Color>

  private ResourcePool( Device device ) {
    this.device = device;
    this.fonts = new HashMap();
    this.colors = new HashMap();
  }

  /**
   * Returns a font for the passed in FontData.
   * @param fontData FontData describing the required font.
   * @return a font for the passed in FontData.
   */
  public Font getFont( FontData fontData ) {
    if ( fontData == null )
      return null;
    notDisposed( device );

    Font font = (Font) fonts.get( fontData );
    if ( font == null ) {
      font = new Font( device, fontData );
      fonts.put( GraphicsUtil.defensiveCopy( fontData ), font );
    }
    return font;
  }

  /**
   * Returns a color for the passed in RGB.
   * @param rgb RGB describing the required color.
   * @return a color for the passed in RGB.
   */
  public Color getColor( RGB rgb ) {
    if ( rgb == null )
      return null;
    notDisposed( device );

    Color color = (Color) colors.get( rgb );
    if ( color == null ) {
      color = new Color( device, rgb );
      colors.put( GraphicsUtil.defensiveCopy( rgb ), color );
    }
    return color;
  }

  private static void notDisposed( Device device ) {
    if ( device.isDisposed() )
      PaperClips.error( SWT.ERROR_DEVICE_DISPOSED );
  }
}
