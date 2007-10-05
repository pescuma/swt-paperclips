/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.internal;

import org.eclipse.swt.graphics.*;

import net.sf.paperclips.TextStyle;

/**
 * Instances of this class are the system resource counterparts of the {@link TextStyle} class. This class
 * allocates and manages the resources required to apply a TextStyle on a given device, and will dispose all
 * allocated resources when needed.
 * @author Matthew Hall
 */
public class TextStyleResource {
  private final Device    device;
  private final TextStyle data;

  private Font            font;
  private Color           foreground;
  private Color           background;

  /**
   * Constructs a new TextStyleResource.
   * @param device the device against which resources will be created.
   * @param style the text style defining which fonts and colors are needed.
   */
  public TextStyleResource( Device device, TextStyle style ) {
    NullUtil.notNull( device, style );
    this.data = style;
    this.device = device;
  }

  /**
   * Allocates (if needed) and returns a system Font corresponding to the FontData in the TextStyle (may be
   * null).
   * @return the system Font corresponding to the FontData in the TextStyle (may be null).
   */
  public Font getFont() {
    FontData fontData = data.getFontData();
    if ( font == null && fontData != null )
      font = new Font( device, fontData );
    return font;
  }

  /**
   * Allocates (if needed) and returns a system Color corresponding to the foreground RGB in the TextStyle
   * (may be null).
   * @return the system Color corresponding to the foreground RGB in the TextStyle (may be null).
   */
  public Color getForeground() {
    RGB rgb = data.getForeground();
    if ( foreground == null && rgb != null )
      foreground = new Color( device, rgb );
    return foreground;
  }

  /**
   * Allocates (if needed) and returns a system Color corresponding to the background RGB in the TextStyle
   * (may be null).
   * @return the system Color corresponding to the background RGB in the TextStyle (may be null).
   */
  public Color getBackground() {
    RGB rgb = data.getBackground();
    if ( background == null && rgb != null )
      background = new Color( device, rgb );
    return background;
  }

  /**
   * Returns the horizontal alignment applied to the text. This is just the alignment from the TextStyle,
   * provided here for convenience.
   * @return the horizontal alignment applied to the text.
   */
  public int getAlignment() {
    return data.getAlignment();
  }

  /**
   * Returns whether the text is drawn with an underline. This is just the underline flag from the TextStyle,
   * provided here for convenience.
   * @return whether the text is drawn with an underline.
   */
  public boolean getUnderline() {
    return data.getUnderline();
  }

  /**
   * Returns whether the text is drawn with a strikeout. This is just the strikeout flag from the TextStyle,
   * provided here for convenience.
   * @return whether the text is drawn with a strikeout.
   */
  public boolean getStrikeout() {
    return data.getStrikeout();
  }

  /**
   * Disposes all system resources allocated by this TextStyleResource instance.
   */
  public void dispose() {
    disposeFont();
    disposeForeground();
    disposeBackground();
  }

  private void disposeFont() {
    if ( font != null ) {
      font.dispose();
      font = null;
    }
  }

  private void disposeForeground() {
    if ( foreground != null ) {
      foreground.dispose();
      foreground = null;
    }
  }

  private void disposeBackground() {
    if ( background != null ) {
      background.dispose();
      background = null;
    }
  }
}
