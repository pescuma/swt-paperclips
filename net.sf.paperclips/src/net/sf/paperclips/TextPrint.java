/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print for displaying text.
 * <p>
 * TextPrints are never greedy with layout space, even with center- or right-alignment. (Greedy prints take
 * up all the available space on the page.) Therefore, when center- or right-alignment is required, it is
 * necessary to wrap the text in a Print which will enforce the same alignment. Usually this is a
 * center:default:grow or right:default:grow column in a GridPrint.
 * @author Matthew Hall
 */
public class TextPrint implements Print {
  /** The default text for a TextPrint. Value is "". */
  public static final String     DEFAULT_TEXT      = "";

  /** The default font data for a TextPrint. Value is device-dependent. */
  public static final FontData   DEFAULT_FONT_DATA = new FontData();

  /** The default alignment for TextPrint. Value is SWT.LEFT. */
  public static final int        DEFAULT_ALIGN     = SWT.LEFT;

  private static final TextStyle DEFAULT_STYLE     = new TextStyle();

  String                         text;
  TextStyle                      style;
  boolean                        wordSplitting;

  /**
   * Constructs a TextPrint with the default properties.
   */
  public TextPrint() {
    this( DEFAULT_TEXT );
  }

  /**
   * Constructs a TextPrint with the given text.
   * @param text the text to print.
   */
  public TextPrint( String text ) {
    this( text, DEFAULT_STYLE );
  }

  /**
   * Constructs a TextPrint with the given text and font data.
   * @param text the text to print.
   * @param fontData the font that will be used to print the text.
   */
  public TextPrint( String text, FontData fontData ) {
    this( text, DEFAULT_STYLE.font( fontData ) );
  }

  /**
   * Constructs a TextPrint with the give text and alignment.
   * @param text the text to print.
   * @param align the horizontal text alignment. Must be one of {@link SWT#LEFT }, {@link SWT#CENTER } or
   *        {@link SWT#RIGHT }.
   */
  public TextPrint( String text, int align ) {
    this( text, DEFAULT_STYLE.align( align ) );
  }

  /**
   * Constructs a TextPrint with the given text, font data, and alignment.
   * @param text the text to print.
   * @param fontData the font that will be used to print the text.
   * @param align the horizontal text alignment. Must be one of {@link SWT#LEFT }, {@link SWT#CENTER } or
   *        {@link SWT#RIGHT }.
   */
  public TextPrint( String text, FontData fontData, int align ) {
    this( text, DEFAULT_STYLE.font( fontData ).align( align ) );
  }

  /**
   * Constructs a TextPrint with the given text and style.
   * @param text the text to print.
   * @param style the style to apply to the text.
   */
  public TextPrint( String text, TextStyle style ) {
    if ( text == null || style == null )
      throw new NullPointerException();
    this.text = text;
    this.style = style;
    this.wordSplitting = true;
  }

  /**
   * Returns the text that will be printed.
   * @return the text that will be printed.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text that will be printed.
   * @param text the text to print.
   */
  public void setText( String text ) {
    if ( text == null )
      throw new NullPointerException();
    this.text = text;
  }

  /**
   * Returns the text style.
   * @return the text style.
   */
  public TextStyle getStyle() {
    return style;
  }

  /**
   * Sets the text style to the argument.
   * @param style the new text style.
   */
  public void setStyle( TextStyle style ) {
    if ( style == null )
      throw new NullPointerException();
    this.style = style;
  }

  /**
   * Returns the font that will be used to print the text.
   * @return the font that will be used to print the text.
   */
  public FontData getFontData() {
    return style.getFontData();
  }

  /**
   * Sets the font that will be used to print the text.
   * @param fontData the font that will be used to print the text.
   */
  public void setFontData( FontData fontData ) {
    style = style.font( fontData );
  }

  /**
   * Returns the horizontal text alignment. Possible values include {@link SWT#LEFT}, {@link SWT#CENTER} or
   * {@link SWT#RIGHT}.
   * @return the horizontal text alignment.
   * @deprecated Use {@link #getAlignment()} instead.
   */
  public int getAlign() {
    return style.getAlignment();
  }

  /**
   * Sets the horizontal text alignment.
   * @param alignment the horizontal text alignment. Must be one of {@link SWT#LEFT}, {@link SWT#CENTER} or
   *        {@link SWT#RIGHT}.
   * @deprecated Use {@link #setAlignment(int)} instead.
   */
  public void setAlign( int alignment ) {
    setAlignment( alignment );
  }

  /**
   * Returns the horizontal text alignment. Possible values include {@link SWT#LEFT}, {@link SWT#CENTER} or
   * {@link SWT#RIGHT}.
   * @return the horizontal text alignment.
   */
  public int getAlignment() {
    return style.getAlignment();
  }

  /**
   * Sets the horizontal text alignment.
   * @param alignment the horizontal text alignment. Must be one of {@link SWT#LEFT}, {@link SWT#CENTER} or
   *        {@link SWT#RIGHT}.
   */
  public void setAlignment( int alignment ) {
    style = style.align( alignment );
  }

  /**
   * Returns the foreground color.
   * @return the foreground color.
   * @deprecated Use {@link #getForeground()} instead.
   */
  public RGB getRGB() {
    return getForeground();
  }

  /**
   * Sets the foreground color to the argument.
   * @param foreground the new foreground color.
   * @deprecated Use {@link #setForeground(RGB)} instead.
   */
  public void setRGB( RGB foreground ) {
    setForeground( foreground );
  }

  /**
   * Returns the foreground color. A null value indicates that the foreground color is inherited.
   * @return the foreground color.
   */
  public RGB getForeground() {
    return style.getForeground();
  }

  /**
   * Sets the foreground color to the argument.
   * @param foreground the new foreground color. A null value causes the foreground color to be inherited.
   */
  public void setForeground( RGB foreground ) {
    style = style.foreground( foreground );
  }

  /**
   * Returns the background color. A null value indicates that the background is transparent.
   * @return the background color.
   */
  public RGB getBackground() {
    return style.getBackground();
  }

  /**
   * Sets the background color to the argument.
   * @param background the new background color. A null value causes the background to be transparent.
   */
  public void setBackground( RGB background ) {
    style = style.background( background );
  }

  /**
   * Returns the underline flag.
   * @return the underline flag.
   */
  public boolean getUnderline() {
    return style.getUnderline();
  }

  /**
   * Sets the underline flag to the argument.
   * @param underline the underline flag.
   */
  public void setUnderline( boolean underline ) {
    style = style.underline( underline );
  }

  /**
   * Returns the strikout flag.
   * @return the strikout flag.
   */
  public boolean getStrikeout() {
    return style.getStrikeout();
  }

  /**
   * Sets the strikeout flag to the argument.
   * @param strikeout the strikeout flag.
   */
  public void setStrikeout( boolean strikeout ) {
    style = style.strikeout( strikeout );
  }

  /**
   * Returns whether word splitting is enabled. Default is true.
   * @return whether word splitting is enabled.
   */
  public boolean getWordSplitting() {
    return wordSplitting;
  }

  /**
   * Sets whether word splitting is enabled.
   * @param wordBreaking whether to allow word splitting.
   */
  public void setWordSplitting( boolean wordBreaking ) {
    this.wordSplitting = wordBreaking;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return new TextIterator( this, device, gc );
  }
}

class TextIterator extends AbstractIterator {
  final String    text;
  final String[]  lines;
  final TextStyle style;
  final boolean   wordSplitting;

  int             row;
  int             col;

  TextIterator( TextPrint print, Device device, GC gc ) {
    super( device, gc );
    this.text = print.text;
    this.lines = print.text.split( "(\r)?\n" );
    this.style = print.style;
    this.wordSplitting = print.wordSplitting;

    this.row = 0;
    this.col = 0;
  }

  TextIterator( TextIterator that ) {
    super( that );

    this.text = that.text;
    this.lines = (String[]) that.lines.clone();
    this.style = that.style;
    this.wordSplitting = that.wordSplitting;

    this.row = that.row;
    this.col = that.col;
  }

  public boolean hasNext() {
    return row < lines.length;
  }

  public PrintPiece next( int width, int height ) {
    if ( !hasNext() )
      throw new IllegalStateException();

    Font font = null;
    Font font_old = gc.getFont();

    try {
      FontData fontData = style.getFontData();
      if ( fontData != null )
        gc.setFont( font = new Font( device, fontData ) );

      FontMetrics fm = gc.getFontMetrics();

      // Check line height
      final int lineHeight = fm.getHeight();
      if ( lineHeight > height )
        return null;

      // Determine maximum number of lines that could fit in next PrintPiece.
      final int maxLines = height / lineHeight;

      String[] nextLines = nextLines( width, maxLines );
      if ( nextLines.length == 0 )
        return null;

      int maxWidth = maxExtent( nextLines ).x;

      int ascent = fm.getAscent() + fm.getLeading();

      return new TextPiece( device,
                            new Point( maxWidth, nextLines.length * lineHeight ),
                            this,
                            nextLines,
                            ascent );
    }
    finally {
      if ( font != null ) {
        gc.setFont( font_old );
        font.dispose();
      }
    }
  }

  private String[] nextLines( final int width, final int maxLines ) {
    List nextLines = new ArrayList( Math.min( lines.length, maxLines ) );

    while ( ( nextLines.size() < maxLines ) && ( row < lines.length ) ) {
      String line = lines[row].substring( col );

      // Find out how much text will fit on one line.
      int charCount = findLineBreak( gc, line, width );

      // If none of the text could fit in the current line, terminate this iteration.
      if ( line.length() > 0 && charCount == 0 )
        break;

      // Get the text that fits on this line.
      String thisLine = line.substring( 0, charCount );
      nextLines.add( thisLine );

      // Move cursor past the text we just consumed.
      col += charCount;

      skipWhitespace();

      advanceToNextRowIfCurrentRowCompleted();
    }

    return (String[]) nextLines.toArray( new String[nextLines.size()] );
  }

  private void skipWhitespace() {
    while ( col < lines[row].length() && Character.isWhitespace( lines[row].charAt( col ) ) )
      col++;
  }

  private void advanceToNextRowIfCurrentRowCompleted() {
    if ( col >= lines[row].length() ) {
      row++;
      col = 0;
    }
  }

  public Point minimumSize() {
    return maxExtent( text.split( "\\s" ) );
  }

  public Point preferredSize() {
    return maxExtent( lines );
  }

  Point maxExtent( String[] text ) {
    Font font = null;
    Font font_old = gc.getFont();
    try {
      FontData fontData = style.getFontData();
      if ( fontData != null )
        gc.setFont( font = new Font( device, fontData ) );

      FontMetrics fm = gc.getFontMetrics();
      int maxWidth = 0;

      for ( int i = 0; i < text.length; i++ ) {
        String textPiece = text[i];
        maxWidth = Math.max( maxWidth, gc.stringExtent( textPiece ).x );
      }

      return new Point( maxWidth, fm.getHeight() );
    }
    finally {
      if ( font != null ) {
        gc.setFont( font_old );
        font.dispose();
      }
    }
  }

  int findLineBreak( GC gc, String text, int width ) {
    // Offsets within the string
    int loIndex = 0;
    int hiIndex = text.length();

    // Pixel width of entire string
    int pixelWidth = gc.stringExtent( text ).x;

    // Does the whole string fit?
    if ( pixelWidth <= width )
      // I'll take it
      return hiIndex;

    // Do a binary search to find the maximum characters that will fit within the given width.
    while ( loIndex < hiIndex ) {
      int midIndex = ( loIndex + hiIndex + 1 ) / 2;
      int midWidth = gc.stringExtent( text.substring( 0, midIndex ) ).x;

      if ( midWidth < width )
        // don't add 1, the next character could make it too big
        loIndex = midIndex;
      else if ( midWidth > width )
        // subtract 1, we already know midIndex makes it too big
        hiIndex = midIndex - 1;
      else {
        // perfect fit
        loIndex = hiIndex = midIndex;
      }
    }

    return findWordBreak( text, loIndex );
  }

  int findWordBreak( String text, int maxLength ) {
    // If the max length is the string length, no break
    // (we mainly check this to avoid an exception in for-loop)
    if ( maxLength == text.length() )
      return maxLength;

    // Otherwise, break string at the last whitespace at or before maxLength.
    for ( int i = maxLength; i >= 0; i-- )
      if ( Character.isWhitespace( text.charAt( i ) ) )
        return i;

    // No whitespace? Break at max length (if word breaking is allowed)
    if ( wordSplitting )
      return maxLength;

    return 0;
  }

  public PrintIterator copy() {
    return new TextIterator( this );
  }
}