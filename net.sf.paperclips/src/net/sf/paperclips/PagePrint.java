/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator Print which displays page headers and footers, with page numbering capabilities.
 * @author Matthew
 */
public class PagePrint implements Print {
  Print body;

  PageDecoration header;

  PageDecoration footer;

  int headerGap = 1; // in points

  int footerGap = 1; // in points

  /**
   * Constructs a PagePrint with the given body, header and footer.
   * @param body the Print being decorated.
   * @param header a PageDecoration for creating the header. May be null.
   * @param footer a PageDecoration for creating the footer. may be null.
   */
  public PagePrint (Print body, PageDecoration header, PageDecoration footer) {
    this.body = BeanUtils.checkNull (body);
    this.header = header;
    this.footer = footer;
  }

  private int checkGap (int gap) {
    if (gap < 0)
      throw new IllegalArgumentException ("Gap must be >= 0 (value is " + gap
          + ")");
    return gap;
  }

  /**
   * Sets the page body to the argument.
   * @param body the new page body.
   */
  public void setBody (Print body) {
    this.body = BeanUtils.checkNull (body);
  }

  /**
   * Sets the page header to the argument.
   * @param header a PageDecoration which creates the header. May be null.
   */
  public void setHeader (PageDecoration header) {
    this.header = header;
  }

  /**
   * Sets the gap between the header and body to the argument, expressed in
   * points.
   * @param points the new gap between the header and body (if there is a header).
   */
  public void setHeaderGap (int points) {
    this.headerGap = checkGap (points);
  }

  /**
   * Sets the page footer to the argument.
   * @param footer a PageDecoration which creates the footer. May be null.
   */
  public void setFooter (PageDecoration footer) {
    this.footer = footer;
  }

  /**
   * Sets the gap between the body and footer to the argument, expressed in
   * points.
   * @param points the new gap between the body and footer (if there is a footer).
   */
  public void setFooterGap (int points) {
    this.footerGap = checkGap (points);
  }

  /**
   * Returns the page body.
   * @return the page body.
   */
  public Print getBody () {
    return body;
  }

  /**
   * Returns the page header.
   * @return the page header.
   */
  public PageDecoration getHeader () {
    return header;
  }

  /**
   * Returns the gap between the header and body, expressed in points.
   * @return the gap between the header and body, expressed in points.
   */
  public int getHeaderGap () {
    return headerGap;
  }

  /**
   * Returns the page footer.
   * @return the page footer.
   */
  public PageDecoration getFooter () {
    return footer;
  }

  /**
   * Returns the gap between the body and footer, expressed in points.
   * @return the gap between the body and footer, expressed in points.
   */
  public int getFooterGap () {
    return footerGap;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new PageIterator (this, device, gc);
  }
}

class PageNumberer {
  int pageCount = 0;

  synchronized PageNumber next () {
    return new InnerPageNumber ();
  }

  class InnerPageNumber implements PageNumber {
    final int pageNumber = pageCount++; // POST-increment

    public int getPageCount () {
      return pageCount;
    }

    public int getPageNumber () {
      return pageNumber;
    }
  }

  @Override
  public PageNumberer clone () {
    PageNumberer result = new PageNumberer ();
    result.pageCount = this.pageCount;
    return result;
  }
}

class PageIterator implements PrintIterator {
  final Device device;

  final GC gc;

  final Point dpi;

  final PrintIterator body;

  final PageDecoration header;

  final int headerGap; // pixels

  final PageDecoration footer;

  final int footerGap; // pixels

  final PageNumberer numberer;

  final Point minimumSize;

  final Point preferredSize;

  PageIterator (PagePrint print, Device device, GC gc) {
    this.device = device;
    this.gc = gc;
    this.dpi = device.getDPI ();

    body = print.body.iterator (device, gc);
    header = print.header;
    headerGap = header == null ? 0 : print.headerGap * dpi.y / 72;
    footer = print.footer;
    footerGap = footer == null ? 0 : print.footerGap * dpi.y / 72;

    // Calculate the minimum and preferred size.
    Point bodyMinSize = body.minimumSize ();
    Point bodyPrefSize = body.preferredSize ();

    PageNumber samplePageNumber = new PageNumber () {
      public int getPageCount () {
        return 9999;
      }

      public int getPageNumber () {
        return 9999;
      }
    };

    if (header != null) {
      PrintIterator iter = header.createPrint (samplePageNumber).iterator (
          device, gc);

      bodyMinSize.y += headerGap;
      bodyPrefSize.y += headerGap;

      Point minSize = iter.minimumSize ();
      bodyMinSize.y += minSize.y;
      bodyMinSize.x = Math.max (bodyMinSize.x, minSize.x);

      Point prefSize = iter.preferredSize ();
      bodyPrefSize.y += prefSize.y;
      bodyPrefSize.x = Math.max (bodyPrefSize.x, prefSize.x);
    }

    if (footer != null) {
      PrintIterator iter = footer.createPrint (samplePageNumber).iterator (
          device, gc);

      bodyMinSize.y += footerGap;
      bodyPrefSize.y += footerGap;

      Point minSize = iter.minimumSize ();
      bodyMinSize.y += minSize.y;
      bodyMinSize.x = Math.max (bodyMinSize.x, minSize.x);

      Point prefSize = iter.preferredSize ();
      bodyPrefSize.y += prefSize.y;
      bodyPrefSize.x = Math.max (bodyPrefSize.x, prefSize.x);
    }

    this.minimumSize = bodyMinSize;
    this.preferredSize = bodyPrefSize;

    this.numberer = new PageNumberer ();
  }

  PageIterator (PageIterator that) {
    this.device = that.device;
    this.gc = that.gc;
    this.dpi = that.dpi;

    this.body = that.body.copy ();
    this.header = that.header;
    this.headerGap = that.headerGap;
    this.footer = that.footer;
    this.footerGap = that.footerGap;

    this.numberer = that.numberer.clone ();

    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;
  }

  public boolean hasNext () {
    return body.hasNext ();
  }

  public Point minimumSize () {
    return minimumSize;
  }

  public Point preferredSize () {
    return preferredSize;
  }

  PageNumber pageNumber = null;

  public PrintPiece next (int width, int height) {
    // Remembering the page number in an instance field--this way if the
    // iteration fails, the page number will be available for the next
    // iteration (so page numbers don't get skipped).
    if (pageNumber == null) pageNumber = numberer.next ();

    // y offset
    int y = 0;

    List <CompositeEntry> entries = new ArrayList <CompositeEntry> ();

    // HEADER
    if (header != null) {
      PrintPiece headerPiece = header.createPrint (pageNumber).iterator (
          device, gc).next (width, height);

      if (headerPiece == null) return null;

      entries.add (new CompositeEntry (headerPiece, new Point (0, 0)));

      y += headerPiece.getSize ().y + headerGap;
      height -= y;
    }

    // FOOTER
    if (footer != null) {
      PrintPiece footerPiece = footer.createPrint (pageNumber).iterator (
          device, gc).next (width, height);

      if (footerPiece == null) return null;

      entries.add (new CompositeEntry (footerPiece, new Point (0, y + height
          - footerPiece.getSize ().y)));

      height = height - footerPiece.getSize ().y - footerGap;
    }

    // BODY
    PrintPiece bodyPiece = body.next (width, height);

    if (bodyPiece == null) return null;

    entries.add (new CompositeEntry (bodyPiece, new Point (0, y)));

    // Compile and return page.
    PrintPiece result = new CompositePiece (entries);

    // Iteration successful. Null the pageNumber field so the next iteration
    // advances to the next page.
    pageNumber = null;

    return result;
  }

  public PrintIterator copy () {
    return new PageIterator (this);
  }
}