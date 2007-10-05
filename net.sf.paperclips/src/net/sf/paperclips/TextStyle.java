/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Defines a set of styles that can be applied to text.
 * @author Matthew Hall
 */
public class TextStyle {
  private FontData fontData;
  private RGB      foreground;
  private RGB      background;
  private int      alignment;
  private boolean  underline;
  private boolean  strikeout;

  /**
   * Constructs a new TextStyle with default font (device-dependent), black foreground, transparent
   * background, default alignment, and the strikeout and underline flags set to false.
   */
  public TextStyle() {
    fontData = TextPrint.DEFAULT_FONT_DATA;
    foreground = new RGB( 0, 0, 0 );
    background = null;
    alignment = TextPrint.DEFAULT_ALIGN;
    underline = false;
    strikeout = false;
  }

  private TextStyle( TextStyle that ) {
    this.fontData = that.fontData;
    this.foreground = that.foreground;
    this.background = that.background;
    this.alignment = that.alignment;
    this.underline = that.underline;
    this.strikeout = that.strikeout;
  }

  private TextStyle internalFont( FontData fontData ) {
    TextStyle result = new TextStyle( this );
    result.fontData = fontData;
    return result;
  }

  /**
   * Returns a copy of this TextStyle, with the font changed to the font described by the arguments. This
   * method is equivalent to calling font( new FontData( name, height, style ) ).
   * @param name the name of the font (must not be null)
   * @param height the font height in points
   * @param style a bit or combination of NORMAL, BOLD, ITALIC
   * @return a copy of this TextStyle, with the font changed to the font described by the arguments.
   */
  public TextStyle font( String name, int height, int style ) {
    return internalFont( new FontData( name, height, style ) );
  }

  /**
   * Returns a copy of this TextStyle, with the font changed to the argument.
   * @param fontData the new font. A null value causes the font to be inherited from the enclosing elements
   *        of the document.
   * @return a copy of this TextStyle, with the font changed to the argument.
   */
  public TextStyle font( FontData fontData ) {
    return internalFont( defensiveCopy( fontData ) );
  }

  /**
   * Returns a copy of this TextStyle, with the font name changed to the argument.
   * @param name the new font name (must not be null)
   * @return a copy of this TextStyle, with the font name changed to the argument.
   */
  public TextStyle fontName( String name ) {
    return font( name, fontData.getHeight(), fontData.getStyle() );
  }

  /**
   * Returns a copy of this TextStyle, with the font height changed to the argument.
   * @param height the new font height in points
   * @return a copy of this TextStyle, with the font height changed to the argument.
   */
  public TextStyle fontHeight( int height ) {
    return font( fontData.getName(), height, fontData.getStyle() );
  }

  /**
   * Returns a copy of this TextStyle, with the font style changed to the argument.
   * @param style a bit or combination of NORMAL, BOLD, ITALIC
   * @return a copy of this TextStyle, with the font style changed to the argument.
   */
  public TextStyle fontStyle( int style ) {
    return font( fontData.getName(), fontData.getHeight(), style );
  }

  private RGB deriveRGB( final int rgb ) {
    int red = ( rgb >> 16 ) & 0xFF;
    int green = ( rgb >> 8 ) & 0xFF;
    int blue = rgb & 0xFF;
    return new RGB( red, green, blue );
  }

  private TextStyle internalForeground( RGB foreground ) {
    TextStyle result = new TextStyle( this );
    result.foreground = foreground;
    return result;
  }

  /**
   * Returns a copy of this TextStyle, with the foreground changed to the argument.
   * @param foreground the new foreground. A null value causes the foreground to be inherited from the
   *        enclosing elements of the document.
   * @return a copy of this TextStyle, with the foreground changed to the argument.
   */
  public TextStyle foreground( RGB foreground ) {
    return internalForeground( defensiveCopy( foreground ) );
  }

  /**
   * Returns a copy of this TextStyle, with the foreground changed to the color described by the arguments.
   * This method is equivalent to calling foreground(new RGB(red, green, blue)).
   * @param red the red component of the new foreground color
   * @param green the green component of the new foreground color
   * @param blue the blue component of the new foreground color
   * @return a copy of this TextStyle, with the foreground changed to the color described by the arguments.
   */
  public TextStyle foreground( int red, int green, int blue ) {
    return internalForeground( new RGB( red, green, blue ) );
  }

  /**
   * Returns a copy of this TextStyle, with the foreground changed to the color described by the argument.
   * @param rgb an integer containing the red, green and blue components in the 0xFF0000, 0x00FF00, and
   *        0x0000FF positions, respectively.
   * @return a copy of this TextStyle, with the foreground changed to the color described by the argument.
   */
  public TextStyle foreground( int rgb ) {
    return internalForeground( deriveRGB( rgb ) );
  }

  private TextStyle internalBackground( RGB background ) {
    TextStyle result = new TextStyle( this );
    result.background = background;
    return result;
  }

  /**
   * Returns a copy of this TextStyle, with the background changed to the argument.
   * @param background the new background. A null value causes the text background to be transparent.
   * @return a copy of this TextStyle, with the background changed to the argument.
   */
  public TextStyle background( RGB background ) {
    return internalBackground( defensiveCopy( background ) );
  }

  /**
   * Returns a copy of this TextStyle, with the background changed to the color described by the arguments.
   * This method is equivalent to calling background(new RGB(red, green, blue)
   * @param red the red component of the new background color
   * @param green the green component of the new background color
   * @param blue the blue component of the new background color
   * @return a copy of this TextStyle, with the background changed to the color described by the arguments.
   */
  public TextStyle background( int red, int green, int blue ) {
    return internalBackground( new RGB( red, green, blue ) );
  }

  /**
   * Returns a copy of this TextStyle, with the background changed to the color described by the argument.
   * @param rgb an integer containing the red, green and blue components in the 0xFF0000, 0x00FF00, and
   *        0x0000FF positions, respectively.
   * @return a copy of this TextStyle, with the background changed to the color described by the argument.
   */
  public TextStyle background( int rgb ) {
    return internalBackground( deriveRGB( rgb ) );
  }

  /**
   * Returns a copy of this TextStyle, with the alignment changed to the argument.
   * @param alignment the new alignment. Must be one of SWT.LEFT, SWT.CENTER, or SWT.RIGHT. Invalid values
   *        will be changed to SWT.LEFT.
   * @return a copy of this TextStyle, with the alignment changed to the argument.
   */
  public TextStyle align( int alignment ) {
    TextStyle result = new TextStyle( this );
    result.alignment = checkAlignment( alignment );
    return result;
  }

  private int checkAlignment( int alignment ) {
    if ( ( alignment & SWT.LEFT ) == SWT.LEFT )
      return SWT.LEFT;
    else if ( ( alignment & SWT.CENTER ) == SWT.CENTER )
      return SWT.CENTER;
    else if ( ( alignment & SWT.RIGHT ) == SWT.RIGHT )
      return SWT.RIGHT;

    // no alignment bit--default to left.
    return SWT.LEFT;
  }

  /**
   * Returns a copy of this TextStyle, with the underline flag set to true.
   * @return a copy of this TextStyle, with the underline flag set to true.
   */
  public TextStyle underline() {
    return underline( true );
  }

  /**
   * Returns a copy of this TextStyle, with the underline flag set to the argument.
   * @param underline the new underline flag.
   * @return a copy of this TextStyle, with the underline flag set to the argument.
   */
  public TextStyle underline( boolean underline ) {
    TextStyle result = new TextStyle( this );
    result.underline = underline;
    return result;
  }

  /**
   * Returns a copy of this TextStyle, with the strikeout flag set to true.
   * @return a copy of this TextStyle, with the strikeout flag set to true.
   */
  public TextStyle strikeout() {
    return strikeout( true );
  }

  /**
   * Returns a copy of this TextStyle, with the strikeout flag set to the argument.
   * @param strikeout the new strikeout flag.
   * @return a copy of this TextStyle, with the strikeout flag set to the argument.
   */
  public TextStyle strikeout( boolean strikeout ) {
    TextStyle result = new TextStyle( this );
    result.strikeout = strikeout;
    return result;
  }

  /**
   * Returns the font applied to the text.
   * @return the font applied to the text.
   */
  public FontData getFontData() {
    return defensiveCopy( fontData );
  }

  /**
   * Returns the text foreground color. A null value indicates that the foreground color will be inherited
   * from the enclosing elements of the document.
   * @return the text foreground color.
   */
  public RGB getForeground() {
    return defensiveCopy( foreground );
  }

  /**
   * Returns the text background color. A null value indicates that the background will be transparent.
   * @return the text background color. A null value indicates that the background will be transparent.
   */
  public RGB getBackground() {
    return defensiveCopy( background );
  }

  /**
   * Returns the text alignment. Possible values include SWT.LEFT, SWT.CENTER, or SWT.RIGHT.
   * @return the text alignment.
   */
  public int getAlignment() {
    return alignment;
  }

  /**
   * Returns the underline flag.
   * @return the underline flag.
   */
  public boolean getUnderline() {
    return underline;
  }

  /**
   * Returns the strikeout flag.
   * @return the strikeout flag.
   */
  public boolean getStrikeout() {
    return strikeout;
  }

  private FontData defensiveCopy( FontData fontData ) {
    return fontData == null ? null : new FontData( fontData.getName(),
                                                   fontData.getHeight(),
                                                   fontData.getStyle() );
  }

  private RGB defensiveCopy( RGB rgb ) {
    return rgb == null ? null : new RGB( rgb.red, rgb.green, rgb.blue );
  }
}