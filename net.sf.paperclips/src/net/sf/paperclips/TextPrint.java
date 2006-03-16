/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print for displaying text.
 * @author Matthew
 */
public class TextPrint implements Print {
  /** The default text for a TextPrint. Value is "". */
  public static final String DEFAULT_TEXT = "";

  /** The default font data for a TextPrint. Value is Times 10-point normal. */
  public static final FontData DEFAULT_FONT_DATA = new FontData ("Times", 10,
      SWT.NORMAL);

  /** The default alignment for TextPrint. Value is SWT.LEFT. */
  public static final int DEFAULT_ALIGN = SWT.LEFT;

  String text;

  FontData fontData;

  int align;

  RGB rgb;

  /**
   * Constructs a TextPrint with the default properties.
   */
  public TextPrint () {
    this (DEFAULT_TEXT, DEFAULT_FONT_DATA, DEFAULT_ALIGN);
  }

  /**
   * Constructs a TextPrint with the given text.
   * @param text the text to print.
   */
  public TextPrint (String text) {
    this (text, DEFAULT_FONT_DATA, DEFAULT_ALIGN);
  }

  /**
   * Constructs a TextPrint with the given text and font data.
   * @param text the text to print.
   * @param fontData the font that will be used to print the text.
   */
  public TextPrint (String text, FontData fontData) {
    this (text, fontData, DEFAULT_ALIGN);
  }

  /**
   * Constructs a TextPrint with the give text and alignment.
   * @param text the text to print.
   * @param align the horizontal text alignment. Must be one of
   *          {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public TextPrint (String text, int align) {
    this (text, DEFAULT_FONT_DATA, align);
  }

  /**
   * Constructs a TextPrint with the given text, font data, and alignment.
   * @param text the text to print.
   * @param fontData the font that will be used to print the text.
   * @param align the horizontal text alignment. Must be one of
   *          {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public TextPrint (String text, FontData fontData, int align) {
    setText (text);
    setFontData (fontData);
    setAlign (align);
    setRGB (new RGB (0, 0, 0));
  }

  /**
   * Sets the text that will be printed.
   * @param text the text to print.
   */
  public void setText (String text) {
    this.text = BeanUtils.checkNull (text);
  }

  /**
   * Sets the font that will be used to print the text.
   * @param fontData the font that will be used to print the text.
   */
  public void setFontData (FontData fontData) {
    this.fontData = BeanUtils.checkNull (fontData);
  }

  /**
   * Sets the horizontal text alignment.
   * @param align the horizontal text alignment. Must be one of
   *          {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public void setAlign (int align) {
    this.align = checkAlign (align);
  }

  private int checkAlign (int align) {
    if ((align & SWT.LEFT) == SWT.LEFT)
      return SWT.LEFT;
    else if ((align & SWT.CENTER) == SWT.CENTER)
      return SWT.CENTER;
    else if ((align & SWT.RIGHT) == SWT.RIGHT) return SWT.RIGHT;

    // no alignment bit--default to left.
    return SWT.LEFT;
  }

  /**
   * Returns the text that will be printed.
   * @return the text that will be printed.
   */
  public String getText () {
    return text;
  }

  /**
   * Returns the font that will be used to print the text.
   * @return the font that will be used to print the text.
   */
  public FontData getFontData () {
    return fontData;
  }

  /**
   * Returns the horizontal text alignment.
   * @return the horizontal text alignment.
   */
  public int getAlign () {
    return align;
  }

  /**
   * Sets the text color to the argument.
   * @param foreground the new text color.
   */
  public void setRGB (RGB foreground) {
    this.rgb = BeanUtils.checkNull (foreground);
  }

  /**
   * Returns the text color.
   * @return the text color.
   */
  public RGB getRGB () {
    return rgb;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new TextIterator (this, device, gc);
  }

  /**
   * Returns "PaperClips print job". This method is invoked by PrintUtil to
   * determine the name of the print job. Override this method to change this
   * default.
   */
  @Override
  public String toString () {
    return "PaperClips print job";
  }
}

class TextIterator extends AbstractIterator {
  final String text;

  final String[] lines;

  final FontData fontData;

  final int align;

  final RGB rgb;

  // These are the cursor.
  int row;

  int col;

  TextIterator (TextPrint print, Device device, GC gc) {
    super (device, gc);
    this.text = print.text;
    this.lines = print.text.split ("(\r)?\n");
    this.fontData = print.fontData;
    this.align = print.align;
    this.rgb = print.rgb;

    this.row = 0;
    this.col = 0;
  }

  TextIterator (TextIterator that) {
    super (that);

    this.text = that.text;
    this.lines = that.lines.clone ();
    this.fontData = that.fontData;
    this.align = that.align;
    this.rgb = that.rgb;

    this.row = that.row;
    this.col = that.col;
  }

  public boolean hasNext () {
    return row < lines.length;
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    Font font = null;
    Font font_old = gc.getFont ();

    try {
      gc.setFont (font = new Font (device, fontData));

      FontMetrics fm = gc.getFontMetrics ();

      // Check line height
      int lineHeight = fm.getHeight ();
      if (lineHeight > height) return null;

      // Determine maximum number of lines that could fit in next PrintPiece.
      final int maxLines = height / lineHeight;

      // Largest pixel width of each line.
      int maxWidth = 0;

      // Keep a list of each line that will be printed as we calculate it.
      List <String> nextLines = new ArrayList <String> ();

      while ((nextLines.size () < maxLines) && (row < lines.length)) {
        // Find out how much text will fit on one line.
        String line = lines[row].substring (col);
        int charCount = lineBreak (gc, line, width);

        if (line.length () > 0 && charCount == 0) return null;

        String thisLine = line.substring (0, charCount);
        maxWidth = Math.max (maxWidth, gc.stringExtent (thisLine).x);

        // Get the text that fits on this line.
        nextLines.add (thisLine);

        // Remove the text we used from this row.
        col += charCount;
        while ( // And skip any following whitespace.
        col < lines[row].length ()
            && Character.isWhitespace (lines[row].charAt (col))) {
          col++;
        }

        // If we've used all the text for this row (in the lines array),
        // advance to next row.
        if (col >= lines[row].length ()) {
          row++;
          col = 0;
        }
      }

      return new TextPiece (device, new Point (maxWidth, nextLines.size ()
          * lineHeight), this, nextLines
          .toArray (new String[nextLines.size ()]));
    } finally {
      gc.setFont (font_old);
      if (font != null) font.dispose ();
    }
  }

  public Point minimumSize () {
    return maxExtent (text.split ("\\s"));
  }

  public Point preferredSize () {
    return maxExtent (lines);
  }

  Point maxExtent (String[] text) {
    Font font = null;
    Font font_old = gc.getFont ();
    try {
      font = new Font (device, fontData);
      gc.setFont (font);

      FontMetrics fm = gc.getFontMetrics ();
      int maxWidth = 0;

      for (String textPiece : text)
        maxWidth = Math.max (maxWidth, gc.stringExtent (textPiece).x);

      return new Point (maxWidth, fm.getHeight ());
    } finally {
      if (font != null) font.dispose ();
      gc.setFont (font_old);
    }
  }

  int lineBreak (GC gc, String text, int width) {
    // Offsets within the string
    int loIndex = 0;
    int hiIndex = text.length ();

    // Pixel width of entire string
    int pixelWidth = gc.stringExtent (text).x;

    // Does the whole string fit?
    if (pixelWidth <= width)
    // I'll take it
      return hiIndex;

    // Do a binary search to find the maximum characters that will fit within
    // the given width.
    while (loIndex < hiIndex) {
      int midIndex = (loIndex + hiIndex + 1) / 2;
      int midWidth = gc.stringExtent (text.substring (0, midIndex)).x;

      if (midWidth < width)
        // don't add 1, the next character could make it too big
        loIndex = midIndex;
      else if (midWidth > width)
        // subtract 1, we already know midIndex makes it too big
        hiIndex = midIndex - 1;
      else {
        // perfect fit
        loIndex = hiIndex = midIndex;
      }
    }

    return wordBreak (text, loIndex);
  }

  int wordBreak (String text, int maxLength) {
    // If the max length is the string length, no break
    // (we mainly check this to avoid an exception in for-loop)
    if (maxLength == text.length ()) return maxLength;

    // Otherwise, break string at the last whitespace at or before maxLength.
    for (int i = maxLength; i >= 0; i--)
      if (Character.isWhitespace (text.charAt (i))) return i;

    // No whitespace? Break at max length
    return maxLength;
  }

  public PrintIterator copy () {
    return new TextIterator (this);
  }
}

class TextPiece extends AbstractPiece {
  final String[] lines;

  final FontData fontData;

  final int align;

  final RGB rgb;

  TextPiece (Device device, Point size, TextIterator iter, String[] text) {
    super (iter, size);
    this.lines = text;
    this.fontData = iter.fontData;
    this.align = iter.align;
    this.rgb = iter.rgb;
  }

  public void paint (GC gc, int x, int y) {
    Font font = null;
    Font font_old = gc.getFont ();

    Color fg = null;
    Color fg_old = gc.getForeground ();

    Point size = getSize ();

    try {
      gc.setFont (font = new Font (device, fontData));
      gc.setForeground (fg = new Color (device, rgb));

      final int lineHeight = gc.getFontMetrics ().getHeight ();
      int lineX = x;

      for (int i = 0; i < lines.length; i++) {
        String line = lines[i];

        if (align != SWT.LEFT) {
          int pixelWidth = gc.stringExtent (line).x;
          if (align == SWT.CENTER)
            lineX = x + (size.x - pixelWidth) / 2;
          else if (align == SWT.RIGHT) lineX = x + size.x - pixelWidth;
        }

        gc.drawString (lines[i], lineX, y + lineHeight * i, true);
      }
    } finally {
      gc.setFont (font_old);
      gc.setForeground (fg_old);
      if (font != null) font.dispose ();
      if (fg != null) fg.dispose ();
    }
  }

  public void dispose () {
  // Nothing to dispose
  }
}