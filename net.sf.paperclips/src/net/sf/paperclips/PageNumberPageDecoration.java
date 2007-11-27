/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.internal.*;

/**
 * A PageDecoration which displays the page number. This convenience class helps avoid the need for writing a
 * new PageDecoration class if only a page number is needed. Getter and setter methods are provided for all
 * the properties available in the PagePrint class itself.
 * @author Matthew Hall
 */
public class PageNumberPageDecoration implements PageDecoration {
  FontData         fontData = new FontData();
  int              align    = SWT.LEFT;
  RGB              rgb      = new RGB( 0, 0, 0 );           // black
  PageNumberFormat format   = new DefaultPageNumberFormat();

  /**
   * Constructs a PageNumberPageDecoration with default font, alignment, and page number format.
   */
  public PageNumberPageDecoration() {}

  /**
   * Constructs a PageNumberPageDecoration with the given alignment.
   * @param align horizontal text alignment.
   */
  public PageNumberPageDecoration( int align ) {
    setAlign( align );
  }

  public boolean equals( Object obj ) {
    if ( !EqualsUtil.sameClass( this, obj ) )
      return false;

    PageNumberPageDecoration that = (PageNumberPageDecoration) obj;
    return this.align == that.align && EqualsUtil.areEqual( this.fontData, that.fontData )
        && EqualsUtil.areEqual( this.rgb, that.rgb ) && EqualsUtil.areEqual( this.format, that.format );
  }

  /**
   * Returns the font.
   * @return the font.
   */
  public FontData getFontData() {
    return fontData;
  }

  /**
   * Sets the font.
   * @param fontData the new font.
   */
  public void setFontData( FontData fontData ) {
    NullUtil.notNull( fontData );
    this.fontData = fontData;
  }

  /**
   * Returns the horizontal text alignment.
   * @return the horizontal text alignment.
   */
  public int getAlign() {
    return align;
  }

  /**
   * Sets the horizontal text alignment.
   * @param align the horizontal text alignment.
   */
  public void setAlign( int align ) {
    align = checkAlign( align );
    this.align = align;
  }

  private int checkAlign( int align ) {
    return BitUtil.firstMatch( align, new int[] { SWT.LEFT, SWT.CENTER, SWT.RIGHT }, SWT.LEFT );
  }

  /**
   * Returns the text color.
   * @return the text color.
   */
  public RGB getRGB() {
    return rgb;
  }

  /**
   * Sets the text color.
   * @param rgb the new text color.
   */
  public void setRGB( RGB rgb ) {
    NullUtil.notNull( rgb );
    this.rgb = rgb;
  }

  /**
   * Returns the page number format.
   * @return the page number format.
   */
  public PageNumberFormat getFormat() {
    return format;
  }

  /**
   * Sets the page number format.
   * @param format the page number format.
   */
  public void setFormat( PageNumberFormat format ) {
    NullUtil.notNull( format );
    this.format = format;
  }

  public Print createPrint( PageNumber pageNumber ) {
    PageNumberPrint result = new PageNumberPrint( pageNumber );
    result.setFontData( fontData );
    result.setAlign( align );
    result.setPageNumberFormat( format );
    result.setRGB( rgb );
    return result;
  }
}