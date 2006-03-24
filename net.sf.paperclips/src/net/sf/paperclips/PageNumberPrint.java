/*
 * Created on Oct 19, 2005
 */
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
 * A print for displaying a page number.
 * @author Matthew
 */
public class PageNumberPrint implements Print {
  /**
   * The default font data for a PageNumberPrint. Value is Times 10-point
   * normal.
   */
  public static final FontData DEFAULT_FONT_DATA = new FontData ("Times", 10,
      SWT.NORMAL);

  /**
   * The default alignment for a PageNumberPrint. Value is SWT.LEFT.
   */
  public static final int DEFAULT_ALIGN = SWT.LEFT;

  PageNumber pageNumber;

  FontData fontData;

  int align;

  RGB rgb;

  /* Default format. */
  PageNumberFormat format = new PageNumberFormat () {
    public String format (PageNumber pageNumber) {
      return "Page " + (pageNumber.getPageNumber () + 1) + " of "
          + pageNumber.getPageCount ();
    }
  };

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
  }

  /**
   * Sets the page number to the argument.
   * @param pageNumber the new page number.
   */
  public void setPageNumber (PageNumber pageNumber) {
    this.pageNumber = BeanUtils.checkNull (pageNumber);
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
    this.fontData = BeanUtils.checkNull (fontData);
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
    this.format = BeanUtils.checkNull (format);
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
        public int getPageCount () {
          return 9999;
        }

        public int getPageNumber () {
          return 9999;
        }
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

    return new PageNumberPiece (this);
  }

  public PrintIterator copy () {
    return new PageNumberIterator (this);
  }
}

class PageNumberPiece extends AbstractPiece {
  final PageNumber pageNumber;

  final FontData fontData;

  final int align;

  final PageNumberFormat format;

  final RGB rgb;

  PageNumberPiece (PageNumberIterator iter) {
    super (iter, iter.size);
    this.pageNumber = iter.pageNumber;
    this.fontData = iter.fontData;
    this.align = iter.align;
    this.format = iter.format;
    this.rgb = iter.rgb;
  }

  public void paint (GC gc, int x, int y) {
    Font oldFont = gc.getFont ();
    Color oldForeground = gc.getForeground ();

    Font font = null;
    Color foreground = null;

    Point size = getSize ();

    try {
      font = new Font (device, fontData);
      foreground = new Color (device, rgb);

      gc.setFont (font);
      gc.setForeground (foreground);

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
      }

      // Draw the page number.
      gc.drawText (text, x, y);
    } finally {
      gc.setFont (oldFont);
      gc.setForeground (oldForeground);

      if (font != null) font.dispose ();
      if (foreground != null) foreground.dispose ();
    }
  }

  public void dispose () {
  // Nothing to dispose
  }
}