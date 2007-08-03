/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * Displays the page number and page count within the context of a {@link PagePrint}.  To properly display page
 * numbers, instances of this class should be created using the {@link PageNumber} argument which is passed to the
 * {@link PageDecoration#createPrint(PageNumber)} method by PagePrint.
 * <p>
 * PageNumberPrints are never greedy with layout space, even with center- or right-alignment.  (Greedy
 * prints take up all the available space on the page.)  Therefore, when center- or right-alignment
 * is required, it is necessary to wrap the page number in a Print which will enforce the same alignment.
 * Usually this is a center:default:grow or right:default:grow column in a GridPrint.
 * 
 * @author Matthew Hall
 * @see PagePrint
 * @see PageDecoration
 * @see PageNumber
 * @see PageNumberFormat
 * @see DefaultPageNumberFormat
 */
public class PageNumberPrint implements Print {
  /** The default font data for a PageNumberPrint.  Value is device-dependent. */
  public static final FontData DEFAULT_FONT_DATA = new FontData ();

  /** The default alignment for a PageNumberPrint.  Value is SWT.LEFT. */
  public static final int DEFAULT_ALIGN = SWT.LEFT;

  PageNumber pageNumber;
  FontData fontData;
  int align;
  RGB rgb;
  PageNumberFormat format;

  /**
   * Constructs a PageNumberPrint for the given page number.
   * @param pageNumber the page number of the page this Print will appear on.
   */
  public PageNumberPrint (PageNumber pageNumber) {
    this (pageNumber, DEFAULT_FONT_DATA, DEFAULT_ALIGN);
  }

  /**
   * Constructs a PageNumberPrint for the given page number and font.
   * @param pageNumber the page number of the page this Print will appear on.
   * @param fontData the font that this Print will appear in.
   */
  public PageNumberPrint (PageNumber pageNumber, FontData fontData) {
    this (pageNumber, fontData, DEFAULT_ALIGN);
  }

  /**
   * Constructs a PageNumberPrint for the given page number and alignment.
   * @param pageNumber the page number of the page this Print will appear on.
   * @param align the horizontal alignment of the text.
   */
  public PageNumberPrint (PageNumber pageNumber, int align) {
    this(pageNumber, DEFAULT_FONT_DATA, align);
  }

  /**
   * Constructs a PageNumberPrint for the given page number, font and alignment.
   * @param pageNumber the page number of the page this Print will appear on.
   * @param fontData the font that this Print will appear in.
   * @param align the horizontal alignment of the text.
   */
  public PageNumberPrint (PageNumber pageNumber, FontData fontData, int align) {
    setPageNumber (pageNumber);
    setFontData (fontData);
    setAlign (align);
    setRGB (new RGB (0, 0, 0));
    setPageNumberFormat(new DefaultPageNumberFormat());
  }

  /**
   * Sets the page number to the argument.
   * @param pageNumber the new page number.
   */
  public void setPageNumber (PageNumber pageNumber) {
    if (pageNumber == null)
      throw new NullPointerException();
    this.pageNumber = pageNumber;
  }

  /**
   * Returns the page number of this Print.
   * @return the page number of this Print.
   */
  public PageNumber getPageNumber () {
    return pageNumber;
  }

  /**
   * Sets the text font to the argument.
   * @param fontData the new text font.
   */
  public void setFontData (FontData fontData) {
    if (fontData == null)
      throw new NullPointerException();
    this.fontData = fontData;
  }

  /**
   * Returns the text font.
   * @return the text font.
   */
  public FontData getFontData () {
    return fontData;
  }

  /**
   * Sets the horizontal text alignment to the argument.
   * @param align the horizontal alignment. Must be one of {@link SWT#LEFT },
   *          {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public void setAlign (int align) {
    this.align = checkAlign (align);
  }

  /**
   * Returns the horizontal text alignment.
   * @return the horizontal text alignment.
   */
  public int getAlign () {
    return align;
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
   * Sets the format that will be used to convert the page number to a text
   * string.
   * @param format the new page number format.
   */
  public void setPageNumberFormat (PageNumberFormat format) {
    if (format == null)
      throw new NullPointerException();
    this.format = format;
  }

  /**
   * Returns the page number format. This property determines how the PageNumber
   * will be converted into a String representing the page number. The default
   * value of this property formats page numbers as follows:<br>
   * 
   * <pre>
   * Page 1 of 5
   * </pre>
   * 
   * @return the page number format.
   */
  public PageNumberFormat getPageNumberFormat () {
    return format;
  }

  /**
   * Sets the text color.
   * @param foreground the new text color.
   */
  public void setRGB (RGB foreground) {
    if (foreground == null)
      throw new NullPointerException();
    this.rgb = foreground;
  }

  /**
   * Returns the text color.
   * @return the text color.
   */
  public RGB getRGB () {
    return rgb;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new PageNumberIterator (this, device, gc);
  }
}

class PageNumberIterator extends AbstractIterator {
  final PageNumber pageNumber;
  final FontData fontData;
  final int align;
  final RGB rgb;
  final PageNumberFormat format;
  final Point size;

  boolean hasNext = true;

  PageNumberIterator (PageNumberPrint print, Device device, GC gc) {
    super (device, gc);

    this.pageNumber = print.pageNumber;
    this.fontData = print.fontData;
    this.align = print.align;
    this.rgb = print.rgb;
    this.format = print.format;

    // Calculate the size for the largest possible page number string.
    Font oldFont = gc.getFont ();
    Font font = null;
    try {
      font = new Font (device, fontData);
      gc.setFont (font);

      size = gc.textExtent (format.format (new PageNumber () {
        public int getPageCount () { return 9999; }
        public int getPageNumber() { return 9998; } // (zero-based index) 
      }));
    } finally {
      gc.setFont (oldFont);
      if (font != null) font.dispose ();
    }
  }

  PageNumberIterator (PageNumberIterator that) {
    super (that);
    this.pageNumber = that.pageNumber;
    this.fontData = that.fontData;
    this.align = that.align;
    this.rgb = that.rgb;
    this.format = that.format;
    this.size = that.size;
    this.hasNext = that.hasNext;
  }

  public boolean hasNext () {
    return hasNext;
  }

  public Point minimumSize () {
    return size;
  }

  public Point preferredSize () {
    return size;
  }

  public PrintPiece next (int width, int height) {
    if (width < size.x || height < size.y) return null;

    Point size = new Point(this.size.x, this.size.y);
    if (align == SWT.CENTER || align == SWT.RIGHT)
    	size.x = width;

    PageNumberPiece piece = new PageNumberPiece (this, size);
    hasNext = false;

    return piece;
  }

  public PrintIterator copy () {
    return new PageNumberIterator (this);
  }
}

class PageNumberPiece extends AbstractPiece {
  private final PageNumber pageNumber;
  private final FontData fontData;
  private final int align;
  private final PageNumberFormat format;
  private final RGB rgb;

  private Font font;
  private Color foreground;

  PageNumberPiece (PageNumberIterator iter, Point size) {
    super (iter, size);
    this.pageNumber = iter.pageNumber;
    this.fontData = iter.fontData;
    this.align = iter.align;
    this.format = iter.format;
    this.rgb = iter.rgb;
  }

  private Font getFont() {
    if (font == null)
      font = new Font(device, fontData);
    return font;
  }

  private Color getForeground() {
    if (foreground == null)
      foreground = new Color(device, rgb);
    return foreground;
  }

  public void paint (GC gc, int x, int y) {
    Font oldFont = gc.getFont ();
    Color oldForeground = gc.getForeground ();

    Point size = getSize ();

    try {
      gc.setFont (getFont());
      gc.setForeground (getForeground());

      String text = format.format (pageNumber);
      Point textSize = gc.textExtent (text);

      // Adjust x for alignment.
      switch (align) {
      case SWT.CENTER:
        x = x + (size.x - textSize.x) / 2;
        break;
      case SWT.RIGHT:
        x = x + size.x - textSize.x;
        break;
      default:
        break;
      }

      // Draw the page number.
      gc.drawText (text, x, y, true);
    } finally {
      gc.setFont (oldFont);
      gc.setForeground (oldForeground);
    }
  }

  public void dispose () {
    if (font != null) {
      font.dispose();
      font = null;
    }
    if (foreground != null) {
      foreground.dispose();
      foreground = null;
    }
  }
}